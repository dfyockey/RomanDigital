package net.diffengine.romandigitalclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;

public class TimeTickRelay extends Service {
    final String CHANNEL_ID = getString(R.string.channel_id);
    final String NOTIFICATION_TAG = getString(R.string.notification_tag);
    final int NOTIFICATION_ID = Integer.parseInt(getString(R.string.notification_id));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        createNotificationChannel();

        // Make PendingIntent to handle click on the notification
        Intent clickIntent = new Intent(this, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_rd_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.rd_launcher_foreground))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(clickPendingIntent)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            stopSelf(startId);
        } else {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification);

            int foregroundServiceType = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;
            }

            ServiceCompat.startForeground(this, startId, notification, foregroundServiceType);
        }

        return START_REDELIVER_INTENT;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
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
}
