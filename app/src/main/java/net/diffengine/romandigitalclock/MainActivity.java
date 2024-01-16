package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timeText;
    Handler myHandler;
    int clockdelay = 1000;
    WindowInsetsControllerCompat windowInsetsControllerCompat;

    private Runnable updatetime = new Runnable() {
        @Override
        public void run() {
            timeText = findViewById(R.id.timeText);
            timeText.setText(romantime.now(false));
            myHandler.postDelayed(updatetime,clockdelay);
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
    }

    protected void onResume() {
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
        super.onResume();
    }

    protected void onPause() {
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        super.onPause();
    }
}