/*
 * MainActivity.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright © 2024-2026 David Yockey
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

import static android.view.View.VISIBLE;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/** @noinspection Convert2Lambda, SpellCheckingInspection */
public class MainActivity extends AppCompatActivity {
    private TextView TimeDisplay;
    private AppCompatTextView TimeDisplaySizeControl;
    private View     bkgndView;

    public static boolean left  = false;
    public static boolean right = true;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private final Context context = this;
    private final String UPDATE_DISPLAY = "net.diffengine.romandigitalclock.UPDATE_DISPLAY";

    // Aliases for option keys
    public static final String
            ampm = "switch_format",
            alignment = "switch_alignment",
            ampmSeparator = "switch_separator",
            keepon = "switch_keep_on",
            onlywhencharging = "switch_when_charging";

    private SharedPreferences prefs;

    //////////////////////////////////////////////////////////////////////

    private boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Regarding the null receiver in the following line, see
        // https://developer.android.com/training/monitoring-device-state/battery-monitoring#DetermineChargeState
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
        boolean keepScreenOn = getPref(keepon) && (!getPref(onlywhencharging) || isCharging());
        bkgndView.setKeepScreenOn(keepScreenOn);
    }

    /** @noinspection SameParameterValue*/
    // Not inlined since this method will likely be useful for a later added feature
    private Intent makeIntent (String action) {
        Intent i = new Intent();
            i.setAction(action);
            i.setPackage(context.getPackageName());
        return i;
    }

    int text_resize_attempt_count = 0;

    private void updateTimeDisplay() {
        // Negate romantime.now arguments where needed to accommodate chosen state arrangement of
        // a/b switches, where false/true states depend on chosen left/right positions
        String now = romantime.now( !getPref(ampm), getPref(ampmSeparator), !getPref(alignment), TimeZone.getDefault().getID() );

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
                TimeDisplay.setVisibility(VISIBLE);
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

    private final View.OnClickListener bkgndOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View vToolbar = findViewById(R.id.my_toolbar);

            if (vToolbar.isShown()) {
                vToolbar.setVisibility(View.INVISIBLE);
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
            } else {
                vToolbar.setVisibility(VISIBLE);
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

    private void modToolbarMenu(Toolbar myToolbar, @ColorInt int color) {
        // Loop through Views contained in myToolbar as suggested at
        // https://snow.dog/blog/how-to-dynamicaly-change-android-toolbar-icons-color (which is
        // Apache 2.0 licensed at https://gist.github.com/chomi3/7e088760ef7bca10430e), but set
        // icon colors by setting their Tint as suggested at
        // https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically
        int childCount = myToolbar.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View view = myToolbar.getChildAt(i);
            if (view instanceof ActionMenuView) {
                //noinspection PatternVariableCanBeUsed
                ActionMenuView actionMenuView = (ActionMenuView) view;
                Menu actionMenu = actionMenuView.getMenu();
                for (int j = 0; j < actionMenu.size(); ++j) {
                    MenuItem menuItem = actionMenu.getItem(j);
                    Drawable icon = menuItem.getIcon();
                    // Icon needs to be "wrapped" to facilitate use across different API levels.
                    // See https://developer.android.com/reference/androidx/core/graphics/drawable/DrawableCompat#wrap(android.graphics.drawable.Drawable)
                    if (icon != null) {
                        DrawableCompat.setTint(DrawableCompat.wrap(icon), color);
                    }
                }
            }
        }
    }

    static private String getHexFromColorRes(Context context, @ColorRes int id) {
        int colorInt = ContextCompat.getColor(context, id) & 0xFFFFFF;
        return String.format("%06X", colorInt);
    }

    static public String getDefaultColorHexString(Context context) {
        return getHexFromColorRes(context, R.color.clock_red);
    }

    private void setDisplayColor(@ColorInt int color) {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(color);

        modToolbarMenu(toolbar, color);

        TextView textView = findViewById(R.id.TimeDisplay);
        textView.setTextColor(color);
    }

    public static String getHexColor(Context context, SharedPreferences sp, String key) {
        String defaultColorHexStr = getHexFromColorRes(context, R.color.clock_red);
        String hexcolor = sp.getString(key, defaultColorHexStr);
        if (!AppSettingsActivity.isHexColor(hexcolor)) {
            hexcolor = defaultColorHexStr;
        }
        return hexcolor;
    }

    private void setDisplayColorFromPref() {
        if(prefs != null) {
            String hexcolor = getHexColor(this, prefs, "hexcolor");
            setDisplayColor(Color.parseColor("#" + hexcolor));
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setDisplayFont(String family) {
        if( prefs != null) {
            Typeface typeface = Typeface.DEFAULT;
            int index = Integer.parseInt(prefs.getString("list_typeface", "0"));

            if (index > 0) {
                index -= 1;

                // Add a new AbstractMap for each font family to be used. See https://www.baeldung.com/java-initialize-hashmap
                Map<String, int[]> font = Map.ofEntries(
                        new AbstractMap.SimpleEntry<>("roboto", new int[]{R.font.roboto_mono, R.font.roboto_sans, R.font.roboto_serif})
                );

                try {
                    typeface = ResourcesCompat.getFont(context, Objects.requireNonNull(font.get(family))[index]);
                } catch(Resources.NotFoundException e) {
                    /*
                     Ignore any exception if a font's not found, and typeface will still equal Typeface.DEFAULT.
                     May cause display of an unintended typeface, but that's better than a crash.
                    */
                }
            }

            TimeDisplay.setTypeface(typeface);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make MainActivity window extend beneath the System Bars to the edges of the screen.
        // From info at https://stackoverflow.com/questions/49190381/fullscreen-app-with-displaycutout
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)  {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
                Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());

                // Set Toolbar margins to avoid overlapping with insets
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) myToolbar.getLayoutParams();
                lpToolbar.topMargin   = insets.top;
                lpToolbar.leftMargin  = insets.left;
                lpToolbar.rightMargin = insets.right;
                myToolbar.setLayoutParams(lpToolbar);

                return WindowInsetsCompat.CONSUMED;
            }
        });

        // VERY Important!
        // Needed so the menu resource is loaded into the toolbar!
        myToolbar.inflateMenu(R.menu.main_menu);

//        // Uncomment to include a toolbar button to reset something while debugging
//        if (BuildConfig.DEBUG) {
//            MenuItem item = myToolbar.getMenu().findItem(R.id.item_reset_setting);
//            item.setEnabled(true);
//            item.setVisible(true);
//        }

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean returnState = true;
                int itemId = item.getItemId();

                if(itemId == R.id.item_settings) {
                    showActivity(AppSettingsActivity.class);
                } else if (itemId == R.id.item_about) {
                    showActivity(AboutActivity.class);
                } else if (itemId == R.id.item_reset_setting) {
                    // Add code here to reset something in DEBUG build on click of the reset button
                } else {
                        returnState = false;
                }

                return returnState;
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setDisplayColorFromPref();

        BootCompletedBroadcastReceiver.startRelayIfWidgets(context);
    }

    //---------------------------------------------------------------

    protected void onPause() {
        unregisterReceiver(updateReceiver);
        unregisterReceiver(broadcastReceiver);
        findViewById(R.id.my_toolbar).setVisibility(View.INVISIBLE);

        // Broadcast an intent immediately after either Close or Save is pressed
        // and the config activity is closed. This updates the widget immediately
        // rather than waiting for the next relayed ACTION_TIME_TICK to arrive.
        Intent update_widget = new Intent(this, TimeDisplayWidget.class);
        update_widget.setAction(TimeDisplayWidget.RELAYED_TIME_TICK);
        update_widget.setPackage(this.getPackageName());
        this.sendBroadcast(update_widget);

        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        AboutActivity.showAboutOnUpgrade(this, BuildConfig.VERSION_CODE);

        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());

        TimeDisplay.setVisibility(View.INVISIBLE);
        Toolbar vToolbar = findViewById(R.id.my_toolbar);
        vToolbar.setVisibility(View.INVISIBLE);

        String maxtime_fill = getString((getPref(ampm) == left) ? R.string.civ_fill : R.string.mil_fill);
        TimeDisplaySizeControl = findViewById(R.id.timedisplay_size_control);
        TimeDisplaySizeControl.setText(maxtime_fill);
        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, TimeDisplaySizeControl.getTextSize());
        setDisplayColorFromPref();
        setDisplayFont("roboto");

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        ContextCompat.registerReceiver(context, updateReceiver, new IntentFilter(UPDATE_DISPLAY), ContextCompat.RECEIVER_NOT_EXPORTED);
        text_resize_attempt_count = 0;
        sendBroadcast(makeIntent(UPDATE_DISPLAY));
    }
}
