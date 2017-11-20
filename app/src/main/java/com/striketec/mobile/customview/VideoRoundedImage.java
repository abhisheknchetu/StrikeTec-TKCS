package com.striketec.mobile.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.jackandphantom.circularimageview.RoundedImage;

/**
 * Created by Qiang on 8/18/2017.
 */

public class VideoRoundedImage extends RoundedImage {

    public VideoRoundedImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public VideoRoundedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public VideoRoundedImage(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 9 / 16);
    }
}
