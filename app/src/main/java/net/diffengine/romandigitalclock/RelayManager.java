package net.diffengine.romandigitalclock;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RelayManager {
    public static void startRelayIfWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimeDisplayWidget.class));
        if (appWidgetIds.length > 0) {
            Intent serviceIntent = new Intent(context, TimeTickRelay.class);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            } catch (Exception e) {
                new AlertDialog.Builder(context)
                        .setTitle(Html.fromHtml("<font color='#" + MainActivity.getHexFromColorRes(context, R.color.clock_red) + "'>RomanDigital Service Error</font>"))
                        .setMessage(Html.fromHtml("Unable to start/restart the TimeTickRelay service needed for widget operation, likely due to Android being too busy.<br /><br />Try again?<br /><br />If repeated retries fail, consider opening a new issue at RomanDigital's GitHub site to report a bug."))
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            startRelayIfWidgets(context);
                        })
                        .setNeutralButton("Open a new Issue at GitHub", (dialogInterface, i) -> {
                            String url = "https://github.com/dfyockey/RomanDigital/issues";
                            Intent openRdIssuesPage = new Intent(Intent.ACTION_VIEW)
                                    .setData(Uri.parse(url))
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(openRdIssuesPage);
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                        .create()
                        .show();
            }
        }
    }

    public static void startRelayIfNeeded(AppCompatActivity activity) {
        if(!isTimeTickRelayRunning(activity)) {
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