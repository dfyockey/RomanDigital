package net.diffengine.romandigitalclock;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class ColorSeekBarView extends LinearLayout {

    TextView barLabel;
    SeekBar  barColor;

    private Context context;
    private @Nullable AttributeSet attrs;

    public ColorSeekBarView(Context context) {
        super(context);
        init(context, null);
    }

    public ColorSeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorSeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ColorSeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        //this.context = context;
        //this.attrs   = attrs;
        inflate(context, R.layout.color_seekbar_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupSeekBar();
    }

    private void setupSeekBar() {
        barLabel = findViewById(R.id.colorLabel);
        barColor = findViewById(R.id.colorBar);

        barLabel.setText("R");

        //ColorStateList color = null;
        ColorStateList color = makeColorStateList(R.color.clock_red);

//        if (attrs != null) {
//            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorSeekBarView, 0, 0);
//
//            try {
//                barLabel.setText(a.getText(R.styleable.ColorSeekBarView_label));
//                color = a.getColorStateList(R.styleable.ColorSeekBarView_color);
//            } finally {
//                a.recycle();
//            }
//        }

        barColor.setProgressDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.seekbar_track_material, null));
        barColor.setProgressBackgroundTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setProgressBackgroundTintList(color);
        barColor.setProgressTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setProgressTintList(color);
        barColor.setThumbTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setThumbTintList(color);
    }

    private ColorStateList makeColorStateList(@ColorRes int color) {
        @ColorInt int colorInt = getContext().getResources().getColor(color);
        return ColorStateList.valueOf(colorInt);
    }
}
