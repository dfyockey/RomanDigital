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

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.app_settings_frame, new SettingsFragment())
                    .replace(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.time_preferences, rootKey);

            // At start of the activity, ensure that the separator switch is disabled and set to
            // left if the format switch is set to right (i.e. 24 hour format).
            //
            SwitchPreferenceCompat pFormat = findPreference("switch_format");
            SwitchPreferenceCompat pSeparator = findPreference("switch_separator");
            try {
                //noinspection DataFlowIssue
                if (pFormat.isChecked() == MainActivity.right) {
                    //noinspection DataFlowIssue
                    pSeparator.setChecked(MainActivity.left);
                    pSeparator.setEnabled(false);
                }
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), "Error auto-setting\nseparator switch!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onPreferenceTreeClick(@NonNull Preference preference) {
            if (preference.getKey().equals("switch_format")) {
                // Set separator switch enable and check states based on whether format switch state
                // is left or right (i.e. whether format is 12 or 24 hour). Implementation in code
                // of the enable/disable operation is needed because it is opposite to that provided
                // by the normal preference dependency attribute.
                SwitchPreferenceCompat pFormat = (SwitchPreferenceCompat)preference;
                SwitchPreferenceCompat pSeparator = findPreference("switch_separator");
                try {
                    if (pFormat.isChecked() == MainActivity.right) {
                        //noinspection DataFlowIssue
                        pSeparator.setChecked(MainActivity.left);
                        pSeparator.setEnabled(false);
                    } else {
                        //noinspection DataFlowIssue
                        pSeparator.setEnabled(true);
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "Error responding to\nformat switch click!", Toast.LENGTH_LONG).show();
                }
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

        // Kick the widget so it'll update immediately based on
        // any preference changes made through this activity
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);
    }
}