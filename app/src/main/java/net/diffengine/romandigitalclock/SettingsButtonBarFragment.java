/*
 * SettingsButtonBarFragment.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024 David Yockey
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

package net.diffengine.romandigitalclock;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsButtonBarFragment extends Fragment implements View.OnClickListener {
    Button btnCancel;
    Button btnSave;

    public SettingsButtonBarFragment() {
        // If the layout isn't provided to the superclass, it's necessary to use a form of
        // fragmentTransaction.add or .replace that takes the fragment class rather than an
        // instance thereof to instantiate the fragment in an activity's onCreate method.
        // Otherwise, onCreateView will never be called.
        super(R.layout.fragment_settings_button_bar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick (View v) {

        Activity activity = requireActivity();

        // Get the appWidgetId if we're in a widget config activity
        //
        //noinspection ReassignedVariable
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        String cls = activity.getComponentName().getClassName();
        if (cls.equals("net.diffengine.romandigitalclock.TimeDisplayWidgetConfigActivity")) {
            TimeDisplayWidgetConfigActivity widgetConfigActivity = (TimeDisplayWidgetConfigActivity) activity;
            appWidgetId = widgetConfigActivity.appWidgetId;
        }

        if (v == btnCancel) {
            // Add code to reset settings here
        } else if (v == btnSave) {
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Provision of appwidget id in the extra data should prevent crash of some UIs
                // (e.g. TouchWiz on old Samsung devices) at activity destruction.
                // See https://stackoverflow.com/a/40709721
                Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                activity.setResult(RESULT_OK, result);
            }
        } else {
            // In case of some utterly stupid modification... :)
            return;
        }

        activity.finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_button_bar, container, false);

        btnCancel = v.findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(this);

        btnSave = v.findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        return v;
    }
}