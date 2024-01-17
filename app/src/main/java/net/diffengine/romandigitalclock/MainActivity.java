package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView txtTime;
    private Handler  myHandler;
    private boolean  ampm;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private final Runnable updatetime = new Runnable() {
        @Override
        public void run() {
            int updatedelay_in_ms = 1000;
            txtTime.setText(romantime.now(ampm));
            myHandler.postDelayed(updatetime, updatedelay_in_ms);
        }
    };

    private void showSettings() {
        Intent showSettingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(showSettingsIntent);
    }

    View.OnClickListener vOCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) { showSettings(); }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener prefChgListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sp, String key) {
            switch( (key!=null) ? key : "null") {
                case "chkbox_ampm":
                    ampm = sp.getBoolean(key, false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        myHandler = new Handler(Looper.getMainLooper());
        myHandler.post(updatetime);

        txtTime = findViewById(R.id.txtTime);
        txtTime.setOnClickListener(vOCL);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefChgListener);
        ampm = sp.getBoolean("chkbox_ampm", false);
    }

    protected void onPause() {
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }
}