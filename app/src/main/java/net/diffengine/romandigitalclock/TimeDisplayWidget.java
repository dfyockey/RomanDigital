package net.diffengine.romandigitalclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class TimeDisplayWidget extends AppWidgetProvider {
    static AlarmManager alarmManager;
    static PendingIntent alarmPendingIntent;
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";

    private static RemoteViews updateTimeDisplay(Context context) {
        CharSequence widgetText = romantime.now(true, true, false);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        return views;
    }

    private void onTick (Context context, AppWidgetManager appWidgetManager) {
        ComponentName widgetName = new ComponentName(context.getPackageName(), TimeDisplayWidget.class.getName());
        appWidgetManager.updateAppWidget(widgetName, updateTimeDisplay(context));
    }

    private void onTick (Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onTick(context, appWidgetManager);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action.equals(MINUTE_TICK)) {
            setAlarm(context);
            onTick(context);
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
        //tickIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        return PendingIntent.getBroadcast(context, 0, tickIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void setAlarm (Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        long t = cal.getTimeInMillis();
        alarmPendingIntent = getPendingIntent(context);
        alarmManager.setExact(AlarmManager.RTC, t, alarmPendingIntent);
    }

    @Override
    public void onEnabled(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        alarmPendingIntent = getPendingIntent(context);
        alarmManager.setExact(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), alarmPendingIntent);
    }

//        Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
//        //tickIntent.setAction(MINUTE_TICK);
//        tickIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        alarmPendingIntent = PendingIntent.getBroadcast(context, 0, tickIntent, PendingIntent.FLAG_IMMUTABLE);

        //alarmManager.cancel(alarmPendingIntent);


        //alarmManager.setExact(AlarmManager.RTC, later, alarmPendingIntent);

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, updateTimeDisplay(context));
//
//        //CharSequence widgetText = context.getString(R.string.appwidget_text);
//
//        CharSequence widgetText = romantime.now(true, true, false);
//
//                // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//
//        //alarmManager.cancel(alarmPendingIntent);
//
//        long now = Calendar.getInstance().getTimeInMillis();
//        long later = now + 30000;
//
//        alarmPendingIntent = getPendingIntent(context);
//        alarmManager.setExact(AlarmManager.RTC, now, alarmPendingIntent);
//        //alarmManager.setExact(AlarmManager.RTC, later, alarmPendingIntent);
    }
}