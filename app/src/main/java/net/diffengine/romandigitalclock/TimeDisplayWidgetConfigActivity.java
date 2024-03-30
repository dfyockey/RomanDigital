package net.diffengine.romandigitalclock;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TimeDisplayWidgetConfigActivity extends AppCompatActivity implements View.OnClickListener {
    Button      btnSetPermission;
    ImageButton imgbtnCloseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK);
        setContentView(R.layout.activity_time_display_widget_config);

        btnSetPermission    = (Button) setViewListener(R.id.btnSetPermission);
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

        if (viewId == R.id.btnSetPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent reqIntent = new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                reqIntent.setData(Uri.parse("package:net.diffengine.romandigitalclock"));
                startActivity(reqIntent);
            }
        } else if (viewId == R.id.imgbtnCloseActivity) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            permission = "Granted";
        } else {
            permission = "Denied";
        }
        btnSetPermission.setText(getString(R.string.button_exact_time_permission) + " (" + permission + ")");

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Since the Widget's alarms were probably, broadcast an intent to kickstart the Widget
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