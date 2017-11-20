package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.QuestionDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionListAdapter extends ArrayAdapter<QuestionDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<QuestionDto> questionDtos;

    private SparseBooleanArray checkedList = new SparseBooleanArray();

    public QuestionListAdapter(Context context, ArrayList<QuestionDto> questionDtos){
        super(context, 0, questionDtos);

        mContext = context;
        this.questionDtos = questionDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initCheck();
    }

    public void setData (ArrayList<QuestionDto> questionDtos){
        this.questionDtos = questionDtos;
        initCheck();
        notifyDataSetChanged();
    }

    public void initCheck(){
        for (int i = 0; i < questionDtos.size(); i++){
            checkedList.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return questionDtos.size();
    }

    @Nullable
    @Override
    public QuestionDto getItem(int position) {
        return questionDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_helpcenter, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        QuestionDto questionDto = getItem(position);
        viewHolder.subjectView.setText(questionDto.mSubject);
        viewHolder.messageView.setText(questionDto.mMessage);

        if (checkedList.get(position)){
            viewHolder.arrowView.setImageResource(R.drawable.arrow_top);
            viewHolder.messageLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.arrowView.setImageResource(R.drawable.arrow_bottom);
            viewHolder.messageLayout.setVisibility(View.GONE);
        }

        viewHolder.subjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheck(position);
            }
        });

        return convertView;
    }

    private void toggleCheck(int position){
        if (checkedList.get(position)){
            checkedList.put(position, false);
        }else {
            checkedList.put(position, true);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder {
        @BindView(R.id.subject_layout)  LinearLayout subjectLayout;
        @BindView(R.id.message_layout) LinearLayout messageLayout;
        @BindView(R.id.subject) TextView subjectView;
        @BindView(R.id.message) TextView messageView;
        @BindView(R.id.arrow) ImageView arrowView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

