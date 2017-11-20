package com.striketec.mobile.activity.leaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.util.AppConst;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompareLeaderboardActivity extends BaseActivity implements FollowCallback{

    private static final String TAG = CompareLeaderboardActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;

    LeaderboardChildFragment leaderboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compareleaderboard);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.leaderboard));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        leaderboardFragment = LeaderboardChildFragment.newInstance(this, AppConst.FEED_COMPARE_LEADERBOARD);
        fragmentTransaction.replace(R.id.container, leaderboardFragment, LeaderboardChildFragment.TAG);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void followChanged(int userId) {
        if (leaderboardFragment != null)
            leaderboardFragment.updateUserFollow(userId);
    }
}
