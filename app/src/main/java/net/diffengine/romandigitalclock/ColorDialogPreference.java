package net.diffengine.romandigitalclock;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

public class ColorDialogPreference extends Preference {
    FragmentManager fm;

    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorDialogPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorDialogPreference(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                ColorDialogFragment colorDialogFragment = new ColorDialogFragment(preference, getKey());
                colorDialogFragment.show(fm, ColorDialogFragment.TAG);
                return true;
            }
        });
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }
}