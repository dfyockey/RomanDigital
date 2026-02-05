/*
 * TimeDisplayWidget.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024-2025 David Yockey
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.getColor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

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

    // While both of the following intent actions will cause update of the time, since a change in
    // settings may change the time display, the SETTINGS_KICK action indicative of such a change
    // also causes update of widget background opacity.
    public static final String MINUTE_TICK = "net.diffengine.romandigitalclock.MINUTE_TICK";
    public static final String SETTINGS_KICK = "net.diffengine.romandigitalclock.SETTINGS_KICK";

    ///////
    // Convertion to indices obviates need to do string comparisons to set up both layout and
    // label color without having to change existing preference data format from strings to ints.
        private static final Map<String, Integer> map = Map.of(
                "hi_label", 0,
                "no_label", 1,
                "lo_label", 2
        );

        private static Integer getLayoutConfigId(String layoutMoniker) {
            return map.get(layoutMoniker);
        }
    //
    ///////

    private static final int[] typefaceIds = {R.id.appwidget_clock_mono, R.id.appwidget_clock_sans, R.id.appwidget_clock_serif};

    private static int appwidget_clock;

    // Unused action parameter is retained because it may be used later
    // to handle SETTINGS_KICK or another power-saving action.
    /** @noinspection unused*/
    private static RemoteViews updateTimeDisplay(Context context, String action, int appWidgetId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ampm          = sp.getBoolean("switch_format" + appWidgetId, false);
        boolean ampmSeparator = sp.getBoolean("switch_separator" + appWidgetId, false);
        boolean alignment     = sp.getBoolean("switch_alignment" + appWidgetId, false);
        String  tzId          = sp.getString("list_timezone" + appWidgetId, TimeZone.getDefault().getID());
        String layoutMoniker  = sp.getString("list_widget_layout" + appWidgetId, "no_label" );
        int layoutId          = R.layout.time_display_widget;
        int layoutConfigId    = getLayoutConfigId(layoutMoniker);
        appwidget_clock       = typefaceIds[Integer.parseInt(sp.getString("list_typeface" + appWidgetId, "0"))];

        // Negate romantime.now arguments where needed to accommodate chosen state arrangement of
        // a/b switches, where false/true states depend on chosen left/right positions
        CharSequence widgetText = romantime.now(!ampm, ampmSeparator, !alignment, tzId);
//        widgetText = "VIII:XXXVIII";
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        // Setup layout
            // Clear all typefaces
            for (int typefaceId : typefaceIds) {
                views.setViewVisibility(typefaceId, GONE);
            }

            views.setViewVisibility(appwidget_clock, VISIBLE);
            views.setTextViewText(appwidget_clock, widgetText);
            views.setViewVisibility(R.id.appwidget_tzlabel_hi, (layoutConfigId == 0 ? VISIBLE : GONE));
            views.setViewVisibility(R.id.appwidget_tzlabel_lo, (layoutConfigId == 2 ? VISIBLE : GONE));
        //

        // Update the widget background on instantiation, system boot, or change of settings
        /*
            Unfortunately, all of the preferences need to be reset for call to updateTimeDisplay so
            the widget is properly displayed following a complete reset of the device's launcher, at
            least on Samsung devices running One UI 6.1 on Android 14, or TouchWiz on Android 5.1.

            The check for SETTINGS_KICK is being kept to facilitate possible implementation of an
            option to save power if it's determined by the user that their launcher doesn't cause
            the issue observed with Samsung's launchers.
        */
//      if (action.equals(SETTINGS_KICK)) {
            int opacityValue = sp.getInt("seekbar_opacity" + appWidgetId, 0);
            views.setInt(R.id.appwidget_bkgnd, "setBackgroundResource", opacity[opacityValue]);

            int widget_text_color_resource;
            if (opacityValue < 5) {
                widget_text_color_resource = R.color.widgetText_LoOpacityBkgnd;
            } else {
                widget_text_color_resource = R.color.widgetText_HiOpacityBkgnd;
            }
            views.setInt(appwidget_clock, "setTextColor", getColor(context, widget_text_color_resource));

            // Set label color
            if (layoutConfigId != 1) {
                int tzlabel = 0;
                if (layoutConfigId == 0) {
                    tzlabel = R.id.appwidget_tzlabel_hi;
                } else if (layoutConfigId == 2) {
                    tzlabel = R.id.appwidget_tzlabel_lo;
                }
                views.setTextViewText(tzlabel, tzId);
                views.setInt(tzlabel, "setTextColor", getColor(context, widget_text_color_resource));
            }

            Bundle widgetOptions = AppWidgetManager.getInstance(context).getAppWidgetOptions(appWidgetId);
            setTimeTextSize(context, views, appWidgetId, widgetOptions);
//        }

        Intent intent;

        // This needs to be here rather than in onUpdate or updateAppWidget;
        // otherwise the widget won't properly respond to a click (i.e. tap)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            intent = new Intent(context, WidgetSettingsActivity.class);
            intent.setAction("click" + appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        } else {
            intent = new Intent(context, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // This makes the entire widget clickable if all other views on the widget have android:clickable="false"
        views.setOnClickPendingIntent(R.id.appwidget_bkgnd, pendingIntent);

        return views;
    }

    private void onTick (Context context, String action, int[] appWidgetIds) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetName = new ComponentName(context.getPackageName(), TimeDisplayWidget.class.getName());

        // SETTINGS_KICK may be received without any extra appWidgetIds array;
        // if so, get the array of current appWidgetIds
        if (appWidgetIds == null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(widgetName);
        }

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, action, appWidgetId);
        }

        int[] allAppWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(widgetName);
        alarmPendingIntent = getPendingIntent(context, allAppWidgetIds);
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
                action.equals(SETTINGS_KICK) ||
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
            int[] appWidgetIds = null;
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
                appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            }

            // Treating a change in any of exact alarm permission, system time, or system timezone
            // as a minute tick insures immediate update of time display on such changes
            onTick(context, action, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, SETTINGS_KICK, appWidgetId);
        }
    }

    private static PendingIntent getPendingIntent(Context context, int[] appWidgetIds) {
        Intent tickIntent = new Intent(context, TimeDisplayWidget.class);
        tickIntent.setAction(MINUTE_TICK);
        tickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        return PendingIntent.getBroadcast(context, 0, tickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
        if (alarmManager != null && alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String action, int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, updateTimeDisplay(context, action, appWidgetId));
    }

    static private int findMaxTextSize(Context context, Rect maxRect, String refText) {
        /*
            Use a binary search to find the largest TextSize such that the provided reference text
            refText fits within the provided rectangle maxRect, where the variable loSize will
            contain the final result.

            Note:
                The search may exit with rect.width() greater than maxRect.width() if the exit
                condition is met when hiSize is set to midSize. This isn't an issue because the
                smaller rectangle around text the size of loSize, i.e the search result, will still
                fit within the max width and height of the maxRect.
        */
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setTypeface(Typeface.MONOSPACE);

        int loSize = 0;
        int hiSize = 1024;   // Arbitrarily selected largest permissible text size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        while (loSize + 1 < hiSize) {
            int midSize = (hiSize + loSize) / 2;

            int textSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                textSize = midSize;
            } else {
                // Convert midSize, in DIP units, to PX units to set the paint text size so that
                // getTextBounds will generate an accurate rectangle
                textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, midSize, displayMetrics);
            }

            paint.setTextSize(textSize);
            paint.getTextBounds(refText, 0, refText.length(), rect);

            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                rect.bottom += paint.getFontSpacing();
            }

            if ((rect.width() >= maxRect.width()) || (rect.height() >= maxRect.height())) {
                hiSize = midSize;
            } else {
                loSize = midSize;
            }
        }
        return loSize;
    }

    static private int calcTimeDisplayTextSize(Context context, int appWidgetId, Bundle bundle) {
        // Get text of max length equal to the clock's max width display
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ampm = sp.getBoolean("switch_format" + appWidgetId, false);
        String maxlengthText = context.getString((ampm == MainActivity.left) ? R.string.civ_fill : R.string.mil_fill);
        int widgetWidth;
        int widgetHeight;

        // Set up Rect containing widget rectangle into which to fit text
        // 1) Get widget width and height in DIP
        boolean isPortrait = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        String widthOption = (isPortrait) ? AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH : AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
        String heightOption = (isPortrait) ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
        int widgetWidthDp = bundle.getInt(widthOption);
        int widgetHeightDp = bundle.getInt(heightOption);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 2) Assign the DIP width and height directly to the variables used in setting up Rect
            widgetWidth = widgetWidthDp;
            widgetHeight = widgetHeightDp;
            // 3) Set Rect using the unconverted values...
        } else {
            // 2) Convert width and height to PX
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            widgetWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widgetWidthDp, displayMetrics);
            widgetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widgetHeightDp, displayMetrics);
            // 3) Set Rect using PX width and height for proper comparison to Rect in PX units returned
            //    from paint.getTextBounds in findMaxTextSize...
        }
        Rect maxRect = new Rect(0, 0, widgetWidth-1, widgetHeight-1);

        return findMaxTextSize(context, maxRect, maxlengthText);
    }

    static private void setTimeTextSize(Context context, RemoteViews views, int appWidgetId, Bundle widgetOptions) {
        int textsize = calcTimeDisplayTextSize(context, appWidgetId, widgetOptions);
        int fudgefactor = 3;    // Conservative value for compensation of possible error in calculated text size
                                // (observed on a Nexus 6 AVD running API 24; value of 1 was sufficent to compensate)
        views.setTextViewTextSize(appwidget_clock, TypedValue.COMPLEX_UNIT_DIP, textsize-fudgefactor);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {

        // This call needs to be here rather than just instantiating a new RemoteViews
        // object so the display will be updated for each of the multiple calls to
        // onAppWidgetOptionsChanged that may occur while the user is resizing a widget
        RemoteViews views = updateTimeDisplay(context, SETTINGS_KICK, appWidgetId);

        setTimeTextSize(context, views, appWidgetId, newOptions);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}