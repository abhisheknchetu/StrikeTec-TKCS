package com.striketec.mobile.activity.subscription;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 user : rakeshk2
 date : 11/13/2017
 description : This class provides option to Re-subscribe
 the canceled Subscription plan.
 **/
public class ReSubscribeActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.subscriptionText)
    TextView subscriptionText;
    @BindView(R.id.resubscribe)
    Button resubscribe;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resubscribe);

        ButterKnife.bind(this);

        initViews();

    }


    private void initViews() {
        mContext = ReSubscribeActivity.this;
        titleView.setText(getResources().getString(R.string.mysubsriptions));

    }

    @OnClick({R.id.resubscribe, R.id.back})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.back:
                    finish();
                    break;

                case R.id.resubscribe:
                    finish();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
