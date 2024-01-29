package net.diffengine.romandigitalclock;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectionMenu extends Fragment {

    private Fragment menu = this;

    public SelectionMenu() {
        // Required empty public constructor
    }

    private void showSettings() {
        Intent showSettingsIntent = new Intent(this.getContext(), SettingsActivity.class);
        startActivity(showSettingsIntent);
    }

    private void showAbout() {
        Intent showSettingsIntent = new Intent(this.getContext(), AboutActivity.class);
        startActivity(showSettingsIntent);
    }

    View.OnTouchListener menuOTL = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.90f);
                    break;
                case MotionEvent.ACTION_UP:
                    v.setAlpha(1f);
                    break;
                default:
                    break;
            }

            return false;
        }
    };

    View.OnClickListener menuOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // requireActivity will never throw an exception because this fragment is always
            // attached to MainActivity, so there's no need to check for an exception.
            FragmentManager fm = requireActivity().getSupportFragmentManager();

            fm.beginTransaction()
                    .hide(menu)
                    .commit();

            int selected_item = v.getId();

            if (selected_item == R.id.item_settings) {
                showSettings();
            } else if (selected_item == R.id.item_about) {
                showAbout();
            } else {
                // nop
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_selection_menu, container, false);

        // Set UI on-action handlers
        v.findViewById(R.id.item_settings).setOnClickListener(menuOCL);
        v.findViewById(R.id.item_settings).setOnTouchListener(menuOTL);
        v.findViewById(R.id.item_about).setOnClickListener(menuOCL);
        v.findViewById(R.id.item_about).setOnTouchListener(menuOTL);

        return v;
    }
}