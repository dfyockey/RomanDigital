package net.diffengine.romandigitalclock;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ExactAlarmRequest extends Fragment implements View.OnClickListener {
    Button btnSetPermission;

    public ExactAlarmRequest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick (View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent reqIntent = new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            reqIntent.setData(Uri.parse("package:net.diffengine.romandigitalclock"));
            startActivity(reqIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exact_alarm_request, container, false);
        btnSetPermission = (Button) v.findViewById(R.id.btnSetPermission2);
        btnSetPermission.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            permission = "Granted";
        } else {
            permission = "Denied";
        }
        btnSetPermission.setText(getString(R.string.button_exact_alarm_permission, permission));
    }
}