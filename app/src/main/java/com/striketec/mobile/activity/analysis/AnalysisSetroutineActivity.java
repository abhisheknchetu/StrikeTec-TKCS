package com.striketec.mobile.activity.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.leaderboard.CompareLeaderboardActivity;
import com.striketec.mobile.adapters.AnalysisComboListAdapter;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.PunchwithDesiredDto;
import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.dto.GeneratedTrainingResultsComboDto;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.dto.TrainingRoundDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.dto.WorkoutDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisSetroutineActivity extends BaseActivity {

    private static String TAG = AnalysisSetroutineActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.timevalue) TextView timevalueView;
    @BindView(R.id.punchcount) TextView punchcountView;
    @BindView(R.id.combinationcount) TextView combinationcountView;
    @BindView(R.id.listview) ListView combolistView;

    TrainingSessionDto sessionDto = null;
    int roundId = -1;
    TrainingRoundDto roundDto = null;
    ArrayList<Integer> comboIdList = new ArrayList<>();

    WorkoutDto workoutDto = null;
    int planid = -1;
    int workoutId = -1;
    int roundPosition = -1;
    boolean isworkout = false;

    ArrayList<TrainingPunchDto> punchDtos = new ArrayList<>();
    ArrayList<GeneratedTrainingResultsComboDto> generatedTrainingResultsComboDtos = new ArrayList<>();

    AnalysisComboListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_set);


        workoutId = getIntent().getIntExtra(AppConst.WORKOUT_ID, -1);

        if (workoutId == -1) {
            isworkout = false;
            sessionDto = (TrainingSessionDto)getIntent().getSerializableExtra(AppConst.SESSION_INTENT);
            roundId = sessionDto.mRoundIds.get(0).mId;
            planid = sessionDto.mPlanId;
        }
        else {
            isworkout = true;
            workoutDto = ComboSetUtil.getWorkoutDtoWithID(workoutId);
            roundDto = (TrainingRoundDto)getIntent().getSerializableExtra(AppConst.ROUND_INTENT);
            roundId = roundDto.mId;
            roundPosition = getIntent().getIntExtra(AppConst.WORKOUT_ROUND_POSITION, 0);
        }

        ButterKnife.bind(this);

        loadSetComboData();
        initViews();
    }

    private void loadSetComboData(){


        if (isworkout){
            comboIdList = workoutDto.getRoundsetIDs().get(roundPosition);
        }else
            comboIdList = ComboSetUtil.getSetDtowithID(planid).getComboIDLists();

        getPuncheswithRoundid(roundId);
    }

    private void initViews(){
        if (isworkout) {
            titleView.setText(String.format(getResources().getString(R.string.roundtitle), String.valueOf(roundPosition + 1)));
            timevalueView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(roundDto.mEndTime) - Long.parseLong(roundDto.mStartTime)) / 1000)));
            punchcountView.setText(String.valueOf(roundDto.mPunchCount));

        }else {
            titleView.setText(ComboSetUtil.getSetDtowithID(planid).getName());
            timevalueView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(sessionDto.mEndTime) - Long.parseLong(sessionDto.mStartTime)) / 1000)));
            punchcountView.setText(String.valueOf(sessionDto.mPunchCount));
        }

        combinationcountView.setText(String.valueOf(comboIdList.size()));

        listAdapter = new AnalysisComboListAdapter(this, generatedTrainingResultsComboDtos);
        combolistView.setAdapter(listAdapter);
    }

    private void getPuncheswithRoundid(final int roundId){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.TRAINING_DATA_REST.getPunchesWithRoundId(SharedUtils.getHeader(), String.valueOf(roundId)).enqueue(new IndicatorCallback<ServerTrainingPunchesDtoResponse>(AnalysisSetroutineActivity.this) {
                @Override
                public void onResponse(Call<ServerTrainingPunchesDtoResponse> call, Response<ServerTrainingPunchesDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingPunchesDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            roundDto = responseDto.mRound;
                            punchDtos.clear();
                            punchDtos.addAll(responseDto.mPunches);
                            //generate combo punches
                            generateComboPunches();

                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingPunchesDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(AnalysisSetroutineActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void generateComboPunches(){
        int punchindex = 0;
        for (int i = 0; i < comboIdList.size(); i++){
            GeneratedTrainingResultsComboDto trainingComboResults = new GeneratedTrainingResultsComboDto();

            ComboDto tmpComboDto = ComboSetUtil.getComboDtowithID(comboIdList.get(i));
            ArrayList<PunchwithDesiredDto> punchwithDesiredDtos = new ArrayList<>();

            trainingComboResults.mComboId = tmpComboDto.getId();

            int punchcount = tmpComboDto.getComboTypes().size();
            for (int j = 0; j < punchcount; j++){
                PunchwithDesiredDto punchwithDesiredDto = new PunchwithDesiredDto();
                punchwithDesiredDto.mDesiredPunchKey = tmpComboDto.getComboTypes().get(j);
                if (tmpComboDto.getComboTypes().get(j).length() > 1){
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

                punchwithDesiredDtos.add(punchwithDesiredDto);
            }

            trainingComboResults.mComboPunches = punchwithDesiredDtos;
            generatedTrainingResultsComboDtos.add(trainingComboResults);
        }

        listAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.back, R.id.compareleaderboard})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.compareleaderboard:
                Intent compareIntent = new Intent(AnalysisSetroutineActivity.this, CompareLeaderboardActivity.class);
                startActivity(compareIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){
        GeneratedTrainingResultsComboDto generatedTrainingResultsComboDto = listAdapter.getItem(position);
        Intent comboanalysisIntent  = new Intent(this, AnalysisCombinationActivity.class);
        comboanalysisIntent.putExtra(AppConst.ROUND_INTENT, generatedTrainingResultsComboDto);
        comboanalysisIntent.putExtra(AppConst.FROM_SESSION, false);
        startActivity(comboanalysisIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
