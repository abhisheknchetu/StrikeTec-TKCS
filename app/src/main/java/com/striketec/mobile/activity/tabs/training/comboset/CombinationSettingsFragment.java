package com.striketec.mobile.activity.tabs.training.comboset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class CombinationSettingsFragment extends Fragment {

    public static String TAG = CombinationSettingsFragment.class.getSimpleName();

    @BindView(R.id.listview) ListView combinationListView;

    ArrayList<ComboDto> comboDtos = new ArrayList<>();
    CombinationListAdapter combinationListAdapter;

    public static CombinationSettingsFragment noroundSettingsFragment;
    private static Context mContext;

    public static CombinationSettingsFragment newInstance(Context context) {
        mContext = context;
        noroundSettingsFragment = new CombinationSettingsFragment();
        return noroundSettingsFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_combination_settings, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        comboDtos = SharedUtils.getSavedCombinationList();
        combinationListAdapter = new CombinationListAdapter(getActivity(), comboDtos);
        combinationListView.setAdapter(combinationListAdapter);
    }

    @OnClick({R.id.starttraining})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.starttraining:
                startTraining();
                break;
        }
    }

    private void startTraining(){
        Intent trainingIntent = new Intent(getActivity(), PlansTrainingActivity.class);
        trainingIntent.putExtra(AppConst.TRAININGTYPE, AppConst.COMBINATION);
        trainingIntent.putExtra(AppConst.COMBO_ID, combinationListAdapter.getItem(combinationListAdapter.getCurrentPosition()).getId());
        startActivity(trainingIntent);
        getActivity().finish();
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){
        combinationListAdapter.setCurrentPosition(position);
        combinationListAdapter.notifyDataSetChanged();
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
