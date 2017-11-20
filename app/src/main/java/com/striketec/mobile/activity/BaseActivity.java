package com.striketec.mobile.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.striketec.mobile.R;
import com.striketec.mobile.util.CommonUtils;

/**
 * Created by Qiang on 7/4/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);

        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.TRANSPARENT);
//        tintManager.setTintColor(getResources().getColor(R.color.transparent));
    }

    public void startLoading(){
        if (loadingDialog == null){
            loadingDialog = new ProgressDialog(this, R.style.ProgressTheme);
            loadingDialog.setCancelable(false);
            loadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            loadingDialog.show();
        }
    }

    public void endLoading(){
        if (loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    //hide software keyboard
    public void hideSoftKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    //hide keyboard when user touch non edit field
    public void setKeyboardAction (View view){
        if (!(view instanceof EditText)){
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup){
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
                View innerView = ((ViewGroup)view).getChildAt(i);
                setKeyboardAction(innerView);
            }
        }
    }
}
