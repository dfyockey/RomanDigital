/*
 * RelayManager.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright © 2026 David Yockey
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

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
//import android.text.Html;
//import android.text.Spanned;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class RelayManager {

    static void startRelayIfWidgets(Context appContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(appContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(appContext, TimeDisplayWidget.class));
        if (appWidgetIds.length > 0) {
            Log.d("RELAYMANAGER", "In startRelayIfWidgets");
            startRelay(appContext);
        }
    }

    static void startRelay(Context appContext) {
        Intent serviceIntent = new Intent(appContext, TimeTickRelay.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(serviceIntent);
        } else {
            appContext.startService(serviceIntent);
        }
    }

    static void startRelayIfNeeded(AppCompatActivity activity) {
        if(!isTimeTickRelayRunning(activity)) {
            Log.d("RELAYMANAGER", "Starting Relay");
            startRelayIfWidgets(activity.getApplicationContext());
        }
    }

    private static boolean isTimeTickRelayRunning(AppCompatActivity activity) {
        boolean isRelayRunning = false;
        String relayProcessName = activity.getPackageName() + ":timetickrelay";
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
            Log.d("RELAYMANAGER", processInfo.processName);
            if (processInfo.processName.equals(relayProcessName)) {
                isRelayRunning = true;
                Log.d("RELAYMANAGER", "Relay is Already Running");
            }
        }
        return isRelayRunning;
    }

    // utility methods ////////////////////////////////////////////////////

//    private static Spanned conjureFromHtml(String htm) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return Html.fromHtml(htm, Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            //noinspection deprecation
//            return Html.fromHtml(htm);
//        }
//    }
}