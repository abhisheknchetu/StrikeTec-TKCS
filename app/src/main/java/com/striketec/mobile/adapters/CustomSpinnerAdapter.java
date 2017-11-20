package com.striketec.mobile.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.util.AppConst;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;
    private ArrayList<String> mObjects;
    private int mKind;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects, int spinnerKind) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mResource = textViewResourceId;
        mObjects = objects;
        mKind = spinnerKind;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(mResource, parent, false);
        TextView label = (TextView) row.findViewById(R.id.item);
        label.setText(mObjects.get(position));
        if (position %2 == 1){
            label.setBackgroundColor(mContext.getResources().getColor(R.color.selectbg));
        }else {
            label.setBackgroundColor(mContext.getResources().getColor(R.color.unselectbg));
        }
        return row;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = null;

        if (mKind == AppConst.SPINNER_DIGIT_ORANGE)
            row = inflater.inflate(R.layout.custom_spinner_orange_digit, parent, false);
        else if (mKind == AppConst.SPINNER_TEXT_ORANGE)
            row = inflater.inflate(R.layout.custom_spinner_orange_text, parent, false);


        TextView label = (TextView) row.findViewById(R.id.custom_spinner_textView);
        label.setText(mObjects.get(position));
        return row;
    }
}
