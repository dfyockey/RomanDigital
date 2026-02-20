/*
 * TimeStyleFragment.java
 * - This file is part of the Android app RomanDigital
 *   and was derived from a fragment removed from AppSettingsActivity.java
 *
 * Copyright © 2024-2026 David Yockey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.diffengine.romandigitalclock.fragment.preference;

import static java.util.Arrays.copyOfRange;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class TimeStyleFragment extends PreferenceFragmentCompat {

    public String postfix;

    // A no-args constructor is needed for reconstruction on device orientation change
    public TimeStyleFragment() {
        /* NOP */
    }

    public TimeStyleFragment(int appWidgetId) {
        postfix = ( (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) ? String.valueOf(appWidgetId) : "" );
    }

    Context prefManagerContext;
    PreferenceCategory category;

    private void addColorDialogPreference() {
        ColorDialogPreference colorPref = new ColorDialogPreference(prefManagerContext, getChildFragmentManager());
        colorPref.setIconSpaceReserved(true);    // Required for some devices that default this to false
        colorPref.setTitle("Color");
        colorPref.setKey("hexcolor");
        category.addPreference(colorPref);
    }

    private void addTypefaceListPreference () {
        ListPreference pref = new ListPreference(prefManagerContext);
        pref.setIconSpaceReserved(true);    // Required for some devices that default this to false
        pref.setKey("list_typeface" + postfix);
        pref.setTitle("Typeface");
        String[] typefaces = {"device default", "monospace", "sans", "serif"};
        String[] typefaceValues = {"0", "1", "2", "3"};

        if (!postfix.isEmpty()) {
            // For use with widgets, which are always 'device default' from which the other
            // typefaces are selected, drop the 'device default' typeface...
            typefaces = copyOfRange(typefaces, 1, typefaces.length);
            // ...and drop the last value
            typefaceValues = copyOfRange(typefaceValues, 0, typefaceValues.length - 1);
        }
        pref.setEntries(typefaces);
        pref.setEntryValues(typefaceValues);
        pref.setValue(typefaceValues[0]);

        ListPreference.SimpleSummaryProvider summaryProvider = ListPreference.SimpleSummaryProvider.getInstance();
        pref.setSummaryProvider(summaryProvider);

        pref.setOnPreferenceChangeListener((preference, newValue) -> {
            FragmentManager supportFragmentManager = requireActivity().getSupportFragmentManager();
            TimeFormatFragment timeFormatFragment = (TimeFormatFragment) supportFragmentManager.findFragmentById(R.id.settings_frame);

            if (timeFormatFragment != null) {
                timeFormatFragment.setAlignmentEnableState(newValue.toString());
            }

            return true;
        });

        category.addPreference(pref);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        if (savedInstanceState != null) {
            postfix = savedInstanceState.getString("postfix");
        }

        PreferenceManager manager = getPreferenceManager();
        prefManagerContext = manager.getContext();
        PreferenceScreen screen = manager.createPreferenceScreen(prefManagerContext);

        category = new PreferenceCategory(prefManagerContext);
        category.setIconSpaceReserved(false);
        category.setTitle("Style");
        screen.addPreference(category);

        if (postfix.isEmpty()) {
            addColorDialogPreference();
        }

        addTypefaceListPreference();

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("postfix", postfix);
    }
}
