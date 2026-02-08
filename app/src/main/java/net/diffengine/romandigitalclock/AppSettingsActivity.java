/*
 * AppSettingsActivity.java
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

import net.diffengine.romandigitalclock.fragment.preference.ScreenSettingsFragment;
import net.diffengine.romandigitalclock.fragment.preference.TimeFormatFragment;
import net.diffengine.romandigitalclock.fragment.preference.TimeStyleFragment;

import java.util.Objects;
import java.util.TimeZone;

public class AppSettingsActivity extends AppCompatActivity {

    TimeStyleFragment timeStyleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings_activity);

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
            int inApp = AppWidgetManager.INVALID_APPWIDGET_ID;
            timeStyleFragment = new TimeStyleFragment(inApp);
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.settings_frame, new TimeFormatFragment(inApp))
                    .add(R.id.style_frame, timeStyleFragment)
                    .add(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .add(R.id.button_bar_2, new SettingsButtonBarFragment())
                    .commit();
        } else {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            timeStyleFragment = (TimeStyleFragment) supportFragmentManager.findFragmentById(R.id.style_frame);
        }
    }

    public static boolean isHexColor(String hex) {
        return !( hex == null || !hex.matches("[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]") );
    }

    private class BroadcastReceiverEx extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (timeStyleFragment != null) {
                timeStyleFragment.updateDialogTimeDisplayPreview();
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