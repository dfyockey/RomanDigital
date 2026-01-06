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
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class TimeTickRelay extends Service {
    String CHANNEL_ID;
    String NOTIFICATION_TAG;
    int NOTIFICATION_ID;

    private static boolean Service_Running = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Tick!", Toast.LENGTH_LONG).show();
        }
    }
    TickReceiver tickReceiver = new TickReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        CHANNEL_ID = getString(R.string.channel_id);
        NOTIFICATION_TAG = getString(R.string.notification_tag);
        NOTIFICATION_ID = Integer.parseInt(getString(R.string.notification_id));
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

//        if (Service_Running) {
//            stopSelf(startId);
//            return START_STICKY;
//        }

        if (!Service_Running) {
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
                Log.d("RELAY", "No Notifications");
                stopSelf(startId);
            } else {
                Log.d("RELAY", "Notifications OK!");

                int foregroundServiceType = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;
                }

                ServiceCompat.startForeground(this, startId, notification, foregroundServiceType);

                Service_Running = true;
            }

            return START_REDELIVER_INTENT;
        }

        stopSelf(startId);
        return START_STICKY;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tickReceiver);
        Service_Running = false;
    }
}
