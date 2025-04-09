package net.diffengine.romandigitalclock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class ColorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Color")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User okays the selected color.
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancels the dialog.
                    }
                });

        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.color_dialog_layout, null);

            // Customize Dialog View //
            EditText etHexcode = v.findViewById(R.id.etHexcode);
            TextView tvMessage = v.findViewById(R.id.tvMessage);
            etHexcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tvMessage.setText(etHexcode.getText());
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

    public static String TAG = "ColorDialogFragment";
};

