/*
 * SettingsActivity.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024-2026 David Yockey
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

import static net.diffengine.romandigitalclock.ColorDialogPreference.UPDATE_PREVIEW;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;
import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity {

    DisplayColorFragment displayColorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Compensate for forced edge-to-edge in SDK 35 (Android 15) and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            View containerView = findViewById(android.R.id.content);
            ViewCompat.setOnApplyWindowInsetsListener(containerView, (v, insets) -> {

                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, 0, bars.right, bars.bottom);

                View spacer = findViewById(R.id.spacerAppSettings);
                spacer.getLayoutParams().height = bars.top;

                return WindowInsetsCompat.CONSUMED;
            });
        }

        if (savedInstanceState == null) {
            displayColorFragment = new DisplayColorFragment();
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.app_settings_frame, new SettingsFragment(true, AppWidgetManager.INVALID_APPWIDGET_ID))
                    .add(R.id.display_color_frame, displayColorFragment)
                    .add(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .add(R.id.button_bar_2, new SettingsButtonBarFragment())
                    .commit();
        } else {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            displayColorFragment = (DisplayColorFragment) supportFragmentManager.findFragmentById(R.id.display_color_frame);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        static String postfix;

        public SettingsFragment () {
        }

        public SettingsFragment (Boolean isApp, int appWidgetId) {
            final String appPostfix = "";
            final String widgetPostfix = String.valueOf(appWidgetId);
            postfix = ( (isApp) ? appPostfix : widgetPostfix );
        }

        private void setSeparatorEnableState(SwitchPreferenceCompat pFormat) {
            SwitchPreferenceCompat pSeparator = findPreference("switch_separator" + postfix);
            if (pFormat.isChecked() == MainActivity.right) {
                //noinspection DataFlowIssue
                pSeparator.setChecked(MainActivity.left);
                pSeparator.setEnabled(false);
            } else {
                //noinspection DataFlowIssue
                pSeparator.setEnabled(true);
            }
        }

        Context prefManagerContext;
        PreferenceCategory category;

        private void addABSwitchPreference (String key, String aText, String bText) {
            SwitchPreferenceCompat pref = new SwitchPreferenceCompat(prefManagerContext);
            pref.setLayoutResource(R.layout.a_b_switch_layout);
            pref.setKey(key + postfix);
            pref.setDefaultValue(false);
            pref.setTitle(aText);
            pref.setSummary(bText);
            category.addPreference(pref);
        }

        private void addSeparator (String key) {
            Preference pref = new Preference(prefManagerContext);
            pref.setLayoutResource(R.layout.separator_layout);
            pref.setKey(key);
            category.addPreference(pref);
        }

        private void addListPreference (String key, String title, String[] entries, String[] entryValues, String defaultValue) {
            ListPreference pref = new ListPreference(prefManagerContext);
            pref.setIconSpaceReserved(true);    // Required for some devices that default this to false
            pref.setKey(key + postfix);
            pref.setTitle(title);

            // "%s" is documented in the doc for the deprecated android.preference.ListPreference at
            // https://developer.android.com/reference/android/preference/ListPreference.html#setSummary(java.lang.CharSequence),
            // but not in the doc for its replacement androidx.preference.ListPreference at
            // https://developer.android.com/reference/androidx/preference/ListPreference#setSummary(java.lang.CharSequence).
            //
            // Consequently, while it's EXTREMELY useful, it should be considered deprecated unless
            // and until it is documented in the androidx.preference.ListPreference documentation.
            pref.setSummary("%s");

            pref.setEntries(entries);
            pref.setEntryValues(entryValues);
            pref.setValue(defaultValue);

            category.addPreference(pref);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            prefManagerContext = manager.getContext();
            PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

                category = new PreferenceCategory(prefManagerContext);
                category.setTitle("Time");
                category.setIconSpaceReserved(false);
                screen.addPreference(category);

                    addABSwitchPreference("switch_format", "12 Hour", "24 Hour");
                    addABSwitchPreference("switch_alignment", "Align to Center", "Align to Divider");
                    addABSwitchPreference("switch_separator", ": for All", "· for AM\n: for PM");

                    if (!postfix.equals("")) {
                        addSeparator("S1");

                        String[] timezoneIds = TimeZone.getAvailableIDs();
                        addListPreference("list_timezone", "Time Zone", timezoneIds, timezoneIds, TimeZone.getDefault().getID() );

                        String[] layoutEntries = {getString(R.string.tz_above_time), getString(R.string.time_only), getString(R.string.tz_below_time)};
                        String[] layoutValues  = {"hi_label", "no_label", "lo_label"};

                        addListPreference("list_widget_layout", "Display Layout", layoutEntries, layoutValues, layoutValues[1] );
                    }

            setPreferenceScreen(screen);

            // At start of the activity, ensure that the separator switch is disabled and set to
            // left if the format switch is set to right (i.e. 24 hour format).
            //
            SwitchPreferenceCompat pFormat = findPreference("switch_format" + postfix);
            //noinspection DataFlowIssue
            setSeparatorEnableState(pFormat);
        }

        @Override
        public boolean onPreferenceTreeClick(@NonNull Preference preference) {
            if (preference.getKey().equals("switch_format" + postfix)) {
                //
                // Set separator switch enable and check states based on whether format switch state
                // is left or right (i.e. whether format is 12 or 24 hour). Implementation in code
                // of the enable/disable operation is needed because it is opposite to that provided
                // by the normal preference dependency attribute.
                //
                SwitchPreferenceCompat pFormat = (SwitchPreferenceCompat)preference;
                setSeparatorEnableState(pFormat);
            }
            return super.onPreferenceTreeClick(preference);
        }
    }

    public static class ScreenSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.screen_preferences, rootKey);
        }
    }

    public static boolean isHexColor(String hex) {
        return !( hex == null || !hex.matches("[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]") );
    }

    public static class DisplayColorFragment extends PreferenceFragmentCompat {
        Context prefManagerContext;
        PreferenceCategory category;

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            prefManagerContext = manager.getContext();
            PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

                category = new PreferenceCategory(prefManagerContext);
                category.setIconSpaceReserved(false);
                category.setTitle("Style");
                screen.addPreference(category);

                ColorDialogPreference colorPref = new ColorDialogPreference(prefManagerContext, getChildFragmentManager());
                colorPref.setTitle("Time Color");
                colorPref.setKey("hexcolor");
                category.addPreference(colorPref);

            setPreferenceScreen(screen);
        }

        public void updateDialogTimeDisplayPreview() {
            ColorDialogPreference colorDialogPreference = category.findPreference("hexcolor");
            ColorDialogPreference.ColorDialogFragment colorDialogFragment = Objects.requireNonNull(colorDialogPreference).getColorDialogFragment();

            if (colorDialogFragment != null) {
                Dialog dialog = colorDialogFragment.getDialog();
                if (dialog != null && dialog.isShowing()) {
                    colorDialogFragment.updatePreviewTime();
                }
            }
        }
    }

    private class BroadcastReceiverEx extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (displayColorFragment != null) {
                displayColorFragment.updateDialogTimeDisplayPreview();
            }
        }
    }

    // Receiver instance to be registered as exported for receiving system-broadcast ACTION_TIME_TICK intent
    private final BroadcastReceiverEx broadcastReceiver = new BroadcastReceiverEx();

    // Receiver instance to be registered as RECEIVER_NOT_EXPORTED for receiving UPDATE_PREVIEW intent broadcast from ColorDialogFragment
    private final BroadcastReceiverEx updateReceiver = new BroadcastReceiverEx();

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        ContextCompat.registerReceiver(this, updateReceiver, new IntentFilter(UPDATE_PREVIEW), ContextCompat.RECEIVER_NOT_EXPORTED);
    }
}