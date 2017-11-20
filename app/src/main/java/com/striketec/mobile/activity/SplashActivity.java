package com.striketec.mobile.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.credential.SigninActivity;
import com.striketec.mobile.activity.leaderboard.ExploreFilterActivity;
import com.striketec.mobile.activity.onboard.OnboardActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DBAdapter;
import com.striketec.mobile.util.FontManager;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1999;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Log.e("Super", "oncreate");
        requestMultiplePermissions();
    }

    private void initView() {
        Log.e("Super", "init = ");
        dbAdapter = DBAdapter.getInstance(SplashActivity.this);
        copyPropertise();
        CommonUtils.getKeyHash();
        PresetUtil.getCountries();
        autoLogin();
//        gotoTestActivity();
    }

    private void autoLogin() {
        if (SharedUtils.getBoolenvalue(SharedUtils.REMEMBER_ME, false)) {
            if (SharedUtils.getStringvalue(SharedUtils.LOGGEDINTYPE, "").equalsIgnoreCase(SharedUtils.EMAIl)) {
                //login with email, password
                signinWithEmail();
            } else if (SharedUtils.getStringvalue(SharedUtils.LOGGEDINTYPE, "").equalsIgnoreCase(SharedUtils.FACEBOOK)) {
                //login with fbid
                Log.e("Super", "fbid = " + SharedUtils.getStringvalue(SharedUtils.FBID, ""));
                fbsignin(SharedUtils.getStringvalue(SharedUtils.FBID, ""));
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoSigninActivity();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoSigninActivity();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    private void signinWithEmail() {
        if (CommonUtils.isOnline()) {
            RetrofitSingleton.CREDENTIAL_REST.loginUser(SharedUtils.getStringvalue(SharedUtils.EMAIl, ""), SharedUtils.getStringvalue(SharedUtils.PWD, "")).enqueue(new IndicatorCallback<AuthResponseDto>(this, false) {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null) {
                        AuthResponseDto authResponseDto = response.body();

                        if (!authResponseDto.mError) {
                            if (AppConst.DEBUG)

                                StriketecApp.currentUser = authResponseDto.mUser;
                            dbAdapter.updateUser(StriketecApp.currentUser);
                            SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                            StriketecApp.isLoggedin = true;
                            CommonUtils.updateRegistrationToken();

                            if (StriketecApp.currentUser.mshowTip == 1) {
                                gotoTipsActivity();
                            } else {
                                gotoMainActivity();
                            }
                        } else {
                            gotoSigninActivity();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    gotoSigninActivity();
                    /**
                    user : rakeshk2
                    date : 10/24/2017
                    description : 
                    **/
//                    gotoMainActivity();
                }
            });
        } else {
            UserDto userDto = dbAdapter.getUserInfoFromEmail(SharedUtils.getStringvalue(SharedUtils.EMAIl, ""), SharedUtils.getStringvalue(SharedUtils.PWD, ""));
            if (userDto != null) {
                StriketecApp.currentUser = userDto;
                gotoMainActivity();
            } else {
                gotoSigninActivity();
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

                            StriketecApp.currentUser = authResponseDto.mUser;
                            dbAdapter.updateUser(StriketecApp.currentUser);
                            SharedUtils.saveStringValue(SharedUtils.AUTH, authResponseDto.mToken);

                            StriketecApp.isLoggedin = true;
                            CommonUtils.updateRegistrationToken();

                            if (StriketecApp.currentUser.mshowTip == 1) {
                                gotoTipsActivity();
                            } else {
                                gotoMainActivity();
                            }
                        } else {
                            if (!TextUtils.isEmpty(authResponseDto.mMessage))
                                CommonUtils.showAlert(SplashActivity.this, authResponseDto.mMessage);

                            gotoSigninActivity();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(SplashActivity.this, t.getLocalizedMessage());

                    gotoSigninActivity();
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));

            UserDto userDto = dbAdapter.getUserInfoFromFBId(fbid);
            if (userDto != null) {
                StriketecApp.currentUser = userDto;
                gotoMainActivity();
            }
        }
    }

    private void gotoSigninActivity() {
        Intent signinIntent = new Intent(this, SigninActivity.class);
        startActivity(signinIntent);
        finish();
    }

    private void gotoTipsActivity() {
        Intent tipsIntent = new Intent(this, OnboardActivity.class);
        startActivity(tipsIntent);
        finish();
    }

    private void gotoMainActivity() {
        Intent maininIntent = new Intent(this, MainActivity.class);
        startActivity(maininIntent);
        finish();
    }

    private void gotoTestActivity() {
        Intent maininIntent = new Intent(this, ExploreFilterActivity.class);
        startActivity(maininIntent);
        finish();
    }

    private void copyPropertise() {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = FontManager.assetManager.open(AppConst.PROPERTIESFILEPATH);

            File myDirectory = new File(getExternalFilesDir(null), AppConst.CONFIG_DIRECTORY);

            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            if (AppConst.DEBUG)
                Log.e(TAG, "config directory path = " + myDirectory.getAbsolutePath());

            File outFile = new File(myDirectory, AppConst.PROPERTIESFILEPATH);

            if (!outFile.exists()) {
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                out.flush();
                out.close();
                out = null;
            }

            in.close();
            in = null;

        } catch (IOException e) {
            if (AppConst.DEBUG)
                Log.e(TAG, "Failed to copy asset file: " + e);
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void requestMultiplePermissions() {

        String internetPermission = Manifest.permission.INTERNET;
        String networkStatePermission = Manifest.permission.ACCESS_NETWORK_STATE;
        String readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String bluetoothPermission = Manifest.permission.BLUETOOTH;
        String bluetoothAdminPermission = Manifest.permission.BLUETOOTH_ADMIN;
//        String recordAudioPermission = Manifest.permission.RECORD_AUDIO;

        int hasInternetPermission = ContextCompat.checkSelfPermission(this, internetPermission);
        int hasNetworkStatePermission = ContextCompat.checkSelfPermission(this, networkStatePermission);
        int hasReadStoragePermission = ContextCompat.checkSelfPermission(this, readStoragePermission);
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(this, writeStoragePermission);
        int hasbluetoothPermission = ContextCompat.checkSelfPermission(this, bluetoothPermission);
        int hasbluetoothAdminPermission = ContextCompat.checkSelfPermission(this, bluetoothAdminPermission);
        int hasRecordAudioPermission = ContextCompat.checkSelfPermission(this, bluetoothAdminPermission);

        List<String> permissions = new ArrayList<>();

        if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(internetPermission);
        }

        if (hasNetworkStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(networkStatePermission);
        }


        if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readStoragePermission);
        }

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(writeStoragePermission);
        }

        if (hasbluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(bluetoothPermission);
        }

        if (hasbluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(bluetoothAdminPermission);
        }

        /*if (hasRecordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(recordAudioPermission);
        }*/

        if (!permissions.isEmpty()) {
            String[] params = permissions.toArray(new String[permissions.size()]);
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(params, REQUEST_PERMISSIONS);
            }
        } else {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == permissions.length) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                initView();
            } else {
                CommonUtils.showToastMessage("Permission Denied");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
