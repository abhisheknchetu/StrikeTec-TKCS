package com.striketec.mobile.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.calendarlibarary.caldroid.CaldroidGridAdapter;
import com.calendarlibarary.caldroid.CalendarHelper;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class CalendarCustomAdapter extends CaldroidGridAdapter {

    Context mContext;
    LayoutInflater inflater;
    CalendarDialog calendarDialog;

    public CalendarCustomAdapter(Context context, int month, int year, Map<String, Object> caldroidData,
                                 Map<String, Object> extraData){
        super(context, month, year, caldroidData, extraData);

        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.calendarDialog = CalendarDialog.calendarDialog;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_calendaritem, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final DateTime dateTime = this.datetimeList.get(position);
        viewHolder.dateView.setText("" + dateTime.getDay());

        viewHolder.leftBgView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        viewHolder.rightBgView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        viewHolder.circleBgView.setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));

        if (dateTime.getMonth() != month){
            viewHolder.dateView.setAlpha(0.4f);
        }else
            viewHolder.dateView.setAlpha(1f);

        if (calendarDialog != null) {

            if (calendarDialog.startDateTime != null && calendarDialog.endDateTime != null && dateTime.equals(calendarDialog.startDateTime) && dateTime.equals(calendarDialog.endDateTime)){
                viewHolder.circleBgView.setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.orange)));
            }else {
                if (calendarDialog.startDateTime != null && dateTime.equals(calendarDialog.startDateTime)) {
                    viewHolder.circleBgView.setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.orange)));
                    if (calendarDialog.endDateTime != null)
                        viewHolder.rightBgView.setBackgroundColor(mContext.getResources().getColor(R.color.first_orange));
                }

                if (calendarDialog.endDateTime != null && dateTime.equals(calendarDialog.endDateTime)) {
                    viewHolder.circleBgView.setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.orange)));
                    if (calendarDialog.startDateTime != null)
                        viewHolder.leftBgView.setBackgroundColor(mContext.getResources().getColor(R.color.first_orange));
                }

                if (calendarDialog.startDateTime != null && calendarDialog.endDateTime != null && dateTime.lt(calendarDialog.endDateTime) && dateTime.gt(calendarDialog.startDateTime)) {
                    viewHolder.leftBgView.setBackgroundColor(mContext.getResources().getColor(R.color.first_orange));
                    viewHolder.rightBgView.setBackgroundColor(mContext.getResources().getColor(R.color.first_orange));
                }
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarDialog.isAnimation())
                    return;

                if (dateTime.getMonth() != month){
                    if (dateTime.getYear() > year){
                        calendarDialog.caldroidFragment.getRightArrowButton().performClick();
                    }else if (dateTime.getYear() < year){
                        calendarDialog.caldroidFragment.getLeftArrowButton().performClick();
                    }else {
                        if (dateTime.getMonth() > month){
                            calendarDialog.caldroidFragment.getRightArrowButton().performClick();
                        }else {
                            calendarDialog.caldroidFragment.getLeftArrowButton().performClick();
                        }
                    }
                }else {
                    if (calendarDialog.isGoal){
                        if (dateTime.equals(calendarDialog.startDateTime)){
                            return;
                        }else {
                            calendarDialog.startDateTime = dateTime;
                            Date firstDate = CalendarHelper.convertDateTimeToDate(dateTime);
                            Date lastDate = DatesUtil.addDate(firstDate, 6);
                            calendarDialog.endDateTime = CalendarHelper.convertDateToDateTime(lastDate);
                            notifyDataSetChanged();
                            calendarDialog.updateView();
                            return;
                        }
                    }else {
                        if (dateTime.equals(calendarDialog.startDateTime)){
                            calendarDialog.startDateTime = null;
                            notifyDataSetChanged();
                            calendarDialog.updateView();
                            return;
                        }

                        if (dateTime.equals(calendarDialog.endDateTime)){
                            calendarDialog.endDateTime = null;
                            notifyDataSetChanged();
                            calendarDialog.updateView();
                            return;
                        }

                        if (calendarDialog.endDateTime != null && calendarDialog.startDateTime != null){
                            calendarDialog.moveAnimation();
                            return;
                        }

                        if (calendarDialog.startDateTime == null){
                            if (calendarDialog.endDateTime != null) {
                                if (calendarDialog.endDateTime.lt(dateTime)) {
                                    calendarDialog.startDateTime = calendarDialog.endDateTime;
                                    calendarDialog.endDateTime = dateTime;
                                    notifyDataSetChanged();
                                    calendarDialog.updateView();
                                    return;
                                } else {
                                    calendarDialog.startDateTime = dateTime;
                                    notifyDataSetChanged();
                                    calendarDialog.updateView();
                                    return;
                                }
                            }else {
                                calendarDialog.startDateTime = dateTime;
                                notifyDataSetChanged();
                                calendarDialog.updateView();
                                return;
                            }
                        }else {
                            if (calendarDialog.startDateTime.lt(dateTime)){
                                calendarDialog.endDateTime = dateTime;
                                notifyDataSetChanged();
                                calendarDialog.updateView();
                                return;
                            }else {
                                calendarDialog.endDateTime = calendarDialog.startDateTime;
                                calendarDialog.startDateTime = dateTime;
                                notifyDataSetChanged();
                                calendarDialog.updateView();
                                return;
                            }
                        }
                    }

                }
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.leftview) View leftBgView;
        @BindView(R.id.rightview) View rightBgView;
        @BindView(R.id.circleimageView) ImageView circleBgView;
        @BindView(R.id.datetext) TextView dateView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

