package com.striketec.mobile.activity.credential;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.onboard.OnboardActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.tabs.training.TrainingChooseActivity;
import com.striketec.mobile.activity.tabs.training.sensortest.SensorTestActivity;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.response.ResponseObject;
import com.striketec.mobile.response.SubscriptionMainResponse;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DBAdapter;
import com.striketec.mobile.util.SharedUtils;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class SigninActivity extends BaseActivity {

    public static final String TAG = SigninActivity.class.getSimpleName();

    @BindView(R.id.email)
    EditText emailView;
    @BindView(R.id.password)
    EditText pwdView;
    @BindView(R.id.signup)
    Button signupView;
    @BindView(R.id.remember_check)
    ImageView rememberCheckView;
    DBAdapter dbAdapter;
    boolean pwdshowed = false;
    boolean rememberchecked = false;
    String email, pwd;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = getIntent().getStringExtra(SharedUtils.EMAIl);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        dbAdapter = DBAdapter.getInstance(this);
        initViews();

    }

    private void initViews() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String firstString = getResources().getString(R.string.signupfirst) + " ";
        SpannableString firstSpannable = new SpannableString(firstString);
        firstSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.punches_text)), 0, firstString.length(), 0);
        builder.append(firstSpannable);

        String signupString = getResources().getString(R.string.signup);
        SpannableString signupSpan = new SpannableString(signupString);
        signupSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, signupString.length(), 0);
        builder.append(signupSpan);

        signupView.setText(builder);

        changePwdShowHide();

        if (!TextUtils.isEmpty(email))
            emailView.setText(email);

        updateRememberCheckView();
        SharedUtils.saveStringValue(SharedUtils.AUTH, "");

        if (AppConst.DEBUG) {
//            emailView.setText("toniorasma@yahoo.com");
//            pwdView.setText("Rkh.0610");
            emailView.setText("rakeshk2@chetu.com");
            pwdView.setText("Strike@123");
        }
    }

    @OnClick({R.id.fbsignin, R.id.signin, R.id.pwdshowhide, R.id.signup, R.id.forgotpwd, R.id.rememberme_parent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbsignin:
                loginToFB();
                break;

            case R.id.signin:
                signin();

//                gotoTipScreen();
//                gotoMainorTipScreen();
//                gotoMainScreen();
//                gotoTrainingChooseActivity();
                break;

            case R.id.pwdshowhide:
                changePwdShowHide();
                break;

            case R.id.signup:
//                gotoSearchSensorActivity();
                signupStep1Activity();
                break;

            case R.id.forgotpwd:
                gotoForgotPwdActivity();
                break;

            case R.id.rememberme_parent:
                toggleRemeber();
                break;

            default:
                break;
        }
    }

    private void toggleRemeber() {
        if (rememberchecked) {
            rememberchecked = false;
        } else {
            rememberchecked = true;
        }

        updateRememberCheckView();
    }

    private void updateRememberCheckView() {
        if (rememberchecked) {
            rememberCheckView.setImageResource(R.drawable.img_check);
        } else {
            rememberCheckView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        }
    }

    private void signin() {
        if (checkValidCredentials()) {
            if (CommonUtils.isOnline()) {
                RetrofitSingleton.CREDENTIAL_REST.loginUser(email, pwd).enqueue(new IndicatorCallback<AuthResponseDto>(this) {
                    @Override
                    public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null) {
                            AuthResponseDto authResponseDto = response.body();

                            if (!authResponseDto.mError) {
                                if (AppConst.DEBUG)
                                    Log.e(TAG, "response = " + authResponseDto.mMessage);

                                if (rememberchecked) {
                                    SharedUtils.saveBoolenValue(SharedUtils.REMEMBER_ME, true);
                                    SharedUtils.saveStringValue(SharedUtils.LOGGEDINTYPE, SharedUtils.EMAIl);
                                    SharedUtils.saveStringValue(SharedUtils.EMAIl, email);
                                    SharedUtils.saveStringValue(SharedUtils.PWD, pwd);
                                }

                                StriketecApp.currentUser = authResponseDto.mUser;
                                dbAdapter.updateUser(StriketecApp.currentUser);
                                SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                                StriketecApp.isLoggedin = true;
                                CommonUtils.updateRegistrationToken();

                                if (StriketecApp.currentUser.mshowTip == 1) {
                                    gotoTipScreen();
                                } else {
                                    gotoMainScreen();
                                }
                            } else {
                                if (!TextUtils.isEmpty(authResponseDto.mMessage))
                                    CommonUtils.showAlert(SigninActivity.this, authResponseDto.mMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(SigninActivity.this, t.getLocalizedMessage());
                    }
                });
            } else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));

                UserDto userDto = dbAdapter.getUserInfoFromEmail(email, pwd);
                if (userDto != null) {
                    StriketecApp.currentUser = userDto;
                    gotoMainScreen();
                }
            }
        }
    }

    private void fbsignin(final String fbid) {
        if (CommonUtils.isOnline()) {
            RetrofitSingleton.CREDENTIAL_REST.fbLogin(fbid).enqueue(new IndicatorCallback<AuthResponseDto>(this) {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null) {
                        AuthResponseDto authResponseDto = response.body();

                        if (!authResponseDto.mError) {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + authResponseDto.mMessage);

                            if (rememberchecked) {
                                SharedUtils.saveBoolenValue(SharedUtils.REMEMBER_ME, true);
                                SharedUtils.saveStringValue(SharedUtils.LOGGEDINTYPE, SharedUtils.FACEBOOK);
                                SharedUtils.saveStringValue(SharedUtils.FBID, fbid);
                            }

                            StriketecApp.currentUser = authResponseDto.mUser;
                            dbAdapter.updateUser(StriketecApp.currentUser);
                            SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                            StriketecApp.isLoggedin = true;
                            CommonUtils.updateRegistrationToken();

                            if (StriketecApp.currentUser.mshowTip == 1) {
                                gotoTipScreen();
                            } else {
                                gotoMainScreen();
                            }
                        } else {
                            if (!TextUtils.isEmpty(authResponseDto.mMessage))
                                CommonUtils.showAlert(SigninActivity.this, authResponseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(SigninActivity.this, t.getLocalizedMessage());
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));

            UserDto userDto = dbAdapter.getUserInfoFromFBId(fbid);
            if (userDto != null) {
                StriketecApp.currentUser = userDto;
                gotoMainScreen();
            }
        }
    }

    private void loginToFB() {

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

                                            /* login with fb id */
                                            fbsignin(id);

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

    private void signupStep1Activity() {
        Intent step1Intent = new Intent(this, SignupCredentialActivity.class);
        startActivity(step1Intent);
        finish();
    }

    private void gotoTrainingChooseActivity() {
        Intent chooseIntent = new Intent(this, TrainingChooseActivity.class);
        startActivity(chooseIntent);
        finish();
    }

    private void gotoMainScreen() {
        if (AppConst.SENSOR_TEST) {
            Intent sensortestIntent = new Intent(this, SensorTestActivity.class);
            startActivity(sensortestIntent);
            finish();
        } else {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void gotoMainorTipScreen() {

        if (AppConst.SENSOR_TEST) {
            Intent sensortestIntent = new Intent(this, SensorTestActivity.class);
            startActivity(sensortestIntent);
        } else {
            if (checkValidCredentials()) {
                Intent nextIntent;

                if (StriketecApp.currentUser.mshowTip == 0) {
                    nextIntent = new Intent(this, MainActivity.class);
                } else {
                    nextIntent = new Intent(this, OnboardActivity.class);
                }

                startActivity(nextIntent);
                finish();
            }
        }
    }

    private void gotoTipScreen() {
        Intent tipIntent = new Intent(this, OnboardActivity.class);
        startActivity(tipIntent);
        finish();
    }

    private void gotoForgotPwdActivity() {
        Intent forgotIntent = new Intent(this, ForgotpasswordActivity.class);
        startActivity(forgotIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void changePwdShowHide() {
        if (!pwdshowed) {
            pwdshowed = true;
            pwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwdView.setSelection(pwdView.getText().length());
        } else {
            pwdshowed = false;
            pwdView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            pwdView.setSelection(pwdView.getText().length());
        }
    }

    /*
        check if user's credentials are valid or not
    */
    private boolean checkValidCredentials() {

        email = emailView.getText().toString().trim();
        pwd = pwdView.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            CommonUtils.showToastMessage(getResources().getString(R.string.error_email_required));
            emailView.requestFocus();
            return false;
        } else {
            if (!CommonUtils.isEmailValid(email)) {
                CommonUtils.showToastMessage(getResources().getString(R.string.error_invalid_email));
                emailView.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(pwd)) {
            CommonUtils.showToastMessage(getResources().getString(R.string.error_password_required));
            pwdView.requestFocus();
            return false;
        } else {
            if (pwd.length() < 8) {
                CommonUtils.showToastMessage(getResources().getString(R.string.error_password_short));
                pwdView.requestFocus();

                return false;
            }

            return CommonUtils.pwdValidation(pwd);
        }
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
