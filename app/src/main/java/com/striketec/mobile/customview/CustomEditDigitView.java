package com.striketec.mobile.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.striketec.mobile.util.FontManager;

/**
 * Created by Qiang on 8/7/2017.
 */

public class CustomEditDigitView extends EditText {
    public CustomEditDigitView(Context context) {
        super(context);
        init();
    }

    public CustomEditDigitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditDigitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        isInEditMode();
        setTypeface(FontManager.efdigits);
    }
}
