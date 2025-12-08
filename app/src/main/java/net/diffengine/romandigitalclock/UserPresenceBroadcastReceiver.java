/*
 * UserPresenceBroadcastReceiver.java
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

/*
 * Yes, yes, I know this is a horribly redundant slight modification of
 * BootCompletedBroadcastReceiver. But I'm in a hurry! I'll fix it later!
 * (Said every corner-cutting dev ever.)
 */

package net.diffengine.romandigitalclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(Intent.ACTION_USER_PRESENT)) {
            Intent kickstart = new Intent(context, TimeDisplayWidget.class);
            kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
            kickstart.setPackage(context.getPackageName());
            context.sendBroadcast(kickstart);
        }
    }
}