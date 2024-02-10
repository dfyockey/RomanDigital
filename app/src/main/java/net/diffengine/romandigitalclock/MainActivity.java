package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView txtTime;
    private View     bkgndView;
    private Handler  myHandler;
    private Fragment menuFragment;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private final Context context = this;

    // Storage for values of options loaded from settings
    private static final Map<String, Boolean> opt = new HashMap<>();
    static {
        opt.put("chkbox_format", false);
        opt.put("chkbox_ampm_separator", false);
        opt.put("chkbox_keep_on", false);
        opt.put("chkbox_when_charging", false);
    }

    // Aliases for option keys
    private static final String ampm = "chkbox_format",
                                ampmSeparator = "chkbox_ampm_separator",
                                keepon = "chkbox_keep_on",
                                onlywhencharging = "chkbox_when_charging";

    //////////////////////////////////////////////////////////////////////

    private boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        // Are we charging / charged?
        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            return (
                    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
            );
        } else {
            // Returning `true` in this case should prevent the display from coming on
            // if it's already off, with the correct charging state being returned
            // on the next non-null batteryStatus.
            return true;
        }
    }

    private void setKeepScreenOn() {
        //noinspection DataFlowIssue
        boolean keepScreenOn = opt.get(keepon) && (!opt.get(onlywhencharging) || isCharging());
        bkgndView.setKeepScreenOn(keepScreenOn);
    }

    private final Runnable updatetime = new Runnable() {
        @Override
        public void run() {
            int updatedelay_in_ms = 200;    // Must be less than the minimum screen timeout (15sec)

            //noinspection DataFlowIssue
            txtTime.setText( romantime.now( opt.get(ampm), opt.get(ampmSeparator) ) );
            setKeepScreenOn();

            myHandler.postDelayed(updatetime, updatedelay_in_ms);
        }
    };

    //---------------------------------------------------------------

    private void MainMenu(int visible) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (visible == View.VISIBLE) {
            ft.show(menuFragment);
        } else {
            ft.hide(menuFragment);
        }

        ft.commit();
    }

    /** @noinspection Convert2Lambda*/
    private final View.OnClickListener bkgndOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainMenu(View.INVISIBLE);
        }
    };

    /** @noinspection Convert2Lambda*/
    private final View.OnClickListener clockOCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainMenu(View.VISIBLE);
        }
    };

    //---------------------------------------------------------------

    private final SharedPreferences.OnSharedPreferenceChangeListener prefChgListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        /** @noinspection ReassignedVariable*/
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sp, String key) {
            // Load changed setting value into option hashmap
            key = (key!=null) ? key : "null";
            opt.put(key, sp.getBoolean(key, false));
        }
    };

    //---------------------------------------------------------------

    private void setListeners() {
        txtTime = findViewById(R.id.txtTime);
        txtTime.setOnClickListener(clockOCL);

        bkgndView = findViewById(R.id.main_activity_bkgnd);
        bkgndView.setOnClickListener(bkgndOCL);
    }

    private void setupMainMenu(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            menuFragment = new SelectionMenu();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.SelectionMenu, menuFragment, null)
                    .hide(menuFragment)
                    .commit();
        } else if (menuFragment == null) {
            // Recover reference to menuFragment following an orientation change
            menuFragment = getSupportFragmentManager().findFragmentById(R.id.SelectionMenu);
        }
    }

    private void getSettings() {
        // Load setting values into options hashmap
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefChgListener);
        for (String key : opt.keySet()) {
            opt.put(key, sp.getBoolean(key, false));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        myHandler = new Handler(Looper.getMainLooper());
        myHandler.post(updatetime);

        setListeners();
        setupMainMenu(savedInstanceState);
        getSettings();
    }

    //---------------------------------------------------------------

    protected void onPause() {
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }
}