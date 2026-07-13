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
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RelayManager {
    static void startRelayIfWidgets(Context context) {
        String dbl_br = "<br /><br />";
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimeDisplayWidget.class));
        if (appWidgetIds.length > 0) {
            try {
                startRelay(context);
            } catch (Exception e) {
                new AlertDialog.Builder(context)
                        .setTitle(conjureFromHtml(
                                "<font color='#"
                                + MainActivity.getHexFromColorRes(context, R.color.clock_red)
                                + "'>" + context.getString(R.string.fgnd_svc_err_title)
                                + "</font>")
                        )
                        .setMessage(conjureFromHtml(
                                context.getString(R.string.fgnd_svc_err_1) + dbl_br
                                + context.getString(R.string.fgnd_svc_err_2) + dbl_br
                                + context.getString(R.string.fgnd_svc_err_3))
                        )
                        .setPositiveButton("Yes", (dialogInterface, i) -> startRelayIfWidgets(context))
                        .setNeutralButton("Yes (crash on fail)", (dialogInterface, i) -> {
                            try {
                                startRelay(context);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                        .create()
                        .show();
            }
        }
    }

    static void startRelay(Context context) {
        Intent serviceIntent = new Intent(context, TimeTickRelay.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    private static Spanned conjureFromHtml(String htm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htm, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            return Html.fromHtml(htm);
        }
    }

    static void startRelayIfNeeded(AppCompatActivity activity) {
        if(!isTimeTickRelayRunning(activity)) {
            Log.d("ROMANDIGITAL", "Starting Relay");
            startRelayIfWidgets(activity);
        }
    }

    private static boolean isTimeTickRelayRunning(AppCompatActivity activity) {
        boolean isRelayRunning = false;
        String relayProcessName = activity.getPackageName() + ":timetickrelay";
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
            Log.d("ROMANDIGITAL", processInfo.processName);
            if (processInfo.processName.equals(relayProcessName)) {
                isRelayRunning = true;
                Log.d("ROMANDIGITAL", "Relay is Running");
            }
        }
        return isRelayRunning;
    }
}