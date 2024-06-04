package net.diffengine.romandigitalclock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

public class TimeDisplayWidgetConfigActivity extends AppCompatActivity {
    int appWidgetId;

    public TimeDisplayWidgetConfigActivity() {
        super(R.layout.activity_time_display_widget_config);
    }

    private void setResultCanceled() {
        // Enable cancellation of the configuration and,
        // if it's being added, app widget addition to home screen.
        // See https://developer.android.com/develop/ui/views/appwidgets/configuration#java
        // Provision of appwidget id in the extra data should also
        // prevent crash of TouchWiz on old Samsung devices at activity destruction.
        // See https://stackoverflow.com/a/40709721
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResultCanceled();
        setContentView(R.layout.activity_time_display_widget_config);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                fragmentTransaction.add(R.id.containerRequestFragment, ExactAlarmRequestFragment.class, null);
            }

            fragmentTransaction.replace(R.id.widget_settings, new SettingsFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Enable close of activity with an OK condition
            // See https://developer.android.com/develop/ui/views/appwidgets/configuration#java
            /*
                No need to update the widget here since it will be updated on receipt of
                the kickstart intent that will be broadcast in this activity's onPause method
            */
            Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Since the widget's alarms may have been canceled on pause,
        // broadcast an intent to kickstart the widget when it resumes
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}