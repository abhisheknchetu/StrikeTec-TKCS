package com.striketec.mobile.activity.credential;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class SignupCredentialActivity extends BaseActivity {

    public static final String TAG = SignupCredentialActivity.class.getSimpleName();

    @BindView(R.id.fname) EditText fnameView;
    @BindView(R.id.lname) EditText lnameView;
    @BindView(R.id.email) EditText emailView;
    @BindView(R.id.password) EditText pwdView;
    @BindView(R.id.confirmpassword) EditText confirmPwdView;

    private CallbackManager callbackManager;

    boolean pwdShowed = false, confirmpwdShowed = false;

    String fname, lname, email, pwd, confirmpwd;

    UserDto userDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_credential);

        userDto = (UserDto)getIntent().getSerializableExtra(AppConst.USER_INTENT);

        if (userDto == null)
            userDto = new UserDto();

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        changePwdShowHide();
        changeconfirmPwdShowHide();
    }

    @OnClick({R.id.fbsignup, R.id.pwdshowhide, R.id.confirmpwdshowhide, R.id.next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.fbsignup:
                loginToFB();
                break;
            case R.id.pwdshowhide:
                changePwdShowHide();
                break;
            case R.id.confirmpwdshowhide:
                changeconfirmPwdShowHide();
                break;
            case R.id.next:
                signUp();
                break;
        }
    }

    private void fbSignup(String fbId, String firstname, String lastname, final String fbemail){
        if (CommonUtils.isOnline()){
            RetrofitSingleton.CREDENTIAL_REST.fbSignup(fbId, firstname, lastname, fbemail).enqueue(new IndicatorCallback<AuthResponseDto>(this) {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        AuthResponseDto authResponseDto = response.body();

                        if (!authResponseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +authResponseDto.mMessage);
                            userDto.mEmail = fbemail;

                            SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                            gotoProfileScreen();
                        }else {
                            if (!TextUtils.isEmpty(authResponseDto.mMessage))
                                CommonUtils.showAlert(SignupCredentialActivity.this, authResponseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(SignupCredentialActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void loginToFB(){

        if (AccessToken.getCurrentAccessToken() != null)
            LoginManager.getInstance().logOut();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("Success");

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name ,email, gender, cover, picture.type(large)");


                        GraphRequest gr = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    System.out.println("Error");
                                } else {
                                    System.out.println("Success");
                                    try {
                                        try {
                                            String id = json.getString("id");
                                            String firstName = json.getString("first_name");
                                            String lastName = json.getString("last_name");
                                            String email = json.getString("email");

                                            if (AppConst.DEBUG) {

                                                Log.e(TAG, "id: = " + id + "\n" +
                                                        "firstname: = " + firstName + "\n" +
                                                        "lastname: = " + lastName + "\n" +
                                                        "email: = " + email);
                                            }

                                            /* signup with fb id */
                                            fbSignup(id, firstName,lastName, email);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        gr.setParameters(parameters);
                        gr.executeAsync();
                        //.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(FacebookException error) {
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }


    /*
    hide/show password
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
    hide/show confirm password
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
    check user's credentials are valid or not
     */
    private boolean checkValidCredentials(){
        fname = fnameView.getText().toString().trim();
        lname = lnameView.getText().toString().trim();
        email = emailView.getText().toString().trim();
        pwd = pwdView.getText().toString().trim();
        confirmpwd = confirmPwdView.getText().toString().trim();

        if (TextUtils.isEmpty(fname)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_fname_required));
            fnameView.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lname)){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_lname_required));
            lnameView.requestFocus();
            return false;
        }

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

    private void signUp(){
        if (checkValidCredentials()){

            if (CommonUtils.isOnline()){
                RetrofitSingleton.CREDENTIAL_REST.registerUser(fname, lname, email, pwd).enqueue(new IndicatorCallback<AuthResponseDto>(this) {
                    @Override
                    public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            AuthResponseDto authResponseDto = response.body();

                            if (!authResponseDto.mError){
                                if (AppConst.DEBUG)
                                    Log.e(TAG, "response = " +authResponseDto.mMessage);
                                userDto.mEmail = email;
                                userDto.mPassword = pwd;

                                SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                                gotoProfileScreen();
                            }else {
                                if (!TextUtils.isEmpty(authResponseDto.mMessage))
                                    CommonUtils.showAlert(SignupCredentialActivity.this, authResponseDto.mMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(SignupCredentialActivity.this, t.getLocalizedMessage());
                    }
                });
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }
    }

    private void gotoSigninActivity(){
        Intent signinIntent = new Intent(this, SigninActivity.class);
        signinIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(signinIntent);
        finish();
    }

    private void gotoProfileScreen(){
        Intent profileIntent = new Intent(this, SignupProfileActivity.class);
        profileIntent.putExtra(AppConst.USER_INTENT, userDto);

        startActivity(profileIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        gotoSigninActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
