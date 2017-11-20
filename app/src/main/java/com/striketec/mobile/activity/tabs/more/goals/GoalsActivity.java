package com.striketec.mobile.activity.tabs.more.goals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.GoalListAdapter;
import com.striketec.mobile.dto.GoalDto;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoalsActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_title) TextView secondTitleView;
    @BindView(R.id.listview) ListView goalListView;

    ArrayList<GoalDto> goalDtos = new ArrayList<>();
    GoalListAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.goals));

        secondTitleView.setText(getResources().getString(R.string.new_goal));
        secondTitleView.setVisibility(View.VISIBLE);
        secondTitleView.setTextColor(getResources().getColor(R.color.white));
        secondTitleView.setAllCaps(false);

        adapter = new GoalListAdapter(this, goalDtos);
        goalListView.setAdapter(adapter);

        loadGoals();
    }

    @OnClick({R.id.back, R.id.second_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.second_title:
                Intent newIntent = new Intent(GoalsActivity.this, NewGoalActivity.class);
                startActivity(newIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void loadGoals(){
        int count = 10;
        for (int i = 0; i < count; i++){
            GoalDto goalDto = new GoalDto();

            //activity
            int activityint = CommonUtils.getRandomNum(10, 4);
            if (activityint < 7)
                goalDto.mActivity = getResources().getString(R.string.activity_boxing);
            else
                goalDto.mActivity = getResources().getString(R.string.activity_kickboxing);

            //type
            int typeint = CommonUtils.getRandomNum(10, 4);
            if (typeint < 7)
                goalDto.mType = getResources().getString(R.string.goal_type_1);
            else
                goalDto.mType = getResources().getString(R.string.goal_type_2);

            //goal
            int mGoal = CommonUtils.getRandomNum(200, 100);
            goalDto.mGoal = mGoal;

            //finished
            int mfinished = CommonUtils.getRandomNum(mGoal, mGoal / 10);
            goalDto.mFinished = mfinished;

            //start date

            goalDtos.add(goalDto);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
