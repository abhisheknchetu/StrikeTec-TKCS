package com.striketec.mobile.activity.battle;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.BattleRequestListAdapter;
import com.striketec.mobile.dto.BattleUser_Temp;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BattleRequestsActivity extends BaseActivity {

    private static final String TAG = BattleRequestsActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.listview) ListView listView;

    BattleRequestListAdapter listAdapter;
    private ArrayList<BattleUser_Temp> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battlerequests);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.battlerequests));
        listAdapter = new BattleRequestListAdapter(this, items);
        listView.setAdapter(listAdapter);

        loadBattleRequests();
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void loadBattleRequests(){
        for (int i = 0; i < 20; i++){
            BattleUser_Temp battleUser_temp = new BattleUser_Temp();
            battleUser_temp.oppoenentUserName = "John Tucker";
            battleUser_temp.timeDiff = CommonUtils.getRandomNum(20, 0) + "m";

            items.add(battleUser_temp);
        }

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
