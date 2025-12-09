/*
 * DroidBroadcastReceiver.java
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

package net.diffengine.romandigitalclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.diffengine.romandigitalclock.TimeDisplayWidget;

abstract class DroidBroadcastReceiver extends BroadcastReceiver {
    String targetAction;

    DroidBroadcastReceiver(String action) {
        targetAction = action;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(targetAction)) {
            Intent kickstart = new Intent(context, TimeDisplayWidget.class);
            kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
            kickstart.setPackage(context.getPackageName());
            context.sendBroadcast(kickstart);
        }
    }
}
