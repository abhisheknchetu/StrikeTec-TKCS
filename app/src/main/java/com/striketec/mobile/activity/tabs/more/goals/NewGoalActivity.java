package com.striketec.mobile.activity.tabs.more.goals;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.activity.tabs.training.quickstart.TrainingRoundSettingsFragment;
import com.striketec.mobile.adapters.GoalActivityTypeListAdapter;
import com.striketec.mobile.adapters.GoalListAdapter;
import com.striketec.mobile.adapters.PresetListAdapter;
import com.striketec.mobile.dto.GoalDto;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewGoalActivity extends BaseActivity implements CalendarDialog.DialogListener{

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.activity) Button activityBtn;
    @BindView(R.id.type) Button typeBtn;
    @BindView(R.id.target) Button targetBtn;
    @BindView(R.id.startdate) Button startdateBtn;
    @BindView(R.id.goal_description) TextView goalDescriptionView;
    @BindView(R.id.next_parent) LinearLayout nextParentView;

    int activityposition = -1;
    int goaltypeposition = -1;

    GoalDto goalDto = new GoalDto();

    String startDateString, endDateString;
    Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.new_goal));

        goalDto.mActivity = "";
        goalDto.mType = "";
        goalDto.mGoal = 0;
        goalDto.mFinished = 0;
        goalDto.mStartDate = "";

        resetView();
    }

    @OnClick({R.id.back, R.id.activity, R.id.type, R.id.target, R.id.startdate, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.activity:
                showSelectActivityTargetDialog(true);
                break;

            case R.id.type:
                showSelectActivityTargetDialog(false);
                break;

            case R.id.target:
                if (TextUtils.isEmpty(goalDto.mType)){
                    CommonUtils.showToastMessage(getResources().getString(R.string.empty_type));

                    return;
                }

                showGoalDialog();
                break;

            case R.id.startdate:

                CalendarDialog fragment = null;
                if (TextUtils.isEmpty(goalDto.mStartDate))
                    fragment = CalendarDialog.newInstance(DatesUtil.getDefaultTimefromMilliseconds(DatesUtil.getFirstDayofWeek()), DatesUtil.getDefaultTimefromMilliseconds(DatesUtil.getEndDayofWeek()), true);
                else
                    fragment = CalendarDialog.newInstance(startDateString, endDateString, true);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                    ft.remove(prev);

                ft.addToBackStack(null);

                fragment.show(ft, "dialog");
                break;

            case R.id.next:
                finish();
                break;
        }
    }

    private void resetView(){
        if (TextUtils.isEmpty(goalDto.mActivity)){
            activityBtn.setText(getResources().getString(R.string.activity));
        }else {
            activityBtn.setText(goalDto.mActivity);
        }

        if (TextUtils.isEmpty(goalDto.mType)){
            typeBtn.setText(getResources().getString(R.string.type));
        }else {
            typeBtn.setText(String.format(getResources().getString(R.string.goal_type), goalDto.mType));
        }

        if (goalDto.mGoal < 1){
            if (TextUtils.isEmpty(goalDto.mType))
                targetBtn.setText(String.format(getResources().getString(R.string.target), getResources().getString(R.string.goal_type_1)));
            else
                targetBtn.setText(String.format(getResources().getString(R.string.target), goalDto.mType));
        }else {
            targetBtn.setText(String.format(getResources().getString(R.string.target_content), goalDto.mGoal, goalDto.mType));
        }

        if (TextUtils.isEmpty(goalDto.mStartDate)){
            startdateBtn.setText(getResources().getString(R.string.goal_startdate));
        }else {
            startdateBtn.setText(DatesUtil.getDuration(startDate, endDate));
        }

        if (!TextUtils.isEmpty(goalDto.mActivity) && !TextUtils.isEmpty(goalDto.mType) && !TextUtils.isEmpty(goalDto.mStartDate) && goalDto.mGoal > 0){
            nextParentView.setVisibility(View.VISIBLE);
            String description = String.format(getString(R.string.goal_descriptionwithenter), goalDto.mGoal, goalDto.mActivity, goalDto.mType);
            goalDescriptionView.setText(description);

        }else {
            nextParentView.setVisibility(View.GONE);
        }
    }

    private void showSelectActivityTargetDialog(final boolean isActivity){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_presetpopup);

        ListView listView = (ListView)dialog.findViewById(R.id.listview);
        final GoalActivityTypeListAdapter adapter;
        if (isActivity){
            adapter = new GoalActivityTypeListAdapter(this, PresetUtil.goalActivityList, true);
            adapter.setCurrentposition(activityposition);
        }else {
            adapter = new GoalActivityTypeListAdapter(this, PresetUtil.goalTypeList, false);
            adapter.setCurrentposition(goaltypeposition);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isActivity){
                    if (position != activityposition){
                        activityposition = position;
                        activityBtn.setText(adapter.getItem(position));
                        goalDto.mActivity = PresetUtil.goalActivityList.get(position);
                    }
                }else {
                    if (position != goaltypeposition){
                        goaltypeposition = position;
                        typeBtn.setText(adapter.getItem(position));
                        goalDto.mType = PresetUtil.goalTypeList.get(position);
                    }
                }
                dialog.dismiss();
                resetView();
            }
        });

        dialog.show();
    }

    private void showGoalDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_savenew_digit);

        final SaveDialogViewHolder saveDialogViewHolder = new SaveDialogViewHolder(dialog);
        saveDialogViewHolder.titleView.setText(typeBtn.getText().toString().trim());

        saveDialogViewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveDialogViewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goal = saveDialogViewHolder.valueView.getText().toString().trim();
                if (TextUtils.isEmpty(goal)){
                    CommonUtils.showToastMessage(getResources().getString(R.string.empty_goal));
                    return;
                }else if (Integer.parseInt(goal) < 1){
                    CommonUtils.showToastMessage(getResources().getString(R.string.empty_goal));
                    return;
                }else {
                    goalDto.mGoal = Integer.parseInt(goal);
                    dialog.dismiss();
                    resetView();
                }
            }
        });

        dialog.show();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDialogClosed() {

    }

    @Override
    public void onDateChanged(Date startDate, Date endDate) {
        startDateString = DatesUtil.changeDatetoString(startDate);
        endDateString = DatesUtil.changeDatetoString(endDate);
        goalDto.mStartDate = startDateString;
        this.startDate = startDate;
        this.endDate = endDate;

        resetView();
    }

    public class SaveDialogViewHolder {
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.value) EditText valueView;
        @BindView(R.id.ok_btn) Button saveBtn;
        @BindView(R.id.cancel_btn) Button cancelBtn;

        SaveDialogViewHolder(Dialog dialog){
            ButterKnife.bind(this, dialog);
        }
    }
}
