package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView TimeDisplay;
    private AppCompatTextView TimeDisplaySizeControl;
    private View     bkgndView;
    private Fragment menuFragment;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private final Context context = this;
    private final String UPDATE_DISPLAY = "net.diffengine.romandigitalclock.UPDATE_DISPLAY";

    // Aliases for option keys
    private static final String
            ampm = "chkbox_format",
            alignment = "chkbox_alignment",
            ampmSeparator = "chkbox_ampm_separator",
            keepon = "chkbox_keep_on",
            onlywhencharging = "chkbox_when_charging";

    // Storage for values of options loaded from settings
    private static final Map<String, Boolean> opt = new HashMap<>();
    static {
        opt.put(ampm, false);
        opt.put(alignment, false);
        opt.put(ampmSeparator, false);
        opt.put(keepon, false);
        opt.put(onlywhencharging, false);
    }

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

    /** @noinspection DataFlowIssue*/
    private void updateTimeDisplay() {
        String now = romantime.now( opt.get(ampm), opt.get(ampmSeparator), opt.get(alignment) );

        // IMPORTANT:
        // For the String returned by romantime.now to be correctly aligned in TimeDisplay textview,
        // TextDisplay.typeface MUST be set in activity_main.xml to 'monospace'

        if (TimeDisplay.getVisibility() == View.INVISIBLE) {
            if (TimeDisplaySizeControl.getTextSize() >= 200) {
                sendBroadcast(new Intent(UPDATE_DISPLAY));
            } else {
                TimeDisplay.setVisibility(View.VISIBLE);
            }
        }

        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, TimeDisplaySizeControl.getTextSize());
        TimeDisplay.setText(now);
        setKeepScreenOn();
    }

    //---------------------------------------------------------------
    /*
        Two BroadcastReceivers are needed to allow updateReceiver, which is only intended to receive
        a broadcast intent from this app itself, to be registered as RECEIVER_NOT_EXPORTED without
        interfering with the receipt of the system-broadcast ACTION_TIME_TICK intent
     */

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeDisplay();
        }
    };

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeDisplay();
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

//    /* Before adding specific cases below, uncomment this interface */
//
//    private interface Case {
//        void run();
//    }

//    /* Put specific cases here if needed and add them to optCase below */
//
//    private class ExampleCase implements Case {
//        @Override
//        public void run() {
//            // Add code to be run in this case here
//        }
//    }
//
//    private static final Map<String, Case> optCase = new HashMap<>();
//    {
//        optCase.put("preference_key", new ExampleCase());
//    }

    /* Put anything to be executed for all cases in this method */
    /** @noinspection ReassignedVariable*/
    private void execCase(SharedPreferences sp, String key) {
        // Load changed setting value into option hashmap
        key = (key!=null) ? key : "null";
        opt.put(key, sp.getBoolean(key, false));

//        // Run Case for key, if any
//        if (optCase.containsKey(key)) {
//            //noinspection DataFlowIssue
//            optCase.get(key).run();
//        }
    }

    /** @noinspection Anonymous2MethodRef, Convert2Lambda */
    // HashMaps opt and optCase are used in supporting methods above to respond
    // to preference changes in lieu of using a lengthy switch statement here
    private final SharedPreferences.OnSharedPreferenceChangeListener prefChgListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sp, String key) {
            execCase(sp, key);
        }
    };

    //---------------------------------------------------------------

    private void setListeners() {
        TimeDisplay = findViewById(R.id.TimeDisplay);
        TimeDisplay.setOnClickListener(clockOCL);

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
        // Load setting values into options hashmap and setup initial state based thereon
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefChgListener);
        for (String key : opt.keySet()) {
            execCase(sp, key);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        setListeners();
        setupMainMenu(savedInstanceState);
        getSettings();
    }

    //---------------------------------------------------------------

    protected void onPause() {
        unregisterReceiver(updateReceiver);
        unregisterReceiver(broadcastReceiver);

        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());

        TimeDisplay.setVisibility(View.INVISIBLE);

        String maxtime_fill = getString((opt.get(ampm)) ? R.string.civ_fill : R.string.mil_fill);
        TimeDisplaySizeControl = findViewById(R.id.timedisplay_size_control);
        TimeDisplaySizeControl.setText(maxtime_fill);
        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, TimeDisplaySizeControl.getTextSize());

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        ContextCompat.registerReceiver(context, updateReceiver, new IntentFilter(UPDATE_DISPLAY), ContextCompat.RECEIVER_NOT_EXPORTED);
        sendBroadcast(new Intent(UPDATE_DISPLAY));
    }
}