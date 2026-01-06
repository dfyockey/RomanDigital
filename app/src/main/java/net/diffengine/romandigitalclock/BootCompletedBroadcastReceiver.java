/*
 * BootCompletedBroadcastReceiver.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2025 David Yockey
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(ACTION_BOOT_COMPLETED)) {
            Intent kickstart = new Intent(context, TimeDisplayWidget.class);
            kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
            kickstart.setPackage(context.getPackageName());
            context.sendBroadcast(kickstart);

//            Intent serviceIntent = new Intent(context, TimeTickRelay.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent);
//            } else {
//                context.startService(serviceIntent);
//            }
        }
    }
}
