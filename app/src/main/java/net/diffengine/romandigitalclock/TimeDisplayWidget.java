package net.diffengine.romandigitalclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class TimeDisplayWidget extends AppWidgetProvider {

    static AlarmManager alarmManager;
    static PendingIntent alarmIntent;
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";
    final int ONE_MINUTE = 60000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(MINUTE_TICK)) {
            CharSequence widgetText = romantime.now(true, true, false);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
            views.setTextViewText(R.id.appwidget_text, widgetText);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetName = new ComponentName(context.getPackageName(), TimeDisplayWidget.class.getName());
            appWidgetManager.updateAppWidget(widgetName, views);
            //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetName));

//            for (int appWidgetId : appWidgetIds) {
//
//                CharSequence widgetText = romantime.now(true, true, false);
//                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
//                views.setTextViewText(R.id.appwidget_text, widgetText);
//
//                appWidgetManager.updateAppWidget(appWidgetId, views);
//            }

            //this.onUpdate(context, appWidgetManager, appWidgetIds);
        } else {
            super.onReceive(context, intent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = romantime.now(true, true, false);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
                tickIntent.setAction(MINUTE_TICK);
                alarmIntent = PendingIntent.getBroadcast(
                        context, 0, tickIntent, PendingIntent.FLAG_IMMUTABLE
                );
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ONE_MINUTE, alarmIntent);
        }
    }

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null)
            alarmManager.cancel(alarmIntent);
    }
}