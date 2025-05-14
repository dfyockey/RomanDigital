/*
 * ColorSeekBarView.java
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

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class ColorSeekBarView extends LinearLayout {

    CharSequence   mtext;
    ColorStateList mcolor;
    ColorStateList mbkgnd;

    TextView barLabel;
    SeekBar  barColor;

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
        inflate(context, R.layout.color_seekbar_layout, this);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorSeekBarView, 0, 0);

            try {
                mtext  = a.getText(R.styleable.ColorSeekBarView_barLabel);
                mcolor = a.getColorStateList(R.styleable.ColorSeekBarView_barColor);
                mbkgnd = a.getColorStateList(R.styleable.ColorSeekBarView_barBackgroundColor);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupSeekBar();
    }

    private void setupSeekBar() {
        barLabel = findViewById(R.id.colorLabel);
        barLabel.setText(mtext);

        barColor = findViewById(R.id.colorBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            barColor.setMin(0);
        }
        barColor.setMax(255);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            barColor.setProgressDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.seekbar_track_material, null));
        }

        barColor.setProgressBackgroundTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setProgressBackgroundTintList(mbkgnd);
        barColor.setProgressTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setProgressTintList(mcolor);
        barColor.setThumbTintMode(PorterDuff.Mode.SRC_IN);
        barColor.setThumbTintList(mcolor);
    }

    public void setProgress(int progress) {
        barColor.setProgress(progress);
    }
    public int  getProgress()             { return barColor.getProgress();  }

    public void setOnColorSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        barColor.setOnSeekBarChangeListener(listener);
    }
}
