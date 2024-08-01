package net.diffengine.romandigitalclock;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.app_settings_frame, new SettingsFragment())
                    .replace(R.id.screen_settings_frame, new ScreenSettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey);

            // At start of the activity, ensure that the separator switch is disabled and set to
            // left if the format switch is set to right (i.e. 24 hour format).
            //
            SwitchPreferenceCompat pFormat = findPreference("chkbox_format");
            SwitchPreferenceCompat pSeparator = findPreference("chkbox_ampm_separator");
            if (pFormat.isChecked() == MainActivity.right) {
                pSeparator.setChecked(MainActivity.left);
                pSeparator.setEnabled(false);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(@NonNull Preference preference) {
            if (preference.getKey().equals("chkbox_format")) {
                // Set separator switch enable and check states based on whether format switch state
                // is left or right (i.e. whether format is 12 or 24 hour). Implementation in code
                // of the enable/disable operation is needed because it is opposite to that provided
                // by the normal preference dependency attribute.
                SwitchPreferenceCompat pFormat = (SwitchPreferenceCompat)preference;
                SwitchPreferenceCompat pSeparator = findPreference("chkbox_ampm_separator");
                if (pFormat.isChecked() == MainActivity.right) {
                    pSeparator.setChecked(MainActivity.left);
                    pSeparator.setEnabled(false);
                } else {
                    pSeparator.setEnabled(true);
                }
            }
            return super.onPreferenceTreeClick(preference);
        }
    }

    public static class ScreenSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.screen_preferences, rootKey);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Kick the widget so it'll update immediately based on
        // any preference changes made through this activity
        Intent kickstart = new Intent(this, TimeDisplayWidget.class);
        kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
        kickstart.setPackage(this.getPackageName());
        this.sendBroadcast(kickstart);
    }
}