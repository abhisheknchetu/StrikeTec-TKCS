package com.striketec.mobile.activity.tabs.challenge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.striketec.mobile.R;
import com.striketec.mobile.adapters.ChallengeBattlesListAdapter;
import com.striketec.mobile.dto.BattleResult_Temp;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BattlesFragment extends Fragment {

    public static String TAG = BattlesFragment.class.getSimpleName();

    @BindView(R.id.receivedlistview) RecyclerView receivedListView;
    @BindView(R.id.mybattleslistview) RecyclerView mybattlesListView;
    @BindView(R.id.finishedlistview) RecyclerView finishedBattlesListView;

    private ArrayList<BattleResult_Temp> receivedBattles = new ArrayList<>();
    private ArrayList<BattleResult_Temp> myBattles = new ArrayList<>();
    private ArrayList<BattleResult_Temp> finishedBattles = new ArrayList<>();

    ChallengeBattlesListAdapter battlesListAdapter, mybattlesListAdapter, finishedBattlesListAdapter;

    public static BattlesFragment battlesFragment
            ;
    private static Context mContext;

    public static BattlesFragment newInstance(Context context) {
        mContext = context;
        battlesFragment = new BattlesFragment();
        return battlesFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_battles, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        receivedListView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mybattlesListView.setLayoutManager(layoutManager1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        finishedBattlesListView.setLayoutManager(layoutManager2);

        battlesListAdapter = new ChallengeBattlesListAdapter(receivedBattles, AppConst.TYPE_RECEIVED_BATTLES);
        receivedListView.setAdapter(battlesListAdapter);

        mybattlesListAdapter = new ChallengeBattlesListAdapter(myBattles, AppConst.TYPE_MY_BATTLES);
        mybattlesListView.setAdapter(mybattlesListAdapter);

        finishedBattlesListAdapter = new ChallengeBattlesListAdapter(finishedBattles, AppConst.TYPE_FINISHED_BATTLES);
        finishedBattlesListView.setAdapter(finishedBattlesListAdapter);

        battlesListAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showToastMessage("Received Battles item is Clicked");
            }
        });

        mybattlesListAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showToastMessage("My Battles item is clicked");
            }
        });


        finishedBattlesListAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showToastMessage("Finished Battles item is Clicked");
            }
        });


        loadBattles();
    }

    private void loadBattles(){

        for (int i = 0; i < 12; i++){
            BattleResult_Temp result_temp = new BattleResult_Temp();
            result_temp.oppoenentUserName = "Philip Todd";
            result_temp.points = CommonUtils.getRandomNum(9999, 1000);

            receivedBattles.add(result_temp);
        }

        battlesListAdapter.notifyDataSetChanged();

        for (int i = 0; i < 8; i++){
            BattleResult_Temp result_temp = new BattleResult_Temp();
            result_temp.oppoenentUserName = "John Smith";
            result_temp.points = CommonUtils.getRandomNum(9999, 1000);

            myBattles.add(result_temp);
        }

        mybattlesListAdapter.notifyDataSetChanged();

        for (int i = 0; i < 8; i++){
            BattleResult_Temp result_temp = new BattleResult_Temp();
            result_temp.oppoenentUserName = "Danna Todd";
            result_temp.points = CommonUtils.getRandomNum(9999, 1000);

            finishedBattles.add(result_temp);
        }

        finishedBattlesListAdapter.notifyDataSetChanged();



    }


    @Override
    public void onResume() {
        super.onResume();
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
