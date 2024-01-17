package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtTime;
    Handler  myHandler;
    int      clockdelay = 1000;
    WindowInsetsControllerCompat windowInsetsControllerCompat;

    private Runnable updatetime = new Runnable() {
        @Override
        public void run() {
            txtTime.setText(romantime.now(false));
            myHandler.postDelayed(updatetime,clockdelay);
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
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }

    protected void onPause() {
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }
}