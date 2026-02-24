package net.diffengine.romandigitalclock.component;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import net.diffengine.romandigitalclock.R;

public class TextViewEC extends AppCompatTextView {

    private void init(Context context, AttributeSet attrs) {

        boolean initCollapsed = true;

        if (attrs != null) {
            try (TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewEC, 0, 0)) {
                initCollapsed = typedArray.getBoolean(R.styleable.TextViewEC_initCollapsed, true);
            }
        }

        String key = getResources().getResourceEntryName(this.getId());

        Activity activity = getActivity(context);
        TextViewEC textViewEC = this;   // Needed for use in setOnClickListener lambda expression
        textViewEC.setSingleLine(activity.getPreferences(Context.MODE_PRIVATE).getBoolean(key, initCollapsed));

        // Need effectively final copy of initCollapsed for use in setOnClickListener lambda expression
        boolean finalInitCollapsed = initCollapsed;

        setOnClickListener(view -> {
            SharedPreferences prefManager = activity.getPreferences(Context.MODE_PRIVATE);
            boolean collapsed = prefManager.getBoolean(key, finalInitCollapsed);

            textViewEC.setSingleLine(!collapsed);
            prefManager.edit().putBoolean(key, !collapsed).apply();
        });
    }

    private Activity getActivity(Context context) {
        if (context != null) {
            if (context instanceof Activity) return (Activity) context;
            if (context instanceof ContextWrapper) return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public TextViewEC(@NonNull Context context) {
        super(context);
    }

    public TextViewEC(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextViewEC(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
}
