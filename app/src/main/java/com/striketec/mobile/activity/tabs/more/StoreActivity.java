package com.striketec.mobile.activity.tabs.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.SensororderListAdapter;
import com.striketec.mobile.dto.SensorOrderDto;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener<SensororderListAdapter.ViewHolder>{

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.listview) DiscreteScrollView horizontalScrollView;
    @BindView(R.id.store_description) TextView sensorDescriptionView;

    ArrayList<SensorOrderDto> sensorOrderDtos = new ArrayList<>();
    SensororderListAdapter adapter;

    private SensororderListAdapter.ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.store));

        adapter = new SensororderListAdapter(sensorOrderDtos);
        horizontalScrollView.setAdapter(adapter);

        horizontalScrollView.addOnItemChangedListener(this);
        loadData();
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

    private void loadData(){
        for (int i = 0; i < 10; i++){
            sensorOrderDtos.add(new SensorOrderDto("Sensor test", R.drawable.img_sensor_default));
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onCurrentItemChanged(@Nullable SensororderListAdapter.ViewHolder viewHolder, int adapterPosition) {

        if (this.viewHolder != null)
            this.viewHolder.setOverlay(0.2f);

        this.viewHolder = viewHolder;
        viewHolder.setOverlay(0.8f);
    }
}
