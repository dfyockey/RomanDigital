/*
 * SettingsActivity.java
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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.app_settings_frame, new SettingsFragment(true, AppWidgetManager.INVALID_APPWIDGET_ID))
                    .add(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .add(R.id.button_bar_2, new SettingsButtonBarFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        String postfix;

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

        // Pass key for symmetry in preference method calls
        private void addTimeZoneListPreference (String key) {
            ListPreference pref = new ListPreference(prefManagerContext);
            pref.setKey(key + postfix);
            pref.setTitle("Time Zone");

            // "%s" is documented in the doc for the deprecated android.preference.ListPreference at
            // https://developer.android.com/reference/android/preference/ListPreference.html#setSummary(java.lang.CharSequence),
            // but not in the doc for its replacement androidx.preference.ListPreference at
            // https://developer.android.com/reference/androidx/preference/ListPreference#setSummary(java.lang.CharSequence).
            //
            // Consequently, while it's EXTREMELY useful, it should be considered deprecated unless
            // and until it is documented in the androidx.preference.ListPreference documentation.
            pref.setSummary("%s");

            pref.setEntries(TimeZone.getAvailableIDs());
            pref.setEntryValues(TimeZone.getAvailableIDs());

            // Set summary if necessary
            if (pref.getEntry() == null) {
                pref.setValue(TimeZone.getDefault().getID());
            }

            category.addPreference(pref);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //setPreferencesFromResource(R.xml.time_preferences, rootKey);

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
                        addTimeZoneListPreference("list_timezone");
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

    @Override
    protected void onPause() {
        super.onPause();

        // Kick the widgets so they'll update time immediately in case some broadcast intent has
        // been missed. This may be unnecessary; I tend to think of intents as similar to messages
        // used in controlling other systems' GUIs, which may not be the case.
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);
    }
}