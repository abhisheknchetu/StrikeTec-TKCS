package com.striketec.mobile.activity.credential;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.customview.HexagonButton;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.PresetUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupTrainingPlanActivity extends BaseActivity {

    public static final String TAG = SignupTrainingPlanActivity.class.getSimpleName();

    @BindView(R.id.skillone) HexagonButton skilloneBtn;
    @BindView(R.id.skilltwo) HexagonButton skilltwoBtn;
    @BindView(R.id.skillthree) HexagonButton skillthreeBtn;

    int skilllevel = 1;

    UserDto userDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_training_plan);

        userDto = (UserDto)getIntent().getSerializableExtra(AppConst.USER_INTENT);

        if (userDto == null)
            userDto = new UserDto();

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initView();
    }

    private void initView(){
        if (!TextUtils.isEmpty(userDto.mSkillLevel)){
            skilllevel = PresetUtil.getSkilllevelPosition(userDto.mSkillLevel) + 1;
        }else {
            skilllevel = 1;
        }

        updateView(skilllevel);
    }

    @OnClick({R.id.skillone, R.id.skilltwo, R.id.skillthree, R.id.next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.skillone:
                updateView(1);
                break;

            case R.id.skilltwo:
                updateView(2);
                break;

            case R.id.skillthree:
                updateView(3);
                break;

            case R.id.next:
                gotoSearchSensorActivity();
                break;
        }
    }

    private void gotoSearchSensorActivity(){
        Intent sensorIntent = new Intent(this, SignupSensorActivity.class);
        sensorIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(sensorIntent);
        finish();
    }

    private void updateView(int level){
        skilllevel = level;

        if(level == 1){
            skilloneBtn.setFill(true);
            skilltwoBtn.setFill(false);
            skillthreeBtn.setFill(false);
        }else if (level == 2){
            skilloneBtn.setFill(false);
            skilltwoBtn.setFill(true);
            skillthreeBtn.setFill(false);
        }else {
            skilloneBtn.setFill(false);
            skilltwoBtn.setFill(false);
            skillthreeBtn.setFill(true);
        }

        userDto.mSkillLevel = PresetUtil.skillLeveList.get(skilllevel - 1);
    }

    private void gotoSignupProfileActivity(){
        Intent profileIntent = new Intent(this, SignupProfileActivity.class);
        profileIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(profileIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        gotoSignupProfileActivity();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
