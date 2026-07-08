/*
 * BootCompletedBroadcastReceiver.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2025-2026 David Yockey
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

import static android.content.Intent.ACTION_BOOT_COMPLETED;

import static net.diffengine.romandigitalclock.RelayManager.startRelay;
import static net.diffengine.romandigitalclock.RelayManager.startRelayAfterInitCounts;
import static net.diffengine.romandigitalclock.RelayManager.startRelayIfWidgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(ACTION_BOOT_COMPLETED)) {
            Log.d("ROMANDIGITAL", "startRelay in BootCompletedBroadcastReceiver");
            startRelay(context);

//            // Relay's never running on boot completion, so call function to start it directly
//            Log.d("ROMANDIGITAL", "startRelayAfterInitCounts in BootCompletedBroadcastReceiver");
//            Log.d("ROMANDIGITAL", "inStartRelayProcess = " + RelayManager.inStartRelayProcess);
//            if (!RelayManager.inStartRelayProcess) {
//                Log.d("ROMANDIGITAL", "In `if...`");
//                RelayManager.inStartRelayProcess = true;
//                startRelayAfterInitCounts(context);
//            } else {
//                Log.d("ROMANDIGITAL", "In `else...`");
//                Log.d("ROMANDIGITAL", "BootCompletedBroadcastReceiver start aborted");
//            }
        }
    }
}
