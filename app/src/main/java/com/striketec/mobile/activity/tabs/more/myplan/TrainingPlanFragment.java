package com.striketec.mobile.activity.tabs.more.myplan;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.customview.HexagonButton;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.util.PresetUtil;

import java.lang.reflect.Field;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrainingPlanFragment extends Fragment {

    public static final String TAG = TrainingPlanFragment.class.getSimpleName();

    @BindView(R.id.skillone) HexagonButton skilloneBtn;
    @BindView(R.id.skilltwo) HexagonButton skilltwoBtn;
    @BindView(R.id.skillthree) HexagonButton skillthreeBtn;

    int skilllevel = 1;

    UserDto userDto;

    int oldskilllevel = -1;

    MyTrainingplanActivity myTrainingplanActivity;

    public static TrainingPlanFragment trainingPlanFragment;
    private static Context mContext;

    public static TrainingPlanFragment newInstance(Context context) {
        mContext = context;
        trainingPlanFragment = new TrainingPlanFragment();
        return trainingPlanFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        myTrainingplanActivity = (MyTrainingplanActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myplan_training_plan, container, false);

        ButterKnife.bind(this, view);

        userDto = StriketecApp.currentUser;

        initView();

        return view;
    }

    private void initView(){
        if (!TextUtils.isEmpty(userDto.mSkillLevel)){
            skilllevel = PresetUtil.getSkilllevelPosition(userDto.mSkillLevel) + 1;
            oldskilllevel = skilllevel;
        }else {
            skilllevel = 1;
        }

        updateView(skilllevel);
    }

    public boolean checkUpdateable(){
        return oldskilllevel != skilllevel;
    }

    public void updateParams(HashMap<String, Object> params){
        params.put("skill_level", PresetUtil.skillLeveList.get(skilllevel - 1));
    }

    @OnClick({R.id.skillone, R.id.skilltwo, R.id.skillthree})
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
        }
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

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
