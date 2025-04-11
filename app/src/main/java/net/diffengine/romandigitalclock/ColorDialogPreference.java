package net.diffengine.romandigitalclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        colorDialogFragment.show(m_fm, ColorDialogFragment.TAG);
        return true;
    }
}