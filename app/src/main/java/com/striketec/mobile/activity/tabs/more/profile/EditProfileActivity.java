package com.striketec.mobile.activity.tabs.more.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.more.myplan.MyTrainingplanActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.UserPreferenceDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_title) TextView secondTitleView;

    @BindView(R.id.profile) ImageView profileImageView;
    @BindView(R.id.email) TextView emailView;
    @BindView(R.id.switch_publicprofile) Switch publicprofileSwitch;
    @BindView(R.id.switch_achievements) Switch achievementsSwitch;
    @BindView(R.id.switch_trainingstatistics) Switch traniningstatisticsSwitch;
    @BindView(R.id.switch_challengehistory) Switch challengehistorySwitch;
    @BindView(R.id.changepassword) RelativeLayout changePasswordView;

    UserPreferenceDto preferenceDto;
    boolean oldpublicprofile, oldachievement, oldtraningstatistics, oldchallengehistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.editprofile));
        secondTitleView.setText(getResources().getString(R.string.done));
        secondTitleView.setTextColor(getResources().getColor(R.color.white));
        secondTitleView.setVisibility(View.VISIBLE);

        if (StriketecApp.currentUser != null)
            emailView.setText(StriketecApp.currentUser.mEmail);

        if (StriketecApp.currentUser.mFacebookId > 0)
            changePasswordView.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(StriketecApp.currentUser.mPhoto)){
            Glide.with(this).load(StriketecApp.currentUser.mPhoto).into(profileImageView);
        }

        preferenceDto = StriketecApp.currentUser.mPreference;

        if (preferenceDto != null){
            oldpublicprofile = preferenceDto.mPublicProfile == 1;
            oldachievement = preferenceDto.mShowAchievements == 1;
            oldtraningstatistics = preferenceDto.mShowTrainingStats == 1;
            oldchallengehistory = preferenceDto.mShowChallengesHistory == 1;
        }else
            preferenceDto = new UserPreferenceDto();

        publicprofileSwitch.setChecked(oldpublicprofile);
        achievementsSwitch.setChecked(oldachievement);
        traniningstatisticsSwitch.setChecked(oldtraningstatistics);
        challengehistorySwitch.setChecked(oldchallengehistory);
    }

    @OnClick({R.id.back, R.id.second_title, R.id.changepassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.second_title:
                updatePreference();
                break;

            case R.id.changepassword:
                Intent passwordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(passwordIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void updatePreference(){
        boolean updateAble = oldpublicprofile != publicprofileSwitch.isChecked() ||
                oldtraningstatistics != traniningstatisticsSwitch.isChecked() ||
                oldachievement != achievementsSwitch.isChecked() ||
                oldchallengehistory != challengehistorySwitch.isChecked();

        if (updateAble){
            if (CommonUtils.isOnline()){

                HashMap<String, Object> params = new HashMap();
                params.put("public_profile", publicprofileSwitch.isChecked());
                params.put("show_achivements", achievementsSwitch.isChecked());
                params.put("show_training_stats", traniningstatisticsSwitch.isChecked());
                params.put("show_challenges_history", challengehistorySwitch.isChecked());

                RetrofitSingleton.USER_REST.updateUserPreferences(SharedUtils.getHeader(), params).enqueue(new IndicatorCallback<DefaultResponseDto>(this, false) {
                    @Override
                    public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            DefaultResponseDto responseDto = response.body();

                            if (!responseDto.mError){
                                if (AppConst.DEBUG)
                                    Log.e(TAG, "response = " +responseDto.mMessage);

                                preferenceDto.mPublicProfile = publicprofileSwitch.isChecked() ? 1 : 0;
                                preferenceDto.mShowAchievements = achievementsSwitch.isChecked() ? 1 : 0;
                                preferenceDto.mShowTrainingStats = traniningstatisticsSwitch.isChecked() ? 1 : 0;
                                preferenceDto.mShowChallengesHistory = challengehistorySwitch.isChecked() ? 1 : 0;

                                finish();

                            }else {
                                if (!TextUtils.isEmpty(responseDto.mMessage))
                                    CommonUtils.showAlert(EditProfileActivity.this, responseDto.mMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(EditProfileActivity.this, t.getLocalizedMessage());
                    }
                });
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }else
            finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
