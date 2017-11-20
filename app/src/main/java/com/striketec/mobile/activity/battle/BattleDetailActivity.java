package com.striketec.mobile.activity.battle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BattleDetailActivity extends BaseActivity {

    private static final String TAG = BattleDetailActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_image) ImageView secondImageView;
    @BindView(R.id.detail) TextView detailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battledetail);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.battles));
        secondImageView.setVisibility(View.VISIBLE);
        secondImageView.setImageResource(R.drawable.icon_edit);

        updateInfo();
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void updateInfo(){
        SetsDto setsDto = SharedUtils.getSavedSetList().get(0);
        String detail = "";
        for (int i = 0; i < setsDto.getComboIDLists().size(); i++){
            if (TextUtils.isEmpty(detail)){
                detail = ComboSetUtil.getComboDtowithID(setsDto.getComboIDLists().get(i)).getCombos();
            }else{
                detail = detail + "\n" + ComboSetUtil.getComboDtowithID(setsDto.getComboIDLists().get(i)).getCombos();
            }
        }

        detailView.setText(detail);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
