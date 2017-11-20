package com.striketec.mobile.activity.tabs.more;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactusActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.email) EditText emailView;
    @BindView(R.id.subject) EditText subjectView;
    @BindView(R.id.message) EditText messageView;

    String email, subject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.contactus));
    }

    @OnClick({R.id.back, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                if (checkValidEmail())
                    finish();
                break;
        }
    }

    /*
 check if user's email is vaild or not
  */
    private boolean checkValidEmail(){
        email = emailView.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_email_required));
            emailView.requestFocus();
            return false;
        }else {
            if (!CommonUtils.isEmailValid(email)){
                CommonUtils.showToastMessage(getResources().getString(R.string.error_invalid_email));
                emailView.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
