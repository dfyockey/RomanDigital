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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.ActionMenuView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView TimeDisplay;
    private AppCompatTextView TimeDisplaySizeControl;
    private View     bkgndView;

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

    private SharedPreferences prefs;

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

    private boolean getPref(String key) {
        return prefs.getBoolean(key, false);
    }

    private void setKeepScreenOn() {
        //noinspection DataFlowIssue
//        boolean kon = getPref(keepon);
//        boolean owc = !getPref(onlywhencharging);
//        boolean ich = isCharging();
//        boolean keepScreenOn = kon && (owc || ich);
//        Toast.makeText(context, "keepon = " + new Boolean(kon).toString() + ", !onlywhencharging = " + new Boolean(owc).toString() + ", inCharging() = " + new Boolean(ich).toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, "keepScreenOn = " + new Boolean(keepScreenOn).toString(), Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "bkgndView.keepScreenOn = " + new Boolean(bkgndView.getKeepScreenOn()).toString(), Toast.LENGTH_SHORT).show();

        boolean keepScreenOn = getPref(keepon) && (!getPref(onlywhencharging) || isCharging());
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
        String now = romantime.now( !getPref(ampm), getPref(ampmSeparator), !getPref(alignment) );

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

    private class BroadcastReceiverEx extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeDisplay();
        }
    }

    // Receiver instance to be registered as exported for receiving system-broadcast ACTION_TIME_TICK intent
    private final BroadcastReceiverEx broadcastReceiver = new BroadcastReceiverEx();

    // Receiver instance to be registered as RECEIVER_NOT_EXPORTED for receiving app-broadcast UPDATE_DISPLAY intent
    private final BroadcastReceiverEx updateReceiver = new BroadcastReceiverEx();

    //---------------------------------------------------------------

    /** @noinspection Convert2Lambda*/
    private final View.OnClickListener bkgndOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View vToolbar = findViewById(R.id.my_toolbar);

            if (vToolbar.isShown()) {
                vToolbar.setVisibility(View.INVISIBLE);
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
            } else {
                vToolbar.setVisibility(View.VISIBLE);
                windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
            }
        }
    };

    //---------------------------------------------------------------

    private void showActivity(Class<?> cls) {
        Intent showActivityIntent = new Intent(context, cls);
        startActivity(showActivityIntent);
    }

    private void setListeners() {
        TimeDisplay = findViewById(R.id.TimeDisplay);

        bkgndView = findViewById(R.id.main_activity_bkgnd);
        bkgndView.setOnClickListener(bkgndOCL);
    }

    private void modToolbarMenu(Toolbar myToolbar) {
        // Loop through Views contained in myToolbar as suggested at
        // https://snow.dog/blog/how-to-dynamicaly-change-android-toolbar-icons-color (which is
        // Apache 2.0 licensed at https://gist.github.com/chomi3/7e088760ef7bca10430e), but set
        // icon colors by setting their Tint as suggested at
        // https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically
        int childCount = myToolbar.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View view = myToolbar.getChildAt(i);
            if (view instanceof ActionMenuView) {
                ActionMenuView actionMenuView = (ActionMenuView) view;
                Menu actionMenu = actionMenuView.getMenu();
                for (int j = 0; j < actionMenu.size(); ++j) {
                    MenuItem menuItem = actionMenu.getItem(j);
                    Drawable icon = menuItem.getIcon();
                    // Icon needs to be "wrapped" to facilitate use across different API levels.
                    // See https://developer.android.com/reference/androidx/core/graphics/drawable/DrawableCompat#wrap(android.graphics.drawable.Drawable)
                    DrawableCompat.setTint(DrawableCompat.wrap(icon), getResources().getColor(R.color.clock_red));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make MainActivity window extend beneath the System Bars to the edges of the screen.
        // From info at https://stackoverflow.com/questions/49190381/fullscreen-app-with-displaycutout
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // System Bars Behavior needs to be set as follows to prevent opening a Quick Settings Panel
        // on at least some Android versions when swiping down from the screen top while the System
        // Bars are hidden, thereby allowing the swipe to show the System Bars instead:
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        setListeners();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setVisibility(View.INVISIBLE);

        // Adapted from https://developer.android.com/develop/ui/views/layout/edge-to-edge#system-bars-insets.
        // But they @#&$%! forgot to mention that this needs to be in the onCreate method!!!
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity_bkgnd), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat windowInsets) {
                Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars());

                // Set Toolbar margins to avoid overlapping with insets
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) myToolbar.getLayoutParams();
                lpToolbar.topMargin   = insets.top;
                lpToolbar.leftMargin  = insets.left;
                lpToolbar.rightMargin = insets.right;
                myToolbar.setLayoutParams(lpToolbar);

                modToolbarMenu(myToolbar);

                return WindowInsetsCompat.CONSUMED;
            }
        });

        // VERY Important!
        // Needed so the menu resource is loaded into the toolbar!
        myToolbar.inflateMenu(R.menu.main_menu);

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean returnState = true;
                int itemId = item.getItemId();

                if(itemId == R.id.item_settings) {
                    showActivity(SettingsActivity.class);
                } else if (itemId == R.id.item_about) {
                    showActivity(AboutActivity.class);
                } else {
                    returnState = false;
                }

                return returnState;
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //---------------------------------------------------------------

    protected void onPause() {
        unregisterReceiver(updateReceiver);
        unregisterReceiver(broadcastReceiver);
        findViewById(R.id.my_toolbar).setVisibility(View.INVISIBLE);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());

        TimeDisplay.setVisibility(View.INVISIBLE);
        Toolbar vToolbar = findViewById(R.id.my_toolbar);
        vToolbar.setVisibility(View.INVISIBLE);

        String maxtime_fill = getString((getPref(ampm) == left) ? R.string.civ_fill : R.string.mil_fill);
        TimeDisplaySizeControl = findViewById(R.id.timedisplay_size_control);
        TimeDisplaySizeControl.setText(maxtime_fill);
        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, TimeDisplaySizeControl.getTextSize());

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        ContextCompat.registerReceiver(context, updateReceiver, new IntentFilter(UPDATE_DISPLAY), ContextCompat.RECEIVER_NOT_EXPORTED);
        text_resize_attempt_count = 0;
        sendBroadcast(makeIntent(UPDATE_DISPLAY));
    }
}
