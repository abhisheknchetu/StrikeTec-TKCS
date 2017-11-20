package com.striketec.mobile.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Super on 10/15/2016.
 */

//set ratio of view according to screen orientation
public class CustomTextureView extends TextureView {

    private boolean isPortrait = true;

    public CustomTextureView(Context context) {
        super(context);
    }

    public CustomTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPortrait(boolean portrait){
        this.isPortrait = portrait;
    }

    public boolean getPortrait(){
        return isPortrait;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isPortrait){
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 9 / 16);
        }else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }

    }
}
