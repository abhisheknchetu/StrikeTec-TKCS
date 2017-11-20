package com.striketec.mobile.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.striketec.mobile.util.FontManager;

/**
 * Created by Qiang on 8/7/2017.
 */

public class CustomDigitView extends TextView {
    public CustomDigitView(Context context) {
        super(context);
        init();
    }

    public CustomDigitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDigitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        isInEditMode();
        setTypeface(FontManager.efdigits);
    }
}
