package net.diffengine.romandigitalclock;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TimeDisplayWidgetConfigActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton imgbtnCloseActivity;

    public TimeDisplayWidgetConfigActivity() {
        super(R.layout.activity_time_display_widget_config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent crash of TouchWiz on old Samsung devices at activity destruction.
        // See https://stackoverflow.com/a/40709721
        int id = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        setResult(RESULT_OK, result);

        setContentView(R.layout.activity_time_display_widget_config);

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.containerRequestFragment, ExactAlarmRequest.class, null)
                    .commit();
        }

        imgbtnCloseActivity = (ImageButton) setViewListener(R.id.imgbtnCloseActivity);
    }

    private View setViewListener(int id) {
        View v = findViewById(id);
        v.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.imgbtnCloseActivity) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Since the Widget's alarms may be canceled on pause,
        // broadcast an intent to kickstart the Widget when it resumes
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