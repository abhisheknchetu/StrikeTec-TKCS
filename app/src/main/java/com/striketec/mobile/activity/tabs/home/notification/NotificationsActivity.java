package com.striketec.mobile.activity.tabs.home.notification;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.battle.BattleRequestsActivity;
import com.striketec.mobile.adapters.NotificationListAdapter;
import com.striketec.mobile.dto.NotificationDto_Temp;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationsActivity extends BaseActivity {

    private static final String TAG = NotificationsActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.training_result) TextView resultView;
    @BindView(R.id.listview) ListView listView;

    private ArrayList<NotificationDto_Temp> itemLists = new ArrayList<>();
    NotificationListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.notifications));
        listAdapter = new NotificationListAdapter(this, itemLists);
        listView.setAdapter(listAdapter);

        updateInfo();
        loadNotification();
    }

    @OnClick({R.id.back, R.id.challenge_request})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.challenge_request:
                Intent challengeIntent = new Intent(this, BattleRequestsActivity.class);
                startActivity(challengeIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void updateInfo(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String your = "Your ";

        SpannableString yourspannableString = new SpannableString(your);
        yourspannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, your.length(), 0);
        builder.append(yourspannableString);

        String lasttraining = getResources().getString(R.string.lasttraining);

        SpannableString lastspannableString = new SpannableString(lasttraining);
        lastspannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.punches_text)), 0, lasttraining.length(), 0);
        builder.append(lastspannableString);

//        String result = " " + String.format(getResources().getString(R.string.lasttrainingresult1), 20) + " ";
        String result = " " + String.format(getResources().getString(R.string.lasttrainingresult1), 20 + "%") + " ";
        SpannableString resultspannableString = new SpannableString(result);
        resultspannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, result.length(), 0);
        builder.append(resultspannableString);

        String week = getResources().getString(R.string.bestlastweek);

        SpannableString weekspannableString = new SpannableString(week);
        weekspannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.punches_text)), 0, week.length(), 0);
        builder.append(weekspannableString);

        resultView.setText(builder);
    }

    private void loadNotification(){
        for (int i = 0; i < 20; i++){
            NotificationDto_Temp notificationDto_temp = new NotificationDto_Temp();

            int typeNum = CommonUtils.getRandomNum(40, 10);
            if (typeNum < 20) {
                notificationDto_temp.type = 1;
                notificationDto_temp.content = getResources().getString(R.string.liketraining);
                notificationDto_temp.oppoenentUserName = "Ana Tucker";
                notificationDto_temp.timeDiff = CommonUtils.getRandomNum(10, 2) + "m";
            }else if (typeNum < 30){
                notificationDto_temp.type = 2;
                notificationDto_temp.content = getResources().getString(R.string.notification_following);
                notificationDto_temp.oppoenentUserName = "John Smith";
                notificationDto_temp.timeDiff = CommonUtils.getRandomNum(20, 10) + "m";
            }else {
                notificationDto_temp.type = 3;
                notificationDto_temp.content = getResources().getString(R.string.notification_beat);
                notificationDto_temp.oppoenentUserName = "Mike Tucker";
                notificationDto_temp.timeDiff = CommonUtils.getRandomNum(30, 20) + "m";
            }

            itemLists.add(notificationDto_temp);
        }

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
