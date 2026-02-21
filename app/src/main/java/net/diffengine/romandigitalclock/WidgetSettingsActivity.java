/*
 * WidgetSettingsActivity.java
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import net.diffengine.romandigitalclock.fragment.preference.TimeFormatFragment;
import net.diffengine.romandigitalclock.fragment.preference.TimeStyleFragment;

import java.util.Objects;

public class WidgetSettingsActivity extends AppCompatActivity {
    int appWidgetId;

    public WidgetSettingsActivity() {
        super(R.layout.widget_settings_activity);
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
        setContentView(R.layout.widget_settings_activity);

        // Compensate for forced edge-to-edge in SDK 35 (Android 15) and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            View containerView = findViewById(android.R.id.content);
//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutWidgetSettings), (v, insets) -> {
            ViewCompat.setOnApplyWindowInsetsListener(containerView, (v, insets) -> {

                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, 0, bars.right, bars.bottom);

                View spacer = findViewById(R.id.spacerWidgetSettings);
                spacer.getLayoutParams().height = bars.top;

                return WindowInsetsCompat.CONSUMED;
            });
        }

        if(BuildConfig.DEBUG) {
            String activityTitle = (String) getTitle();
            setTitle(activityTitle + " - " + appWidgetId);
        }

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true);

            fragmentTransaction.add(R.id.settings_frame, new TimeFormatFragment(appWidgetId));
            // No style setting currently works in SDK 21, so exclude the style fragment from there
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                fragmentTransaction.add(R.id.style_frame, new TimeStyleFragment(appWidgetId));
            }
            fragmentTransaction.add(R.id.widget_bkgnd, new WidgetBkgndSettingsFragment(appWidgetId));
            fragmentTransaction.add(R.id.button_bar, new SettingsButtonBarFragment()).commit();
        }

        BootCompletedBroadcastReceiver.startRelayIfWidgets(this);
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

            seekBarPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                SeekBarPreference seekBarPref = (SeekBarPreference) preference;
                seekBarPref.setSummary( buildOpacityLabel((int)newValue) );
                return true;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AboutActivity.showAboutOnUpgrade(this, BuildConfig.VERSION_CODE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Broadcast an intent immediately after either Close or Save is pressed
        // and the config activity is closed. This updates the widget immediately
        // rather than waiting for the next relayed ACTION_TIME_TICK to arrive.
        Intent update_widget = new Intent(this, TimeDisplayWidget.class);
        update_widget.setAction(TimeDisplayWidget.RELAYED_TIME_TICK);
        update_widget.setPackage(this.getPackageName());
        this.sendBroadcast(update_widget);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
