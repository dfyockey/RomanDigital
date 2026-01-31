package net.diffengine.romandigitalclock.fragment.preference;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import net.diffengine.romandigitalclock.ColorDialogPreference;
import net.diffengine.romandigitalclock.R;

import java.util.Objects;

public class DisplayColorFragment extends PreferenceFragmentCompat {

    public String postfix;

    public DisplayColorFragment (int appWidgetId) {
        super();
        postfix = ( (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) ? String.valueOf(appWidgetId) : "" );
    }

    Context prefManagerContext;
    PreferenceCategory category;

    private void addTypefaceListPreference () {
        ListPreference pref = new ListPreference(prefManagerContext);
        pref.setIconSpaceReserved(true);    // Required for some devices that default this to false
        pref.setKey("list_typeface");
        pref.setTitle("Typeface");
        String[] typefaces = {"monospace", "sans", "serif"};
        String[] typefaceValues = {"0", "1", "2"};

        pref.setEntries(typefaces);
        pref.setEntryValues(typefaceValues);
        pref.setValue(typefaceValues[0]);

        ListPreference.SimpleSummaryProvider summaryProvider = ListPreference.SimpleSummaryProvider.getInstance();
        pref.setSummaryProvider(summaryProvider);

        pref.setOnPreferenceChangeListener((preference, newValue) -> {
            FragmentManager supportFragmentManager = requireActivity().getSupportFragmentManager();
            SettingsFragment settingsFragment = (SettingsFragment) supportFragmentManager.findFragmentById(R.id.app_settings_frame);

            if (settingsFragment != null) {
                settingsFragment.setAlignmentEnableState(newValue.toString());
            }

            return true;
        });

        category.addPreference(pref);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        prefManagerContext = manager.getContext();
        PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

        category = new PreferenceCategory(prefManagerContext);
        category.setIconSpaceReserved(false);
        category.setTitle("Style");
        screen.addPreference(category);

        ColorDialogPreference colorPref = new ColorDialogPreference(prefManagerContext, getChildFragmentManager());
        colorPref.setTitle("Time Color");
        colorPref.setKey("hexcolor");
        category.addPreference(colorPref);

        if (postfix.isEmpty()) {
            addTypefaceListPreference();
        }

        setPreferenceScreen(screen);
    }

    public void updateDialogTimeDisplayPreview() {
        ColorDialogPreference colorDialogPreference = category.findPreference("hexcolor");
        ColorDialogPreference.ColorDialogFragment colorDialogFragment = Objects.requireNonNull(colorDialogPreference).getColorDialogFragment();

        if (colorDialogFragment != null) {
            Dialog dialog = colorDialogFragment.getDialog();
            if (dialog != null && dialog.isShowing()) {
                colorDialogFragment.updatePreviewTime();
            }
        }
    }
}
