/*
 * MainActivity.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024 David Yockey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

    static boolean left  = false;
    static boolean right = true;

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
        opt.put(ampm, left);
        opt.put(alignment, left);
        opt.put(ampmSeparator, left);
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

    private Intent makeIntent (String action) {
        Intent i = new Intent();
            i.setAction(action);
            i.setPackage(context.getPackageName());
        return i;
    }

    int text_resize_attempt_count = 0;

    /** @noinspection DataFlowIssue*/
    private void updateTimeDisplay() {
        // Negate romantime.now arguments where needed to accommodate chosen state arrangement of
        // a/b switches, where false/true states depend on chosen left/right positions
        String now = romantime.now( opt.get(ampm), opt.get(ampmSeparator), !(opt.get(alignment)) );

        // IMPORTANT:
        // For the String returned by romantime.now to be correctly aligned in TimeDisplay textview,
        // TextDisplay.typeface MUST be set in activity_main.xml to 'monospace'

        float pxCurrentControlTextSize = TimeDisplaySizeControl.getTextSize();

        if (TimeDisplay.getVisibility() == View.INVISIBLE) {
            float pxDefaultControlTextSize = getResources().getDimension(R.dimen.timedisplay_size_control_default_textsize);

            /*/////
            //  Check of updateCount prevents infinitely sending broadcasts if an unforeseen
            //  occurrence keeps pxCurrentControlTextSize from falling below
            //  pxDefaultControlTextSize within a reasonable number of tries.
            //
            //  Casting of the px values to int prevents problems in the comparison if a fractional
            //  pixel value is generated in calculation of pxDefaultControlTextSize.
            *//////
            if ( (int)pxCurrentControlTextSize >= (int)pxDefaultControlTextSize && text_resize_attempt_count++ < R.dimen.text_resize_attempt_limit ) {
                sendBroadcast(makeIntent(UPDATE_DISPLAY));
            } else {
                TimeDisplay.setVisibility(View.VISIBLE);
            }
        }

        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxCurrentControlTextSize);
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
            menuFragment = new SelectionMenuFragment();
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
        text_resize_attempt_count = 0;
        sendBroadcast(makeIntent(UPDATE_DISPLAY));
    }
}
