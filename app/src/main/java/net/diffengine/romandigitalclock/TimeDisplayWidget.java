package net.diffengine.romandigitalclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class TimeDisplayWidget extends AppWidgetProvider {

    static AlarmManager alarmManager;
    static PendingIntent alarmIntent;
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";
    final int FIFTEEN_SECS = 15000;

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
            onTick(context);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, updateTimeDisplay(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        onTick(context, appWidgetManager);
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
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), FIFTEEN_SECS, alarmIntent);
        }
    }

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null)
            alarmManager.cancel(alarmIntent);
    }
}