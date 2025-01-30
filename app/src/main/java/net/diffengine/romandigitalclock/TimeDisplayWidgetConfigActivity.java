/*
 * TimeDisplayWidgetConfigActivity.java
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

public class TimeDisplayWidgetConfigActivity extends AppCompatActivity {
    int appWidgetId;

    public TimeDisplayWidgetConfigActivity() {
        super(R.layout.activity_time_display_widget_config);
    }

    private void setResultCanceled() {
        // Enable cancellation of the configuration and,
        // if it's being added, app widget addition to home screen.
        // See https://developer.android.com/develop/ui/views/appwidgets/configuration#java
        // Provision of appwidget id in the extra data should also
        // prevent crash of TouchWiz on old Samsung devices at activity destruction.
        // See https://stackoverflow.com/a/40709721
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResultCanceled();
        setContentView(R.layout.activity_time_display_widget_config);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true);

            fragmentTransaction.add(R.id.widget_settings, new SettingsActivity.SettingsFragment(false, appWidgetId));
            fragmentTransaction.add(R.id.widget_bkgnd, new WidgetBkgndSettingsFragment(appWidgetId));
            fragmentTransaction.add(R.id.button_bar, new SettingsButtonBarFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Enable close of activity with an OK condition
            // See https://developer.android.com/develop/ui/views/appwidgets/configuration#java
            /*
                No need to update the widget here since it will be updated on receipt of
                the kickstart intent that will be broadcast in this activity's onPause method
            */
            Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // This method is unused and should be removed at some point
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.time_preferences, rootKey);
        }
    }

    public static class WidgetBkgndSettingsFragment extends PreferenceFragmentCompat {

        static String postfix;

        public WidgetBkgndSettingsFragment () {
        }

        public WidgetBkgndSettingsFragment (int appWidgetId) {
            postfix = String.valueOf(appWidgetId);
        }

        String buildOpacityLabel(int rawvalue) {
            int percentage = rawvalue * 10;
            return "Opacity: " + percentage + "%";
        }

        Context prefManagerContext;
        PreferenceCategory category;

        @SuppressWarnings("SameParameterValue")     // From https://stackoverflow.com/a/48734923/
        private void addSeekBarPreference (String key) {
            SeekBarPreference pref = new SeekBarPreference(prefManagerContext);
            pref.setKey(key + postfix);
            pref.setMax(10);
            pref.setShowSeekBarValue(false);
            pref.setUpdatesContinuously(true);
            pref.setSummary("Opacity: %");

            // Required for some devices that default this to false
            pref.setIconSpaceReserved(true);

            category.addPreference(pref);
        }

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            prefManagerContext = manager.getContext();
            PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

            category = new PreferenceCategory(prefManagerContext);
            category.setTitle("Background");
            category.setIconSpaceReserved(false);
            screen.addPreference(category);

                addSeekBarPreference("seekbar_opacity");

            setPreferenceScreen(screen);

            SeekBarPreference seekBarPreference = findPreference("seekbar_opacity" + postfix);
            Objects.requireNonNull(seekBarPreference).setSummary( buildOpacityLabel(seekBarPreference.getValue()) );

            seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    SeekBarPreference seekBarPref = (SeekBarPreference) preference;
                    seekBarPref.setSummary( buildOpacityLabel((int)newValue) );
                    return true;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Since the widget's alarms may have been canceled on pause,
        // broadcast an intent to kickstart the widget when it resumes
        // along with immediately updating the widget
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.SETTINGS_KICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
