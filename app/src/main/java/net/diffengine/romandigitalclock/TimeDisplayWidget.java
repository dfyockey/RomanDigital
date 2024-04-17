package net.diffengine.romandigitalclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import java.util.Calendar;

public class TimeDisplayWidget extends AppWidgetProvider {
    static AlarmManager alarmManager;
    static PendingIntent alarmPendingIntent;
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";

    private static RemoteViews updateTimeDisplay(Context context) {
        CharSequence widgetText = romantime.now(true, true, true);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        return views;
    }

    private void onTick (Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetName = new ComponentName(context.getPackageName(), TimeDisplayWidget.class.getName());
        appWidgetManager.updateAppWidget(widgetName, updateTimeDisplay(context));
        setAlarm(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action.equals(MINUTE_TICK)) {
            onTick(context);
        } else if (action.equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)) {
            setAlarm(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
        tickIntent.setAction(MINUTE_TICK);
        return PendingIntent.getBroadcast(context, 0, tickIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private static void setAlarm (Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        long t = cal.getTimeInMillis();
        setAlarm(context, t);
    }

    private static void setAlarm (Context context, long targetTime) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (alarmPendingIntent == null) {
            alarmPendingIntent = getPendingIntent(context);
        }

        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.R || alarmManager.canScheduleExactAlarms() ) {
            alarmManager.setExact(AlarmManager.RTC, targetTime, alarmPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, targetTime, alarmPendingIntent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        setAlarm(context, Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, updateTimeDisplay(context));
    }
}