package com.striketec.mobile.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.striketec.mobile.interfaces.KeyboardCallback;

/**
 * Created by Qiang on 8/7/2017.
 */

public class CustomSearchView extends CustomEditText implements View.OnTouchListener{

    private boolean isKeyboardShown = false;
    private KeyboardCallback callback = null;

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        isInEditMode();
        setOnTouchListener(this);
    }

    public void setCallback(KeyboardCallback callback){
        this.callback = callback;
    }

    public void setHide(){
        if (isKeyboardShown){
            isKeyboardShown = false;
            callback.keyboardHide();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (isKeyboardShown && keyCode == KeyEvent.KEYCODE_BACK) {
//            isKeyboardShown = false;
//            callback.keyboardHide();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (!isKeyboardShown){

            isKeyboardShown = true;
            callback.keyboardShow();
        }
        return false;
    }
}
