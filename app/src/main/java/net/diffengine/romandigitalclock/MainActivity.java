package net.diffengine.romandigitalclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.ActionMenuView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView TimeDisplay;
    private AppCompatTextView TimeDisplaySizeControl;
    private View     bkgndView;
    private Fragment menuFragment;

    static boolean left  = false;
    static boolean right = true;

    private WindowInsetsControllerCompat windowInsetsControllerCompat;

    private final Context context = this;
    private final String UPDATE_DISPLAY = "net.diffengine.romandigitalclock.UPDATE_DISPLAY";

    // Aliases for option keys
    private static final String
            ampm = "chkbox_format",
            alignment = "chkbox_alignment",
            ampmSeparator = "chkbox_ampm_separator",
            keepon = "chkbox_keep_on",
            onlywhencharging = "chkbox_when_charging";

    // Storage for values of options loaded from settings
    private static final Map<String, Boolean> opt = new HashMap<>();
    static {
        opt.put(ampm, left);
        opt.put(alignment, left);
        opt.put(ampmSeparator, left);
        opt.put(keepon, false);
        opt.put(onlywhencharging, false);
    }

    //////////////////////////////////////////////////////////////////////

    private boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        // Are we charging / charged?
        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            return (
                    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
            );
        } else {
            // Returning `true` in this case should prevent the display from coming on
            // if it's already off, with the correct charging state being returned
            // on the next non-null batteryStatus.
            return true;
        }
    }

    private void setKeepScreenOn() {
        //noinspection DataFlowIssue
        boolean keepScreenOn = opt.get(keepon) && (!opt.get(onlywhencharging) || isCharging());
        bkgndView.setKeepScreenOn(keepScreenOn);
    }

    private Intent makeIntent (String action) {
        Intent i = new Intent();
            i.setAction(action);
            i.setPackage(context.getPackageName());
        return i;
    }

    int text_resize_attempt_count = 0;

    /** @noinspection DataFlowIssue*/
    private void updateTimeDisplay() {
        // Negate romantime.now arguments where needed to accommodate chosen state arrangement of
        // a/b switches, where false/true states depend on chosen left/right positions
        String now = romantime.now( opt.get(ampm), opt.get(ampmSeparator), !(opt.get(alignment)) );

        // IMPORTANT:
        // For the String returned by romantime.now to be correctly aligned in TimeDisplay textview,
        // TextDisplay.typeface MUST be set in activity_main.xml to 'monospace'

        float pxCurrentControlTextSize = TimeDisplaySizeControl.getTextSize();

        if (TimeDisplay.getVisibility() == View.INVISIBLE) {
            float pxDefaultControlTextSize = getResources().getDimension(R.dimen.timedisplay_size_control_default_textsize);

            /*/////
            //  Check of updateCount prevents infinitely sending broadcasts if an unforeseen
            //  occurrence keeps pxCurrentControlTextSize from falling below
            //  pxDefaultControlTextSize within a reasonable number of tries.
            //
            //  Casting of the px values to int prevents problems in the comparison if a fractional
            //  pixel value is generated in calculation of pxDefaultControlTextSize.
            *//////
            if ( (int)pxCurrentControlTextSize >= (int)pxDefaultControlTextSize && text_resize_attempt_count++ < R.dimen.text_resize_attempt_limit ) {
                sendBroadcast(makeIntent(UPDATE_DISPLAY));
            } else {
                TimeDisplay.setVisibility(View.VISIBLE);
            }
        }

        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxCurrentControlTextSize);
        TimeDisplay.setText(now);
        setKeepScreenOn();
    }

    //---------------------------------------------------------------
    /*
        Two BroadcastReceivers are needed to allow updateReceiver, which is only intended to receive
        a broadcast intent from this app itself, to be registered as RECEIVER_NOT_EXPORTED without
        interfering with the receipt of the system-broadcast ACTION_TIME_TICK intent
     */

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeDisplay();
        }
    };

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeDisplay();
        }
    };

    //---------------------------------------------------------------

    private void MainMenu(int visible) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (visible == View.VISIBLE) {
            ft.show(menuFragment);
        } else {
            ft.hide(menuFragment);
        }

        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /** @noinspection Convert2Lambda*/
    private final View.OnClickListener bkgndOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //MainMenu(View.INVISIBLE);
            View vToolbar = findViewById(R.id.my_toolbar);
            //vToolbar.setVisibility(View.INVISIBLE);
//            getSupportActionBar().hide();

            if (vToolbar.isShown()) {
                vToolbar.setVisibility(View.INVISIBLE);
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
            } else {
                vToolbar.setVisibility(View.VISIBLE);
                windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
            }
        }
    };

    /** @noinspection Convert2Lambda*/
    private final View.OnClickListener clockOCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //MainMenu(View.VISIBLE);
//            View vToolbar = findViewById(R.id.my_toolbar);
//            vToolbar.setVisibility(View.VISIBLE);
//            getSupportActionBar().show();
        }
    };


    //---------------------------------------------------------------

//    /* Before adding specific cases below, uncomment this interface */
//
//    private interface Case {
//        void run();
//    }

//    /* Put specific cases here if needed and add them to optCase below */
//
//    private class ExampleCase implements Case {
//        @Override
//        public void run() {
//            // Add code to be run in this case here
//        }
//    }
//
//    private static final Map<String, Case> optCase = new HashMap<>();
//    {
//        optCase.put("preference_key", new ExampleCase());
//    }

    /* Put anything to be executed for all cases in this method */
    /** @noinspection ReassignedVariable*/
    private void execCase(SharedPreferences sp, String key) {
        // Load changed setting value into option hashmap
        key = (key!=null) ? key : "null";
        opt.put(key, sp.getBoolean(key, false));

//        // Run Case for key, if any
//        if (optCase.containsKey(key)) {
//            //noinspection DataFlowIssue
//            optCase.get(key).run();
//        }
    }

    /** @noinspection Anonymous2MethodRef, Convert2Lambda */
    // HashMaps opt and optCase are used in supporting methods above to respond
    // to preference changes in lieu of using a lengthy switch statement here
    private final SharedPreferences.OnSharedPreferenceChangeListener prefChgListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sp, String key) {
            execCase(sp, key);
        }
    };

    //---------------------------------------------------------------

    private final View.OnClickListener timedisplayOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(context, v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true);
            }
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
            popup.show();
        }
    };

    private void showActivity(Class<?> cls) {
        Intent showActivityIntent = new Intent(context, cls);
        startActivity(showActivityIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnState = true;
        int itemId = item.getItemId();

        if(itemId == R.id.item_settings) {
            showActivity(SettingsActivity.class);
        } else if (itemId == R.id.item_about) {
            showActivity(AboutActivity.class);
        } else {
            returnState = super.onOptionsItemSelected(item);
        }

        return returnState;
    };

    private void setListeners() {
        TimeDisplay = findViewById(R.id.TimeDisplay);
        //TimeDisplay.setOnClickListener(clockOCL);

        bkgndView = findViewById(R.id.main_activity_bkgnd);
        bkgndView.setOnClickListener(bkgndOCL);

//        TimeDisplay.setOnClickListener(timedisplayOCL);
    }

    private void setupMainMenu(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            menuFragment = new SelectionMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.SelectionMenu, menuFragment, null)
                    .hide(menuFragment)
                    .commit();
        } else if (menuFragment == null) {
            // Recover reference to menuFragment following an orientation change
            menuFragment = getSupportFragmentManager().findFragmentById(R.id.SelectionMenu);
        }
    }

    private void getSettings() {
        // Load setting values into options hashmap and setup initial state based thereon
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefChgListener);
        for (String key : opt.keySet()) {
            execCase(sp, key);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make MainActivity window extend beneath the System Bars to the edges of the screen.
        // From info at https://stackoverflow.com/questions/49190381/fullscreen-app-with-displaycutout
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        windowInsetsControllerCompat = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // System Bars Behavior needs to be set as follows to prevent opening a Quick Settings Panel
        // on at least some Android versions when swiping down from the screen top while the System
        // Bars are hidden, thereby allowing the swipe to shoe the System Bars instead:
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        setListeners();
        setupMainMenu(savedInstanceState);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setVisibility(View.INVISIBLE);

        // Noted at https://medium.com/javarevisited/how-to-get-status-bar-height-in-android-programmatically-c127ad4f8a5d
        int statusBarHeight = getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android"));

        int navBarHeight = getResources().getDimensionPixelSize(
                getResources().getIdentifier("navigation_bar_height", "dimen", "android"));

//        ViewGroup.LayoutParams layoutParams = myToolbar.getLayoutParams();
//        int height = layoutParams.height + statusBarHeight;
//        layoutParams.height = height;
//        myToolbar.setLayoutParams(layoutParams);

        int titleMarginTop = myToolbar.getTitleMarginTop();
        myToolbar.setTitleMarginTop( titleMarginTop + statusBarHeight );

//        int orientation = getResources().getConfiguration().orientation;
//        if ( orientation == Configuration.ORIENTATION_LANDSCAPE ) {
//            myToolbar.setPadding(navBarHeight, 0, navBarHeight, 0);
//        }

        //myToolbar.setPadding(0, statusBarHeight, 0, 0);

        // Set padding at top and bottom of layout to push the Toolbar down
        // while keeping the TimeDisplay centered on the screen
//        ConstraintLayout main = findViewById(R.id.main_activity_bkgnd);
//        main.setPadding(0, statusBarHeight, 0, statusBarHeight);


        // VERY Important!
        // Needed so the menu resource is loaded into the toolbar!
        myToolbar.inflateMenu(R.menu.main_menu);

        //setSupportActionBar(myToolbar);

        // Loop through Views contained in myToolbar as suggested at
        // https://snow.dog/blog/how-to-dynamicaly-change-android-toolbar-icons-color (which is
        // Apache 2.0 licensed at https://gist.github.com/chomi3/7e088760ef7bca10430e), but set
        // icon colors by setting their Tint as suggested at
        // https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically
        int childCount = myToolbar.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View view = myToolbar.getChildAt(i);
            if (view instanceof ActionMenuView) {
                ActionMenuView actionMenuView = (ActionMenuView)view;
                actionMenuView.setPadding(0, statusBarHeight, 0, 0);
                Menu actionMenu = actionMenuView.getMenu();
                for (int j = 0; j < actionMenu.size(); ++j) {
                    MenuItem menuItem = actionMenu.getItem(j);
                    Drawable icon = menuItem.getIcon();
                    // Icon needs to be "wrapped" to facilitate use across different API levels.
                    // See https://developer.android.com/reference/androidx/core/graphics/drawable/DrawableCompat#wrap(android.graphics.drawable.Drawable)
                    DrawableCompat.setTint(DrawableCompat.wrap(icon), getResources().getColor(R.color.clock_red));
                }
            }
        }

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean returnState = true;
                int itemId = item.getItemId();

                if(itemId == R.id.item_settings) {
                    showActivity(SettingsActivity.class);
                } else if (itemId == R.id.item_about) {
                    showActivity(AboutActivity.class);
                } else {
                    returnState = false;
                }

                return returnState;
            }
        });

//        View toolbarMenu = myToolbar.findViewById(R.id.toolbar_menu);
//        //Menu toolbarMenu = myToolbar.getMenu();
//        //Toast.makeText(context, new Integer(toolbarMenu.size()).toString(), Toast.LENGTH_SHORT).show();
//
//        android.content.res.Resources res = getResources();
//
//        //MenuItem menuItem = myToolbar.findViewById(R.id.item_settings);
//        MenuItem menuItem = toolbarMenu.findViewById(R.id.item_settings);
//
//        Drawable drawableGear = menuItem.getIcon();
//        DrawableCompat.setTint(DrawableCompat.wrap(drawableGear), res.getColor(R.color.clock_red));

        //VectorDrawableCompat gear = VectorDrawableCompat.create(res, R.drawable.ic_settings_24dp, null);
        //gear.setColorFilter(res.getColor(R.color.clock_red), PorterDuff.Mode.DST);

        //View itemGear = myToolbar.findViewById(R.id.item_settings);

        //menuGear.setIcon(gear);

        getSettings();


        //getSupportActionBar().hide();
    }

    //---------------------------------------------------------------

    protected void onPause() {
        unregisterReceiver(updateReceiver);
        unregisterReceiver(broadcastReceiver);
        findViewById(R.id.my_toolbar).setVisibility(View.INVISIBLE);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());

        TimeDisplay.setVisibility(View.INVISIBLE);
        Toolbar vToolbar = findViewById(R.id.my_toolbar);
        vToolbar.setVisibility(View.INVISIBLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            vToolbar.setElevation(20F);
//        }

        String maxtime_fill = getString((opt.get(ampm)) ? R.string.civ_fill : R.string.mil_fill);
        TimeDisplaySizeControl = findViewById(R.id.timedisplay_size_control);
        TimeDisplaySizeControl.setText(maxtime_fill);
        TimeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, TimeDisplaySizeControl.getTextSize());

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        ContextCompat.registerReceiver(context, updateReceiver, new IntentFilter(UPDATE_DISPLAY), ContextCompat.RECEIVER_NOT_EXPORTED);
        text_resize_attempt_count = 0;
        sendBroadcast(makeIntent(UPDATE_DISPLAY));
    }
}
