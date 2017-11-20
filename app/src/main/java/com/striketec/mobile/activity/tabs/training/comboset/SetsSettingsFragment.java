package com.striketec.mobile.activity.tabs.training.comboset;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.util.SharedUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SetsSettingsFragment extends Fragment {

    public static String TAG = SetsSettingsFragment.class.getSimpleName();

    @BindView(R.id.listview)
    ListView setListView;

    ArrayList<SetsDto> setsDtos = new ArrayList<>();
    SetroutineListAdapter setroutineListAdapter ;

    public static SetsSettingsFragment noroundSettingsFragment;
    private static Context mContext;

    public static SetsSettingsFragment newInstance(Context context) {
        mContext = context;
        noroundSettingsFragment = new SetsSettingsFragment();
        return noroundSettingsFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_routine_settings, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        setsDtos = SharedUtils.getSavedSetList();
        setroutineListAdapter = new SetroutineListAdapter(getActivity(), setsDtos);
        setListView.setAdapter(setroutineListAdapter);
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
