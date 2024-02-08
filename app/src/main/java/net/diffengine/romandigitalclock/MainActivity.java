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

public class MainActivity extends AppCompatActivity {
    private TextView txtTime;
    private View     bkgndView;
    private Handler  myHandler;

    private boolean ampm;
    private boolean ampmSeparator;
    private boolean keepon;
    private boolean onlywhencharging;

    private final   Context context = this;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private Fragment menuFragment;

    /** @noinspection ReassignedVariable*/
    private void setKeepScreenOn() {
        boolean keepScreenOn = false;
        if (keepon) {
            if (!onlywhencharging || isCharging()) {
                keepScreenOn = true;
            }
        }
        bkgndView.setKeepScreenOn(keepScreenOn);
    }

    private final Runnable updatetime = new Runnable() {
        @Override
        public void run() {
            int updatedelay_in_ms = 200;    // Must be less than the minimum screen timeout (15sec)

            txtTime.setText(romantime.now(ampm, ampmSeparator));
            setKeepScreenOn();

            myHandler.postDelayed(updatetime, updatedelay_in_ms);
        }
    };

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
    View.OnClickListener bkgndOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainMenu(View.INVISIBLE);
        }
    };

    /** @noinspection Convert2Lambda*/
    View.OnClickListener clockOCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainMenu(View.VISIBLE);
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener prefChgListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sp, String key) {
            switch( (key!=null) ? key : "null") {
                case "chkbox_format":
                    ampm = sp.getBoolean(key, false);
                    break;
                case "chkbox_ampm_separator":
                    ampmSeparator = sp.getBoolean(key, false);
                    break;
                case "chkbox_keep_on":
                    keepon = sp.getBoolean(key, false);
                    setKeepScreenOn();
                    //bkgndView.setKeepScreenOn(keepon);
                    break;
                case "chkbox_when_charging":
                    onlywhencharging = sp.getBoolean(key, false);
                    break;
                default:
                    break;
            }
        }
    };
    
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        myHandler = new Handler(Looper.getMainLooper());
        myHandler.post(updatetime);

        txtTime = findViewById(R.id.txtTime);
        txtTime.setOnClickListener(clockOCL);

        bkgndView = findViewById(R.id.main_activity_bkgnd);
        bkgndView.setOnClickListener(bkgndOCL);

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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefChgListener);
        ampm          = sp.getBoolean("chkbox_format",        false);
        ampmSeparator = sp.getBoolean("chkbox_ampm_separator",false);
        keepon        = sp.getBoolean("chkbox_keep_on",       false);
        onlywhencharging = sp.getBoolean("chkbox_when_charging", false);
        // No need to set Keep Screen On state here; it will be set each time updatetime is called.
    }

    protected void onPause() {
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }
}