/*
 * TimeDisplayWidget.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024 David Yockey
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

/** @noinspection SpellCheckingInspection*/
public class TimeDisplayWidget extends AppWidgetProvider {
    static AlarmManager alarmManager;
    static PendingIntent alarmPendingIntent;

    static int[] opacity = {
            (R.drawable.appwidget_bkgnd_0),
            (R.drawable.appwidget_bkgnd_10),
            (R.drawable.appwidget_bkgnd_20),
            (R.drawable.appwidget_bkgnd_30),
            (R.drawable.appwidget_bkgnd_40),
            (R.drawable.appwidget_bkgnd_50),
            (R.drawable.appwidget_bkgnd_60),
            (R.drawable.appwidget_bkgnd_70),
            (R.drawable.appwidget_bkgnd_80),
            (R.drawable.appwidget_bkgnd_90),
            (R.drawable.appwidget_bkgnd_100)
    };
    
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";

    private static RemoteViews updateTimeDisplay(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ampm          = sp.getBoolean("switch_format", false);
        boolean ampmSeparator = sp.getBoolean("switch_separator", false);
        boolean alignment     = sp.getBoolean("switch_alignment", false);

        // Negate romantime.now arguments where needed to accommodate chosen state arrangement of
        // a/b switches, where false/true states depend on chosen left/right positions
        CharSequence widgetText = romantime.now(!ampm, ampmSeparator, !alignment);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        int opacityValue = sp.getInt("seekbar_opacity", 0);
        views.setInt(R.id.appwidget_bkgnd, "setBackgroundResource", opacity[opacityValue]);

        Intent intent;

        // This needs to be here rather than in onUpdate or updateAppWidget;
        // otherwise the widget won't properly respond to a click (i.e. tap)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            intent = new Intent(context, TimeDisplayWidgetConfigActivity.class);
        } else {
            intent = new Intent(context, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // This makes the entire widget clickable if all other views on the widget have android:clickable="false"
        views.setOnClickPendingIntent(R.id.appwidget_bkgnd, pendingIntent);

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
        if (
            action != null &&
            (
                action.equals(MINUTE_TICK) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                action.equals(Intent.ACTION_TIME_CHANGED) ||
                //
                // Update time if date changes in case there's a switch between STD Time and DST
                action.equals(Intent.ACTION_DATE_CHANGED) ||
                //
                // The following AlarmManager intent is only sent when the permission is granted,
                // not when the permission is revoked, and should only occur in Android 12 or 12L
                // due to use of USE_EXACT_ALARM permission.
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && action.equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED))
            )
        ) {
            // Treating a change in any of exact alarm permission, system time, or system timezone
            // as a minute tick insures immediate update of time display on such changes
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
            // While USE_EXACT_ALARM should make this superfluous, I imagine it could be used if
            // canScheduleExactAlarms() is somehow set to false, e.g. by the system running in low-power mode.
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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        int minwidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int textsize = (minwidth < 260) ? 28 : 34;
        RemoteViews views = updateTimeDisplay(context);
        views.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, textsize);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}