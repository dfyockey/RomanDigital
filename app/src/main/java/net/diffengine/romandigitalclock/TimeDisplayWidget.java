package net.diffengine.romandigitalclock;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class TimeDisplayWidget extends AppWidgetProvider {

    //private Handler myHandler = null;
    private CharSequence widgetText = ":";
//    Handler myHandler;

//    private final Runnable updatetime = new Runnable() {
//        @Override
//        public void run() {
//            noinspection DataFlowIssue
//            String now = romantime.now( true, true, false );
//            widgetText = now;
//        }
//    };

    /*
    private void updateWidgetTimeDisplay(Context context) {
        //noinspection DataFlowIssue
        String now = romantime.now( true, true, false );
        widgetText = now;

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.widgetTimeDisplay, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
    */

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //updateWidgetTimeDisplay(context);
            //int appWidgetId = R.id.widgetTimeDisplay;
            //Intent intentUpdate = new Intent(context, TimeDisplayWidget.class);
            //intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

//            Context appContext = context.getApplicationContext();
            //String pkgname = context.getPackageName();
            ComponentName component = new ComponentName(context, TimeDisplayWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(component));

/*            //noinspection DataFlowIssue
            String now = romantime.now( true, true, false );
            widgetText = now;

            widgetText = "New Time...";

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
            views.setTextViewText(R.id.widgetTimeDisplay, widgetText);

            // Instruct the widget manager to update the widget
            int appWidgetId = R.id.RomanDigitalWidget;
            Context appContext = context.getApplicationContext();
            AppWidgetManager.getInstance(appContext).updateAppWidget(appWidgetId, views);*/
        }
    };

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.widgetTimeDisplay);

        //noinspection DataFlowIssue
        String now = romantime.now( true, true, true );
        //widgetText = now.substring(1, now.length()-2);
        widgetText = now;

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_display_widget);
        views.setTextViewText(R.id.widgetTimeDisplay, widgetText);

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
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
//        if (myHandler == null) {
//            myHandler = new Handler(Looper.getMainLooper());
//            myHandler.post(updatetime);
//        }

        Context appContext = context.getApplicationContext();
        appContext.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {
        Context appContext = context.getApplicationContext();
        //registerReceiver(appContext, broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK), 0);
        appContext.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onDisabled(Context context) {
        Context appContext = context.getApplicationContext();
        try {
            appContext.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            System.out.println("Broadcast receiver already unregistered.");
        }
    }
}