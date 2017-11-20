package com.striketec.mobile.activity.credential;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ForgotpasswordActivity extends BaseActivity {

    public static final String TAG = ForgotpasswordActivity.class.getSimpleName();

    private enum STATUS{
        CREDENTIAL,
        EMAIL_SUCCESS,
        EMAIL_FAIELD,
        NEW_PASSWORD,
    }

    @BindView(R.id.email) EditText emailView;
    @BindView(R.id.password) EditText pwdView;
    @BindView(R.id.confirmpassword) EditText confirmPwdView;
    @BindView(R.id.newpasswordparent) LinearLayout newPasswordParent;
    @BindView(R.id.cancelparent) LinearLayout cancelParentView;
    @BindView(R.id.cancel) Button cancelView;
    @BindView(R.id.submit) Button submitView;
    @BindView(R.id.titleview) TextView titleView;
    @BindView(R.id.guideview) TextView guideView;

    boolean pwdShowed = false, confirmpwdShowed = false;
    String email, pwd, confirmpwd, verifycode;

    STATUS step = STATUS.CREDENTIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        changePwdShowHide();
        changeconfirmPwdShowHide();

        updateView();
    }

    @OnClick({R.id.pwdshowhide, R.id.confirmpwdshowhide, R.id.submit, R.id.cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.pwdshowhide:
                changePwdShowHide();
                break;

            case R.id.confirmpwdshowhide:
                changeconfirmPwdShowHide();
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

    /*
    check if user's password is valid or not
     */

    private boolean checkValidPassword(){
        pwd = pwdView.getText().toString().trim();
        confirmpwd = confirmPwdView.getText().toString().trim();

        if (TextUtils.isEmpty(pwd)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_password_required));
            pwdView.requestFocus();
            return false;
        }else {
            if (pwd.length() < 8){
                CommonUtils.showToastMessage(getResources().getString(R.string.error_password_short));
                pwdView.requestFocus();

                return false;
            }
        }

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
            return false;
        }

        return true;
    }

    private void actionSubmit(){
        if (step == STATUS.CREDENTIAL){
            sendResetPasswordRequest();
        }else if (step == STATUS.EMAIL_FAIELD){
            step = STATUS.CREDENTIAL;
            updateView();
        }else if (step == STATUS.EMAIL_SUCCESS) {
            sendVerifyCode();
        }else if (step == STATUS.NEW_PASSWORD){
            resetPassword();
        }
    }

    private void sendResetPasswordRequest(){
        if (checkValidEmail()){
            if (CommonUtils.isOnline()){

                RetrofitSingleton.USER_REST.resetPasswordRequest(email).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
                    @Override
                    public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            DefaultResponseDto responseDto = response.body();

                            if (!responseDto.mError){
                                if (AppConst.DEBUG)
                                    Log.e(TAG, "response = " +responseDto.mMessage);

                                step = STATUS.EMAIL_SUCCESS;
                                updateView();
                                SharedUtils.saveStringValue(SharedUtils.AUTH, responseDto.mToken);

                            }else {
                                if (!TextUtils.isEmpty(responseDto.mMessage))
                                    CommonUtils.showAlert(ForgotpasswordActivity.this, responseDto.mMessage);

                                step = STATUS.EMAIL_FAIELD;
                                updateView();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(ForgotpasswordActivity.this, t.getLocalizedMessage());
                    }
                });
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }
    }

    private void sendVerifyCode(){
        if (CommonUtils.isOnline()){
            verifycode = emailView.getText().toString().trim();
            Log.e(TAG, "verify code = " + verifycode);

            RetrofitSingleton.USER_REST.verifyCode(SharedUtils.getHeader(), verifycode).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
                @Override
                public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        DefaultResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage);

                            step = STATUS.NEW_PASSWORD;
                            updateView();
                            SharedUtils.saveStringValue(SharedUtils.AUTH, responseDto.mToken);

                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(ForgotpasswordActivity.this, responseDto.mMessage);

                            step = STATUS.CREDENTIAL;
                            updateView();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(ForgotpasswordActivity.this, t.getLocalizedMessage());
                    Log.e(TAG, "fail = " + t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void resetPassword(){
        if (checkValidPassword()){
            if (CommonUtils.isOnline()){
                RetrofitSingleton.USER_REST.resetPassword(SharedUtils.getHeader(), pwd).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
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
                                gotoSigninActivity();
                                SharedUtils.saveStringValue(SharedUtils.AUTH, responseDto.mToken);

                            }else {
                                if (!TextUtils.isEmpty(responseDto.mMessage))
                                    CommonUtils.showAlert(ForgotpasswordActivity.this, responseDto.mMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(ForgotpasswordActivity.this, t.getLocalizedMessage());
                    }
                });
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }
    }

    private void gotoSigninActivity(){
        Intent signinIntent = new Intent(this, SigninActivity.class);
        signinIntent.putExtra(SharedUtils.EMAIl, email);
        startActivity(signinIntent);
        finish();
    }

    private void updateView(){
        if (step == STATUS.CREDENTIAL){
            //email field will be showed
            emailView.setText("");
            emailView.setVisibility(View.VISIBLE);
            emailView.setHint(getResources().getString(R.string.email));
            emailView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            newPasswordParent.setVisibility(View.GONE);
            submitView.setText(getResources().getString(R.string.submit));
            cancelView.setText(getResources().getString(R.string.cancel));
            titleView.setText(getResources().getString(R.string.forgotpwdtitle));
            titleView.setTextColor(getResources().getColor(R.color.punches_text));
            guideView.setText(getResources().getString(R.string.forgotpwdguide));
            cancelParentView.setVisibility(View.VISIBLE);
        }else if (step == STATUS.EMAIL_FAIELD){
            emailView.setVisibility(View.GONE);
            newPasswordParent.setVisibility(View.GONE);
            titleView.setText(getResources().getString(R.string.noacount));
            titleView.setTextColor(getResources().getColor(R.color.red));
            guideView.setText(getResources().getString(R.string.noaccountguide));
            submitView.setText(getResources().getString(R.string.tryagain));
            cancelParentView.setVisibility(View.VISIBLE);

        }else if (step == STATUS.EMAIL_SUCCESS){
            emailView.setText("");
            emailView.setHint(getResources().getString(R.string.code));
            emailView.setInputType(InputType.TYPE_CLASS_NUMBER);
            titleView.setText(getResources().getString(R.string.finalstep));
            titleView.setTextColor(getResources().getColor(R.color.punches_text));
            guideView.setText(getResources().getString(R.string.finalstepguide));
            cancelParentView.setVisibility(View.GONE);
            submitView.setText(getResources().getString(R.string.continues));
            newPasswordParent.setVisibility(View.GONE);
        }else if (step == STATUS.NEW_PASSWORD){
            emailView.setVisibility(View.GONE);
            newPasswordParent.setVisibility(View.VISIBLE);
            titleView.setText(getResources().getString(R.string.resetpassword));
            titleView.setTextColor(getResources().getColor(R.color.punches_text));
            guideView.setText(getResources().getString(R.string.resetpwdguide));
            submitView.setText(getResources().getString(R.string.submit));
            cancelParentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (step == STATUS.EMAIL_FAIELD){
            step = STATUS.CREDENTIAL;
            updateView();
        }else if (step == STATUS.EMAIL_SUCCESS) {
            step = STATUS.CREDENTIAL;
            updateView();
        }else if (step == STATUS.NEW_PASSWORD){
            step = STATUS.EMAIL_SUCCESS;
            updateView();
        }else
            super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
