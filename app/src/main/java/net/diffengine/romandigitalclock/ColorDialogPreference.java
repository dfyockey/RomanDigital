/*
 * ColorDialogPreference.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2025 David Yockey
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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

public class ColorDialogPreference extends Preference implements Preference.OnPreferenceClickListener {
    FragmentManager     m_fm;
    SharedPreferences   m_sp;

    @SuppressWarnings({"UnusedDeclaration"})
    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, FragmentManager fm) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(fm);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, FragmentManager fm) {
        super(context, attrs, defStyleAttr);
        init(fm);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, FragmentManager fm) {
        super(context, attrs);
        init(fm);
    }

    public ColorDialogPreference(@NonNull Context context, FragmentManager fm) {
        super(context);
        init(fm);
    }

    private void init(FragmentManager fm) {
        m_fm = fm;
        setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        m_sp = getSharedPreferences();
        if (m_sp != null) {
            String colorhexcode = m_sp.getString(getKey(), "");
            setSummary(colorhexcode);
        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        ColorDialogFragment colorDialogFragment = new ColorDialogFragment(preference, getKey());
        colorDialogFragment.show(m_fm, colorDialogFragment.TAG);
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public static class ColorDialogFragment extends DialogFragment {

        SharedPreferences sp;
        Preference pref;
        String key;

        public ColorDialogFragment(Preference pref, String key) {
            this.sp = pref.getSharedPreferences();
            this.pref = pref;
            this.key = key;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View v = layoutInflater.inflate(R.layout.color_dialog_layout, null);
            EditText etHexcode = v.findViewById(R.id.etHexcode);
            TextView tvPreview = v.findViewById(R.id.tvPreview);

            // Use the Builder class for convenient dialog construction.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Color")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User okays the selected color.
                        String colorText = etHexcode.getText().toString();
                        if (SettingsActivity.isHexColor(colorText)) {
                            sp.edit().putString(key, colorText).commit();
                            pref.setSummary(colorText);
                            dialog.dismiss();
                        } else {
                            // Placing this here instead of in an onCancel method prevents it
                            // from from firing if the user taps outside the dialog to cancel.
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Invalid Color Value")
                                    .setMessage("Value must be a six-character hexadecimal.")
                                    .show();
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            // Customize Dialog View //
            String hexcolor = sp.getString(key, "FFFFFF");
            etHexcode.setText(hexcolor);
            tvPreview.setTextColor(Color.parseColor("#" + hexcolor));
            etHexcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // NOP
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (SettingsActivity.isHexColor(s.toString())) {
                        tvPreview.setTextColor(Color.parseColor("#" + s));
                    } else {
                        tvPreview.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // NOP
                }
            });
            // End of Dialog View Customization //

            builder.setView(v);

            AlertDialog alertDialog = builder.create();
            return alertDialog;
        }

        public String TAG = "ColorDialogFragment";
    }
    //////////////////////////////////////////////////////////////////////////////////////////
}