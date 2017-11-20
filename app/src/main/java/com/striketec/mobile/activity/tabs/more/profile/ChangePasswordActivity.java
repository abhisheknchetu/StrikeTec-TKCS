package com.striketec.mobile.activity.tabs.more.profile;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.Common;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.credential.ForgotpasswordActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.password) EditText pwdView;
    @BindView(R.id.confirmpassword) EditText confirmPwdView;
    @BindView(R.id.currentpassword) EditText currentPwdView;

    boolean pwdShowed = false, confirmpwdShowed = false, currentpwdShowed = false;
    String  pwd, confirmpwd, currentpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    @OnClick({R.id.back, R.id.pwdshowhide, R.id.confirmpwdshowhide, R.id.currentpasswordshowhide, R.id.submit, R.id.cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.pwdshowhide:
                changePwdShowHide();
                break;

            case R.id.confirmpwdshowhide:
                changeconfirmPwdShowHide();
                break;

            case R.id.currentpasswordshowhide:
                changecurrentPwdShowHide();
                break;

            case R.id.submit:
                actionSubmit();
                break;

            case R.id.cancel:
                finish();
                break;

            default:
                break;
        }
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.changepassword));

        changePwdShowHide();
        changeconfirmPwdShowHide();
        changecurrentPwdShowHide();

        if (AppConst.DEBUG)
            currentPwdView.setText(SharedUtils.getStringvalue(SharedUtils.PWD, ""));
    }

    /*
  called when user click eye btn to show/hide password
   */
    private void changePwdShowHide(){
        if (!pwdShowed){
            pwdShowed = true;
            pwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwdView.setSelection(pwdView.getText().length());
        }else {
            pwdShowed = false;
            pwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            pwdView.setSelection(pwdView.getText().length());
        }
    }

    /*
    called when user click eye btn to show/hide confirm password
     */
    private void changeconfirmPwdShowHide(){
        if (!confirmpwdShowed){
            confirmpwdShowed = true;
            confirmPwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPwdView.setSelection(confirmPwdView.getText().length());
        }else {
            confirmpwdShowed = false;
            confirmPwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPwdView.setSelection(confirmPwdView.getText().length());
        }
    }

    private void changecurrentPwdShowHide(){
        if (!currentpwdShowed){
            currentpwdShowed = true;
            currentPwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            currentPwdView.setSelection(confirmPwdView.getText().length());
        }else {
            currentpwdShowed = false;
            currentPwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            currentPwdView.setSelection(confirmPwdView.getText().length());
        }
    }

    private boolean checkValidPassword(){
        pwd = pwdView.getText().toString().trim();
        confirmpwd = confirmPwdView.getText().toString().trim();
        currentpwd = currentPwdView.getText().toString().trim();

        if (TextUtils.isEmpty(currentpwd)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_current_password_required));
            currentPwdView.requestFocus();
            return false;
        }else {
            if (currentpwd.length() < 8){
                CommonUtils.showToastMessage(getResources().getString(R.string.error_password_short));
                currentPwdView.requestFocus();

                return false;
            }
        }

        if (TextUtils.isEmpty(pwd)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_new_password_required));
            pwdView.requestFocus();
            return false;
        }else {
            if (pwd.length() < 8){
                CommonUtils.showToastMessage(getResources().getString(R.string.error_password_short));
                pwdView.requestFocus();

                return false;
            }
        }

        if (CommonUtils.pwdValidation(currentpwd)){
            if (CommonUtils.pwdValidation(pwd)) {

                if (TextUtils.isEmpty(confirmpwd)) {
                    CommonUtils.showToastMessage(getResources().getString(R.string.error_confirmpassword_required));
                    confirmPwdView.requestFocus();
                    return false;
                } else {
                    if (!pwd.equals(confirmpwd)) {
                        CommonUtils.showToastMessage(getResources().getString(R.string.error_samepwd_required));
                        confirmPwdView.requestFocus();

                        return false;
                    }
                }
            }else {
                pwdView.requestFocus();
                return false;
            }
        }else {
            currentPwdView.requestFocus();
            return false;
        }

        return true;
    }

    private void actionSubmit(){
        if (checkValidPassword()){

            if (CommonUtils.isOnline()){
                RetrofitSingleton.USER_REST.changePassword(SharedUtils.getHeader(), currentpwd, pwd).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
                    @Override
                    public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            DefaultResponseDto responseDto = response.body();

                            if (!responseDto.mError){
                                if (AppConst.DEBUG)
                                    Log.e(TAG, "response = " +responseDto.mMessage);

                                if (!TextUtils.isEmpty(responseDto.mMessage))
                                    CommonUtils.showToastMessage(responseDto.mMessage);

                                //go to signin activity
                                SharedUtils.saveStringValue(SharedUtils.AUTH, responseDto.mToken);
                                SharedUtils.saveStringValue(SharedUtils.PWD, pwd);
                                finish();

                            }else {
                                if (!TextUtils.isEmpty(responseDto.mMessage))
                                    CommonUtils.showAlert(ChangePasswordActivity.this, responseDto.mMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(ChangePasswordActivity.this, t.getLocalizedMessage());
                    }
                });
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
