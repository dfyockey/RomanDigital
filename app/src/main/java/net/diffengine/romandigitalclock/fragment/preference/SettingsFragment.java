package net.diffengine.romandigitalclock.fragment.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import net.diffengine.romandigitalclock.MainActivity;
import net.diffengine.romandigitalclock.R;

import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static String postfix;

    public SettingsFragment () {
    }

    public SettingsFragment (Boolean isApp, int appWidgetId) {
        final String appPostfix = "";
        final String widgetPostfix = String.valueOf(appWidgetId);
        postfix = ( (isApp) ? appPostfix : widgetPostfix );
    }

    private void setSeparatorEnableState(SwitchPreferenceCompat pFormat) {
        SwitchPreferenceCompat pSeparator = findPreference("switch_separator" + postfix);
        if (pFormat.isChecked() == MainActivity.right) {
            //noinspection DataFlowIssue
            pSeparator.setChecked(MainActivity.left);
            pSeparator.setEnabled(false);
        } else {
            //noinspection DataFlowIssue
            pSeparator.setEnabled(true);
        }
    }

    Context prefManagerContext;
    PreferenceCategory category;
    SwitchPreferenceCompat pAlignment = null;

    private void addABSwitchPreference (String key, String aText, String bText) {
        SwitchPreferenceCompat pref = new SwitchPreferenceCompat(prefManagerContext);
        pref.setLayoutResource(R.layout.a_b_switch_layout);
        pref.setKey(key + postfix);
        pref.setDefaultValue(false);
        pref.setTitle(aText);
        pref.setSummary(bText);
        category.addPreference(pref);
    }

    private void addSeparator (@SuppressWarnings("SameParameterValue") String key) {
        Preference pref = new Preference(prefManagerContext);
        pref.setLayoutResource(R.layout.separator_layout);
        pref.setKey(key);
        category.addPreference(pref);
    }

    private void addListPreference (String key, String title, String[] entries, String[] entryValues, String defaultValue) {
        ListPreference pref = new ListPreference(prefManagerContext);
        pref.setIconSpaceReserved(true);    // Required for some devices that default this to false
        pref.setKey(key + postfix);
        pref.setTitle(title);

        ListPreference.SimpleSummaryProvider summaryProvider = ListPreference.SimpleSummaryProvider.getInstance();
        pref.setSummaryProvider(summaryProvider);

        pref.setEntries(entries);
        pref.setEntryValues(entryValues);
        pref.setValue(defaultValue);

        category.addPreference(pref);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        prefManagerContext = manager.getContext();
        PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

        category = new PreferenceCategory(prefManagerContext);
        category.setTitle("Time");
        category.setIconSpaceReserved(false);
        screen.addPreference(category);

        addABSwitchPreference("switch_format", "12 Hour", "24 Hour");
        addABSwitchPreference("switch_alignment", "Align to Center", "Align to Divider");
        addABSwitchPreference("switch_separator", ": for All", "· for AM\n: for PM");

        if (!postfix.isEmpty()) {
            addSeparator("S1");

            String[] timezoneIds = TimeZone.getAvailableIDs();
            addListPreference("list_timezone", "Time Zone", timezoneIds, timezoneIds, TimeZone.getDefault().getID() );

            String[] layoutEntries = {getString(R.string.tz_above_time), getString(R.string.time_only), getString(R.string.tz_below_time)};
            String[] layoutValues  = {"hi_label", "no_label", "lo_label"};

            addListPreference("list_widget_layout", "Display Layout", layoutEntries, layoutValues, layoutValues[1] );
        }

        setPreferenceScreen(screen);

        // At start of the activity, ensure that the separator switch is disabled and set to
        // left if the format switch is set to right (i.e. 24 hour format).
        //
        SwitchPreferenceCompat pFormat = findPreference("switch_format" + postfix);
        //noinspection DataFlowIssue
        setSeparatorEnableState(pFormat);

        if (postfix.isEmpty()) {
            //noinspection DataFlowIssue
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            String typefaceValue = sp.getString("list_typeface", "0");
            pAlignment = findPreference("switch_alignment");
            setAlignmentEnableState(typefaceValue);
        }
    }

    public void setAlignmentEnableState(String typefaceValue) {
        if (typefaceValue.equals("0")) {
            pAlignment.setEnabled(true);
        } else {
            pAlignment.setChecked(MainActivity.left);
            pAlignment.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if (preference.getKey().equals("switch_format" + postfix)) {
            //
            // Set separator switch enable and check states based on whether format switch state
            // is left or right (i.e. whether format is 12 or 24 hour). Implementation in code
            // of the enable/disable operation is needed because it is opposite to that provided
            // by the normal preference dependency attribute.
            //
            SwitchPreferenceCompat pFormat = (SwitchPreferenceCompat)preference;
            setSeparatorEnableState(pFormat);
        }
        return super.onPreferenceTreeClick(preference);
    }
}
