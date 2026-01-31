/*
 * SettingsActivity.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024-2025 David Yockey
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

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import net.diffengine.romandigitalclock.fragment.preference.*;

public class SettingsActivity extends AppCompatActivity {

    DisplayColorFragment displayColorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            int inApp = AppWidgetManager.INVALID_APPWIDGET_ID;
            displayColorFragment = new DisplayColorFragment(inApp);
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.app_settings_frame, new SettingsFragment(inApp))
                    .add(R.id.display_color_frame, displayColorFragment)
                    .add(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .add(R.id.button_bar_2, new SettingsButtonBarFragment())
                    .commit();
        } else {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            displayColorFragment = (DisplayColorFragment) supportFragmentManager.findFragmentById(R.id.display_color_frame);
        }
    }

    public static boolean isHexColor(String hex) {
        return !( hex == null || !hex.matches("[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]") );
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

        // Kick the widgets so they'll update time immediately in case some broadcast intent has
        // been missed. This may be unnecessary; I tend to think of intents as similar to messages
        // used in controlling other systems' GUIs, which may not be the case.
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);

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