/*
 * TimeTickRelay.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2026 David Yockey
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

import android.app.ForegroundServiceStartNotAllowedException;   // For Testing
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class TimeTickRelay extends Service {

    private int triesCount; // Only used in a debug build
    private int delay;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class TickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastTimeTick(context);
        }
    }
    TickReceiver tickReceiver = new TickReceiver();

    private void broadcastTimeTick(Context context) {
        Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
        tickIntent.setAction(TimeDisplayWidget.RELAYED_TIME_TICK);
        tickIntent.setPackage(context.getPackageName());
        context.sendBroadcast(tickIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDelay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String CHANNEL_ID = getString(R.string.channel_id);
        int NOTIFICATION_ID = Integer.parseInt(getString(R.string.notification_id));

        createNotificationChannel(CHANNEL_ID);
        Notification notification = createNotification(CHANNEL_ID, createClickPendingIntent());

        try {
            Log.d("TIME_TICK_RELAY", "Try to startForeground");
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, getServiceType());
            Log.d("TIME_TICK_RELAY", "Success!");

            initDelay();

            // Insure we don't accidentally register the receiver twice
            try {
                Log.d("TIME_TICK_RELAY", "Try to unregisterReceiver");
                unregisterReceiver(tickReceiver);
            } catch (IllegalArgumentException e) {
                Log.d("TIME_TICK_RELAY", "IllegalArgumentException caught");
                Log.d("TIME_TICK_RELAY", "The receiver's not registered");
                // Okay, it's not registered, so we're good to go
            }

            Log.d("TIME_TICK_RELAY", "Registering the receiver...");
            registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
            Log.d("TIME_TICK_RELAY", "The receiver's now registered!");

        } catch (RuntimeException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e instanceof ForegroundServiceStartNotAllowedException) {
                Log.d("TIME_TICK_RELAY", "ForegroundServiceStartNotAllowedException caught");

                Log.d("TIME_TICK_RELAY", "Broadcast an unsynchronized RELAYED_TIME_TICK");
                // Send a tick to the widgets to keep them fairly on time while trying to restart the Relay
                broadcastTimeTick(getApplicationContext());

                if (delay < 32000) {
                    delay *= 2;
                }
                Log.d("TIME_TICK_RELAY", "Call startRelayIfWidgets again : Try #" + triesCount++ + ", Delay = " + delay + "ms");
                new Handler(Looper.getMainLooper()).postDelayed(() -> RelayManager.startRelayIfWidgets(getApplicationContext()), delay);
            } else {
                Log.d("TIME_TICK_RELAY", "Ack! It's an exceptional RuntimeException!");
                throw e;
            }
        }

        return START_STICKY;
    }

    private void createNotificationChannel(String channel_id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channel_id,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_MIN
            );
            channel.setDescription(getString(R.string.channel_description));
            channel.setShowBadge(false);

            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int getServiceType() {
        boolean isSDK34 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE);
        return (isSDK34 ? ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE : 0);
    }

    private PendingIntent createClickPendingIntent() {
        // Make PendingIntent to handle click on the notification
        Intent clickIntent = new Intent(this, AboutActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private Notification createNotification(String channel_id, PendingIntent clickPendingIntent) {
        return new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.ic_rd_notification_icon)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentText(getString(R.string.notification_content_text))
                .setContentIntent(clickPendingIntent)
                .build();
    }

    void initDelay() {
        delay = 250;    // 0.5 of intended first delay since it's multiplied by 2 before use

        // Note: delay should not exceed 32000 to insure that two ticks at most are provided
        // per minute when the delay reaches a maximum while the service is trying to get started.
        // That way, the system won't be too effected by the repeated attempts to start the service.

        triesCount = 1; // Only used in a debug build
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.d("TIME_TICK_RELAY", "Try unregisterReceiver in onDestroy");
            unregisterReceiver(tickReceiver);
        } catch (IllegalArgumentException e) {
            Log.d("TIME_TICK_RELAY", "The receiver's already not registered");
            // Okay, it's not registered, so we're good to go
        }
    }
}
