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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class RelayManager {
    static int triesCount = 1;
    static int delay = 750;

    static void initCounts() {
        triesCount = 1;
        delay = 750;
    }

    public static void startRelayIfWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimeDisplayWidget.class));
        if (appWidgetIds.length > 0) {
            Intent serviceIntent = new Intent(context, TimeTickRelay.class);
            try {
                Log.d("RELAYMANAGER", "startForegndSvc try " + triesCount + ", delay = " + delay);
                startForegndSvc(context, serviceIntent);
            } catch (Exception e) {
                if (triesCount < 5) {
                    ++triesCount;
                    delay += 750;
                    new Timer().schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    Log.d("RELAYMANAGER", "Retry...");
                                    startRelayIfWidgets(context);
                                }
                            }, delay);
                } else {
                    Log.d("RELAYMANAGER", "Else...");
                    Message completeMessage = mHandler.obtainMessage(R.dimen.unused_dummy_value, context);
                    completeMessage.sendToTarget();
                }
            }
        }
    }

    static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Context context = (Context) message.obj;
            String dbl_br = "<br /><br />";
            Intent serviceIntent = new Intent(context, TimeTickRelay.class);

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
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        initCounts();
                        startRelayIfWidgets(context);
                    })
                    .setNeutralButton("Yes (crash on fail)", (dialogInterface, i) -> {
                        try {
                            startForegndSvc(context, serviceIntent);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        initCounts();
                        dialogInterface.cancel();
                    })
                    .create()
                    .show();
        }
    };

    private static void startForegndSvc(Context context, Intent serviceIntent) {
//        throw (new RuntimeException());       // For Testing! (comment out remainder of method)
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

    public static void startRelayIfNeeded(AppCompatActivity activity) {
        if(!isTimeTickRelayRunning(activity)) {
            initCounts();   // Apparently fixed uncontrolled start tries.
                            // Needs investigation.
            Log.d("ROMANDIGITAL", "Starting Relay");
            startRelayIfWidgets(activity);
        }
    }

    public static boolean isTimeTickRelayRunning(AppCompatActivity activity) {
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