/*
 * ColorDialogPreference.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright © 2025-2026 David Yockey
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

import static net.diffengine.romandigitalclock.MainActivity.alignment;
import static net.diffengine.romandigitalclock.MainActivity.ampm;
import static net.diffengine.romandigitalclock.MainActivity.ampmSeparator;
import static net.diffengine.romandigitalclock.MainActivity.left;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

import java.util.TimeZone;

public class ColorDialogPreference extends Preference implements Preference.OnPreferenceClickListener {
    FragmentManager     m_fm;
    SharedPreferences   m_sp;
    ColorDialogFragment colorDialogFragment;
    static public final String UPDATE_PREVIEW = "net.diffengine.romandigitalclock.UPDATE_PREVIEW";

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

    public ColorDialogFragment getColorDialogFragment() {
        return colorDialogFragment;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        m_sp = getSharedPreferences();
        if (m_sp != null) {
            String hexcolor = MainActivity.getHexColor(getContext(), m_sp, getKey());
            setSummary("#" + hexcolor);
        }

        colorDialogFragment = (ColorDialogFragment) m_fm.findFragmentByTag(ColorDialogFragment.TAG);
        if(colorDialogFragment != null) {
            colorDialogFragment.setPrefs(this);
        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        colorDialogFragment = new ColorDialogFragment(preference);
        colorDialogFragment.show(m_fm, ColorDialogFragment.TAG);
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public static class ColorDialogFragment extends DialogFragment {

        SharedPreferences sp;
        Preference pref;
        String key;
        String hexcolor;
        ColorSeekBarView[] colorSeekBarViews;
        private AlertDialog alertDialog;
        private TextView tvPreview;
        private AppCompatTextView PreviewDisplaySizeControl;

        public ColorDialogFragment() {
            /* NOP */
        }

        public ColorDialogFragment(Preference preference) {
            setPrefs(preference);
            hexcolor = String.valueOf(pref.getSummary()).substring(1);
            key = pref.getKey();
        }

        // Call from the associated preference's onAttach method to reset pref after a configuration change
        private void setPrefs(Preference preference) {
            pref = preference;
            sp = pref.getSharedPreferences();
        }

        public Dialog getDialog() {
            return alertDialog;
        }

        ///// onCreateDialog & onStart Support Methods ///////////////////////////////////////
        private void setProgress(ColorSeekBarView colorBar, String hexcolor, int startIndex, int endIndex) {
            colorBar.setProgress(Integer.valueOf(hexcolor.substring(startIndex,endIndex), 16));
        }

        private void setProgress(ColorSeekBarView[] colorBars, String hexcolor) {
            if (AppSettingsActivity.isHexColor(hexcolor)) {
                int i = 0;
                for (ColorSeekBarView colorbar:colorBars) {
                    setProgress(colorbar, hexcolor, i, (i+2));
                    i += 2;
                }
            }
        }

        private void setOnColorSeekBarChangeListener(ColorSeekBarView colorBar, ColorSeekBarView[] csbv, EditText et) {
            colorBar.setOnColorSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                final ColorSeekBarView[] colorBars = csbv;
                final EditText etHexcolor = et;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        String hexcolor = "";
                        for (ColorSeekBarView colorBar:colorBars) {
                            int p = colorBar.getProgress();

                            String pHex = Integer.toHexString(p).toUpperCase();
                            if (p < 16) {
                                pHex = "0" + pHex;
                            }

                            hexcolor = hexcolor.concat(pHex);
                        }
                        etHexcolor.setText(hexcolor);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { /* NOP */ }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { /* NOP */ }
            });
        }
        ///// End of onCreateDialog & onStart Support Methods ////////////////////////////////

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Restore Instance State
            if (savedInstanceState != null) {
                key = savedInstanceState.getString("key", "");
                hexcolor = savedInstanceState.getString("hexcolor", MainActivity.getDefaultColorHexString(getContext()));
            }

            LayoutInflater layoutInflater = getLayoutInflater();
            View v = layoutInflater.inflate(R.layout.color_dialog_layout, null);
            EditText etHexcode = v.findViewById(R.id.etHexcode);
            tvPreview = v.findViewById(R.id.tvPreview);
            ColorSeekBarView csvRed = v.findViewById(R.id.sbRed);
            ColorSeekBarView csvGrn = v.findViewById(R.id.sbGreen);
            ColorSeekBarView csvBlu = v.findViewById(R.id.sbBlue);
            colorSeekBarViews = new ColorSeekBarView[]{csvRed, csvGrn, csvBlu};
            PreviewDisplaySizeControl = v.findViewById(R.id.preview_display_size_control);

            // Use the Builder class for convenient dialog construction.
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle(pref.getTitle())
                .setPositiveButton(R.string.save, (dialog, id) -> {
                    // User okays the selected color.
                    String colorText = etHexcode.getText().toString();
                    if (AppSettingsActivity.isHexColor(colorText)) {
                        sp.edit().putString(key, colorText).apply();
                        pref.setSummary("#" + colorText);
                        dialog.dismiss();
                    } else {
                        // Placing this here instead of in an onCancel method prevents it
                        // from from firing if the user taps outside the dialog to cancel.
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Invalid Color Value")
                                .setMessage("Value must be a six-character hexadecimal.")
                                .show();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

            // Customize Dialog View //
            etHexcode.setText(hexcolor);
            setProgress(colorSeekBarViews, hexcolor);

            tvPreview.setTextColor(Color.parseColor("#" + hexcolor));

            // Update the preview at dialog creation
            updatePreviewTime();

            setOnColorSeekBarChangeListener(csvRed, colorSeekBarViews, etHexcode);
            setOnColorSeekBarChangeListener(csvGrn, colorSeekBarViews, etHexcode);
            setOnColorSeekBarChangeListener(csvBlu, colorSeekBarViews, etHexcode);

            etHexcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* NOP */ }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    hexcolor = s.toString();
                    if (AppSettingsActivity.isHexColor(hexcolor)) {
                        tvPreview.setTextColor(Color.parseColor("#" + s));
                        setProgress(colorSeekBarViews, hexcolor);
                    } else {
                        tvPreview.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { /* NOP */ }
            });
            // End of Dialog View Customization //

            builder.setView(v);

            alertDialog = builder.create();
            return alertDialog;
        }

        @Override
        public void onStart() {
            super.onStart();
            setProgress(colorSeekBarViews, hexcolor);
        }

        @Override
        public void onResume() {
            super.onResume();
            tvPreview.setVisibility(View.INVISIBLE);
            String maxtime_fill = getString((getPref(ampm) == left) ? R.string.civ_fill : R.string.mil_fill);
            PreviewDisplaySizeControl.setText(maxtime_fill);
            tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_PX, PreviewDisplaySizeControl.getTextSize());
            text_resize_attempt_count = 0;
            sendUpdatePreviewIntent();
        }

        private boolean getPref(String key) {
            return sp.getBoolean(key, false);
        }

        int text_resize_attempt_count = 0;
        public void updatePreviewTime() {
            if (sp != null) {
                String currentTime = romantime.now(!getPref(ampm), getPref(ampmSeparator), !getPref(alignment), TimeZone.getDefault().getID());

                // IMPORTANT:
                // For the String returned by romantime.now to be correctly aligned in TimeDisplay textview,
                // TextDisplay.typeface MUST be set in activity_main.xml to 'monospace'

                float pxCurrentControlTextSize = PreviewDisplaySizeControl.getTextSize();

                if (tvPreview.getVisibility() == View.INVISIBLE) {
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
                        sendUpdatePreviewIntent();
                    } else {
                        tvPreview.setVisibility(View.VISIBLE);
                    }
                }

                tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxCurrentControlTextSize);
                tvPreview.setText(currentTime);
            }
        }

        void sendUpdatePreviewIntent() {
            requireActivity().sendBroadcast( new Intent()
                    .setAction(UPDATE_PREVIEW)
                    .setPackage(requireContext().getPackageName())
            );
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("key", key);
            outState.putString("hexcolor", hexcolor);
        }

        public static String TAG = "ColorDialogFragment";
    }
    //////////////////////////////////////////////////////////////////////////////////////////
}