package net.diffengine.romandigitalclock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;

import java.util.Objects;

public class ColorDialogFragment extends DialogFragment {

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
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

            // Customize Dialog View //
            String hexcolor = sp.getString("hexcolor", "FFFFFF");
            etHexcode.setText(hexcolor);
            tvPreview.setTextColor(Color.parseColor("#" + hexcolor));
            etHexcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (SettingsActivity.isHexColor(s.toString())) {
                        tvPreview.setTextColor(Color.parseColor("#" + s.toString()));
                    } else {
                        tvPreview.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            // End of Dialog View Customization //

        builder.setView(v);

        // Create the AlertDialog object and return it.
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        new AlertDialog.Builder(getActivity())
                .setTitle("Invalid Color Value")
                .setMessage("Value must be a six-character hexadecimal.")
                .show();
    }

    public static String TAG = "ColorDialogFragment";
};

