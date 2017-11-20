package com.striketec.mobile.activity.calendar;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calendarlibarary.caldroid.CaldroidFragment;
import com.calendarlibarary.caldroid.CaldroidListener;
import com.calendarlibarary.caldroid.CalendarHelper;
import com.striketec.mobile.R;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v4.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

/**
 * Created by Qiang on 9/11/2017.
 */

public class CalendarDialog extends DialogFragment {

    @BindView(R.id.dialog_title) TextView dialogTitle;
    @BindView(R.id.startdate) TextView startDateView;
    @BindView(R.id.enddate) TextView enddateView;
    @BindView(R.id.divider) LinearLayout dividerView;
    @BindView(R.id.end_layout) LinearLayout endParent;
    @BindView(R.id.filter) TextView filterView;
    @BindView(R.id.cancel) TextView cancelView;

    public CalendarFragment caldroidFragment;
    public String startDateString, endDateString;

    SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    public Date startDate, endDate;
    public DateTime startDateTime, endDateTime;
    public boolean isGoal;

    private Timer timer;
    private TimerTask timerTask;
    private Handler mHandler = new Handler();
    private int count = 0;
    private int maxcount = 10;

    public static CalendarDialog calendarDialog;

    private int originX = 0;
    private boolean isAnimation = false;


    public static CalendarDialog newInstance(String startDate, String endDate, boolean isGoal){
        Bundle args = new Bundle();
        calendarDialog = new CalendarDialog();
        args.putString(AppConst.START_DATE, startDate);
        args.putString(AppConst.END_DATE, endDate);
        args.putBoolean(AppConst.IS_GOAL, isGoal);
        calendarDialog.setArguments(args);
        return calendarDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendar, container, false);

        startDateString = getArguments().getString(AppConst.START_DATE);
        endDateString = getArguments().getString(AppConst.END_DATE);
        isGoal = getArguments().getBoolean(AppConst.IS_GOAL);

        ButterKnife.bind(this, view);

        if (isGoal){
            dividerView.setVisibility(View.GONE);
            endParent.setVisibility(View.GONE);
            cancelView.setVisibility(View.VISIBLE);
            filterView.setText(getResources().getString(R.string.ok));
            dialogTitle.setText(getResources().getString(R.string.goalduration));
        }else {
            dividerView.setVisibility(View.VISIBLE);
            endParent.setVisibility(View.VISIBLE);
            cancelView.setVisibility(View.GONE);
            filterView.setText(getResources().getString(R.string.filter));
            dialogTitle.setText(getResources().getString(R.string.selectperiod));
        }

        caldroidFragment = new CalendarFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();

        try {
            startDate = inputFormat.parse(startDateString);
            endDate = inputFormat.parse(endDateString);
            startDateTime = CalendarHelper.convertDateToDateTime(startDate);
            endDateTime = CalendarHelper.convertDateToDateTime(endDate);

            if (startDateTime.gt(endDateTime)){
                endDate = inputFormat.parse(startDateString);
                startDate = inputFormat.parse(endDateString);
                startDateTime = CalendarHelper.convertDateToDateTime(startDate);
                endDateTime = CalendarHelper.convertDateToDateTime(endDate);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        startDateView.setText(dateFormat.format(startDate));
        enddateView.setText(dateFormat.format(endDate));

        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, false);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.calendar_container, caldroidFragment);
        t.commit();

        CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);
            }

            @Override
            public void onCaldroidViewCreated() {
                super.onCaldroidViewCreated();
                if (startDate != null)
                    caldroidFragment.moveToDate(startDate);
            }
        };

        Dialog dialog = CalendarDialog.this.getDialog();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        originX = wmlp.x;

        caldroidFragment.setCaldroidListener(caldroidListener);

        return view;
    }

    @OnClick({R.id.back, R.id.filter, R.id.cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                if (isAnimation)
                    return;

                if (getActivity() instanceof  DialogListener){
                    ((DialogListener)getActivity()).onDialogClosed();
                }

                CalendarDialog.this.getDialog().dismiss();
                break;
            case R.id.filter:
                if (isAnimation)
                    return;

                if (startDateTime != null && endDateTime != null) {
                    if (getActivity() instanceof DialogListener) {
                        ((DialogListener) getActivity()).onDateChanged(startDate, endDate);
                    }

                    CalendarDialog.this.getDialog().dismiss();
                }else {
                    if (!isGoal)
                        CommonUtils.showToastMessage(getResources().getString(R.string.error_emptydate));
                    else
                        CommonUtils.showToastMessage(getResources().getString(R.string.error_emptystartdate));
                }

                break;

            case R.id.cancel:
                if (isAnimation)
                    return;

                if (getActivity() instanceof  DialogListener){
                    ((DialogListener)getActivity()).onDialogClosed();
                }

                CalendarDialog.this.getDialog().dismiss();
                break;
        }
    }

    public void moveAnimation(){
        isAnimation = true;
        count = 0;
        startTimer();
    }

    private void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 30);
    }

    private void stopTimer(){




        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (maxcount == count){
                            stopTimer();
                            isAnimation = false;

                            Dialog dialog = CalendarDialog.this.getDialog();

                            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                            wmlp.x = originX;
                            dialog.getWindow().setAttributes(wmlp);
                        }else {
                            doAnimation();
                            count++;
                        }
                    }
                });
            }
        };
    }

    public boolean isAnimation(){
        return isAnimation;
    }

    private void doAnimation(){
        Dialog dialog = CalendarDialog.this.getDialog();

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        if (count %2 == 1){
            wmlp.x = originX + CommonUtils.dpTopx(5);
        }else {
            wmlp.x = originX - CommonUtils.dpTopx(5);
        }

        dialog.getWindow().setAttributes(wmlp);
    }

    public void updateView(){
        startDateView.setText("");
        enddateView.setText("");
        startDate = null;
        endDate = null;

        if (startDateTime != null){
            startDate = CalendarHelper.convertDateTimeToDate(startDateTime);
            startDateView.setText(dateFormat.format(startDate));
        }

        if (endDateTime != null){
            endDate = CalendarHelper.convertDateTimeToDate(endDateTime);
            enddateView.setText(dateFormat.format(endDate));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }

    public interface DialogListener {
        void onDialogClosed();
        void onDateChanged(Date startDate, Date endDate);
    }
}
