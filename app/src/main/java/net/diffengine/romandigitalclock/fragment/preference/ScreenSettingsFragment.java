package net.diffengine.romandigitalclock.fragment.preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import net.diffengine.romandigitalclock.R;

public class ScreenSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.screen_preferences, rootKey);
    }
}
