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
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class TimeTickRelay extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
            tickIntent.setAction(TimeDisplayWidget.RELAYED_TIME_TICK);
            tickIntent.setPackage(context.getPackageName());
            context.sendBroadcast(tickIntent);
        }
    }
    TickReceiver tickReceiver = new TickReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL_ID = getString(R.string.channel_id);
        int NOTIFICATION_ID = Integer.parseInt(getString(R.string.notification_id));

        createNotificationChannel(CHANNEL_ID);
        Notification notification = createNotification(CHANNEL_ID, createClickPendingIntent());
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, getServiceType());

        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
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
        int foregroundServiceType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;
        }
        return foregroundServiceType;
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
                .setContentIntent(clickPendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tickReceiver);
    }
}
