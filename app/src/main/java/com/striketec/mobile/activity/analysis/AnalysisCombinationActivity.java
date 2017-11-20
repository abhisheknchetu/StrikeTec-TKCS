package com.striketec.mobile.activity.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.leaderboard.CompareLeaderboardActivity;
import com.striketec.mobile.adapters.CombinationPunchListAdapter;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.GeneratedTrainingResultsComboDto;
import com.striketec.mobile.dto.PunchwithDesiredDto;
import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisCombinationActivity extends BaseActivity {

    public static final String TAG = AnalysisCombinationActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_title) TextView tipsView;
    @BindView(R.id.compareonleaderboar_parent) LinearLayout compareParentView;
    @BindView(R.id.sort_type) TextView sorttypeView;
    @BindView(R.id.sort_type_img) ImageView sorttypeImgView;
    @BindView(R.id.sort_duration) TextView sortdurationView;
    @BindView(R.id.sort_duration_img) ImageView sortdurationImgView;
    @BindView(R.id.sort_speed) TextView sortspeedView;
    @BindView(R.id.sort_speed_img) ImageView sortspeedImgView;
    @BindView(R.id.sort_power) TextView sortpowerView;
    @BindView(R.id.sort_power_img) ImageView sortpowerImgView;
    @BindView(R.id.result_status) TextView resultStatusView;
    @BindView(R.id.listview) ListView punchListView;

    @BindView(R.id.timevalue) TextView besttimeView;
    @BindView(R.id.maxspeed) TextView maxspeedView;
    @BindView(R.id.maxpower) TextView maxpowerView;

    @BindView(R.id.child_sortlayout) View childsortLayout;

    private int currentSortPosition = 0;
    private boolean sorttypeDesc = true, sortdurationDesc = true, sortspeeddesc = true, sortpowerdesc = true;

    TrainingSessionDto sessionDto = null;
    ComboDto comboDto = null;
    int roundId = -1;

    GeneratedTrainingResultsComboDto generatedTrainingResultsComboDto = null;

    ArrayList<PunchwithDesiredDto> comboPunches = new ArrayList<>();
    ArrayList<PunchwithDesiredDto> validcomboPunches = new ArrayList<>();

    boolean fromSession = true;

    int comboId = -1;

    CombinationPunchListAdapter punchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_combination);

        fromSession = getIntent().getBooleanExtra(AppConst.FROM_SESSION, true);

        if (fromSession){
            sessionDto = (TrainingSessionDto)getIntent().getSerializableExtra(AppConst.SESSION_INTENT);
            roundId = sessionDto.mRoundIds.get(0).mId;
            comboId = sessionDto.mPlanId;

            //get punches and generate punch result
        }else {
            generatedTrainingResultsComboDto = (GeneratedTrainingResultsComboDto)getIntent().getSerializableExtra(AppConst.ROUND_INTENT);
            comboPunches = generatedTrainingResultsComboDto.mComboPunches;
            comboId = generatedTrainingResultsComboDto.mComboId;
        }

        comboDto = ComboSetUtil.getComboDtowithID(comboId);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        childsortLayout.setVisibility(View.GONE);

        if (fromSession){
            compareParentView.setVisibility(View.VISIBLE);
        }else {
            compareParentView.setVisibility(View.GONE);
        }

        titleView.setText(comboDto.getName());
        tipsView.setVisibility(View.VISIBLE);
        tipsView.setText(getResources().getString(R.string.tips));

        int trainingStatus = 1;

        if (trainingStatus == 0){
            resultStatusView.setText(getResources().getString(R.string.badperformance));
            resultStatusView.setBackgroundColor(getResources().getColor(R.color.force_color));
        }else if (trainingStatus == 1){
            resultStatusView.setText(getResources().getString(R.string.averageperformance));
            resultStatusView.setBackgroundColor(getResources().getColor(R.color.orange));
        }else {
            resultStatusView.setText(getResources().getString(R.string.excellentperformance));
            resultStatusView.setBackgroundColor(getResources().getColor(R.color.speed_color));
        }

        punchListAdapter = new CombinationPunchListAdapter(this, comboPunches);
        punchListView.setAdapter(punchListAdapter);

        if (fromSession){
            //load punches and generate
            getPuncheswithRoundid(roundId);
        }else {
//            updateSortView();
            updateTotalInfo();
        }
    }

    private void updateTotalInfo(){
        //get best time, max speed, max power
        getValidPunches();

        if (validcomboPunches.size() > 0){
            Collections.sort(validcomboPunches, DURATION_COMPARATOR_DOWN);
            besttimeView.setText(String.valueOf(validcomboPunches.get(0).mPunch.mPunchDuration));

            Collections.sort(validcomboPunches, SPEED_COMPARATOR);
            maxspeedView.setText(String.valueOf((int)validcomboPunches.get(0).mPunch.mSpeed));

            Collections.sort(validcomboPunches, FORCE_COMPARATOR);
            maxpowerView.setText(String.valueOf((int)validcomboPunches.get(0).mPunch.mForce));
        }else {
            besttimeView.setText("0.00");
            maxspeedView.setText("0");
            maxpowerView.setText("0");
        }
    }

    private void getValidPunches(){
        validcomboPunches.clear();
        for (int i = 0; i < comboPunches.size(); i++){
            if (comboPunches.get(i).mPunch != null)
                validcomboPunches.add(comboPunches.get(i));
        }
    }

    @OnClick({R.id.back, R.id.sort_type_parent, R.id.sort_duration_parent, R.id.sort_speed_parent, R.id.sort_power_parent, R.id.second_title, R.id.compareleaderboard})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.sort_type_parent:
                if (currentSortPosition == 0){
                    sorttypeDesc= !sorttypeDesc;
                }else {
                    currentSortPosition = 0;
                }

//                updateSortView();

                break;

            case R.id.sort_duration_parent:

                if (currentSortPosition == 1){
                    sortdurationDesc = !sortdurationDesc;
                }else {
                    currentSortPosition = 1;
                }

//                updateSortView();
                break;

            case R.id.sort_speed_parent:

                if (currentSortPosition == 2){
                    sortspeeddesc = !sortspeeddesc;
                }else {
                    currentSortPosition = 2;
                }

//                updateSortView();
                break;
            case R.id.sort_power_parent:

                if (currentSortPosition == 3){
                    sortpowerdesc = !sortpowerdesc;
                }else {
                    currentSortPosition = 3;
                }

//                updateSortView();
                break;

            case R.id.second_title:
                Intent tipsIntent = new Intent(AnalysisCombinationActivity.this, AnalysisTipsActivity.class);
                startActivity(tipsIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.compareleaderboard:
                Intent compareIntent = new Intent(AnalysisCombinationActivity.this, CompareLeaderboardActivity.class);
                startActivity(compareIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void updateSortView(){
        if (currentSortPosition == 0){

            if (sorttypeDesc){
                sorttypeImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(comboPunches, TYPE_COMPARATOR);
            }else {
                sorttypeImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(comboPunches, TYPE_COMPARATOR_DOWN);
            }

            sorttypeView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else if(currentSortPosition == 1){
            if (sortdurationDesc){
                sortdurationImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(comboPunches, DURATION_COMPARATOR);
            }else {
                sortdurationImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(comboPunches, DURATION_COMPARATOR_DOWN);
            }

            sortdurationView.setTextColor(getResources().getColor(R.color.punches_text));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else if (currentSortPosition == 2){
            if (sortspeeddesc){
                sortspeedImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(comboPunches,SPEED_COMPARATOR);
            }else {
                sortspeedImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(comboPunches,SPEED_COMPARATOR_DOWN);
            }

            sortspeedView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else {
            if (sortpowerdesc){
                sortpowerImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(comboPunches,FORCE_COMPARATOR);
            }else {
                sortpowerImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(comboPunches,FORCE_COMPARATOR_DOWN);
            }

            sortpowerView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
        }

        punchListAdapter.setData(comboPunches);
        punchListAdapter.notifyDataSetChanged();
    }

    private void getPuncheswithRoundid(final int roundId){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.TRAINING_DATA_REST.getPunchesWithRoundId(SharedUtils.getHeader(), String.valueOf(roundId)).enqueue(new IndicatorCallback<ServerTrainingPunchesDtoResponse>(AnalysisCombinationActivity.this) {
                @Override
                public void onResponse(Call<ServerTrainingPunchesDtoResponse> call, Response<ServerTrainingPunchesDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingPunchesDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            //generate combo punches
                            generateComboPunches(responseDto.mPunches);

                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingPunchesDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(AnalysisCombinationActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void generateComboPunches(List<TrainingPunchDto> punchDtos){
        int punchindex = 0;

        comboPunches.clear();

        for (int i = 0; i < comboDto.getComboTypes().size(); i++){
            PunchwithDesiredDto punchwithDesiredDto = new PunchwithDesiredDto();
            punchwithDesiredDto.mDesiredPunchKey = comboDto.getComboTypes().get(i);

            if (comboDto.getComboTypes().get(i).length() > 1){
                //this is movement, for movement punch is valid
                punchwithDesiredDto.mPunch = null;
            }else {
                //this is valid punch
                if (punchDtos.size() > punchindex){
                    punchwithDesiredDto.mPunch = punchDtos.get(punchindex);
                    punchindex++;
                }else {
                    punchwithDesiredDto.mPunch = null;
                }
            }

            comboPunches.add(punchwithDesiredDto);
        }

//        updateSortView();
        punchListAdapter.notifyDataSetChanged();
        updateTotalInfo();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public Comparator<PunchwithDesiredDto> TYPE_COMPARATOR = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return CommonUtils.getTypefromvalue(rhs.mPunch.mPunchType).compareToIgnoreCase(CommonUtils.getTypefromvalue(lhs.mPunch.mPunchType));
            else
                return 1;
        }
    };

    public Comparator<PunchwithDesiredDto> DURATION_COMPARATOR = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (rhs.mPunch.mPunchDuration * 100 - lhs.mPunch.mPunchDuration * 100);
            else
                return 1;
        }
    };

    public Comparator<PunchwithDesiredDto> SPEED_COMPARATOR = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (rhs.mPunch.mSpeed * 10 - lhs.mPunch.mSpeed * 10);
            else
                return 1;
        }
    };

    public Comparator<PunchwithDesiredDto> FORCE_COMPARATOR = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (rhs.mPunch.mForce * 100 - lhs.mPunch.mForce * 100);
            else
                return 1;
        }
    };

    public Comparator<PunchwithDesiredDto> TYPE_COMPARATOR_DOWN = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return CommonUtils.getTypefromvalue(lhs.mPunch.mPunchType).compareToIgnoreCase(CommonUtils.getTypefromvalue(rhs.mPunch.mPunchType));
            else
                return -1;
        }
    };

    public Comparator<PunchwithDesiredDto> DURATION_COMPARATOR_DOWN = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (lhs.mPunch.mPunchDuration * 100 - rhs.mPunch.mPunchDuration * 100);
            return -1;
        }
    };

    public Comparator<PunchwithDesiredDto> SPEED_COMPARATOR_DOWN = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (lhs.mPunch.mSpeed * 10 - rhs.mPunch.mSpeed * 10);
            return -1;
        }
    };

    public Comparator<PunchwithDesiredDto> FORCE_COMPARATOR_DOWN = new Comparator<PunchwithDesiredDto>() {
        @Override
        public int compare(PunchwithDesiredDto lhs, PunchwithDesiredDto rhs) {
            if (rhs.mPunch != null && lhs.mPunch != null)
                return (int) (lhs.mPunch.mForce * 100 - rhs.mPunch.mForce * 100);
            return -1;
        }
    };
}
