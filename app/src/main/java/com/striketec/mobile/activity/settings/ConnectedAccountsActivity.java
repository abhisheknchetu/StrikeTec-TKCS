package com.striketec.mobile.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectedAccountsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectedaccounts);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.connectedaccoutns));
    }

    @OnClick({R.id.back, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.next:
                finish();
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
