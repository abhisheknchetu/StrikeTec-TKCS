package com.striketec.mobile.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.credential.SignupSensorActivity;
import com.striketec.mobile.dto.SensorDTO;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SensorListAdapter extends ArrayAdapter<SensorDTO> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<SensorDTO> sensorDTOs;

    private int currentPosition = -1;

    public SensorListAdapter(Context context, ArrayList<SensorDTO> sensorDTOs){
        super(context, 0, sensorDTOs);

        mContext = context;
        this.sensorDTOs = sensorDTOs;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<SensorDTO> sensorDTOs) {
        this.sensorDTOs = sensorDTOs;
    }

    @Override
    public int getCount() {
        return sensorDTOs.size();
    }

    @Nullable
    @Override
    public SensorDTO getItem(int position) {
        return sensorDTOs.get(position);
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
            convertView = inflater.inflate(R.layout.item_connectsensor_row, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final SensorDTO sensorDTO = getItem(position);

        viewHolder.addressView.setText(sensorDTO.bluetoothDevice.getName() + " : " + sensorDTO.bluetoothDevice.getAddress());

        if (sensorDTO.isSet){
            viewHolder.checkiconView.setImageResource(R.drawable.img_check);
        }else
            viewHolder.checkiconView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewHolder.selectLeftSensorView.setVisibility(View.VISIBLE);
        viewHolder.selectRightSensorView.setVisibility(View.VISIBLE);

        if (currentPosition == position){
            viewHolder.selectsensorParentView.setVisibility(View.VISIBLE);

//            if (isLeftSelected())
//                viewHolder.selectLeftSensorView.setVisibility(View.INVISIBLE);
//
//            if (isRightSelected())
//                viewHolder.selectRightSensorView.setVisibility(View.INVISIBLE);

        }else {
            viewHolder.selectsensorParentView.setVisibility(View.GONE);
        }

        viewHolder.addressParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition == position){
                    currentPosition = -1;
                }else {
                    currentPosition = position;
                }

                notifyDataSetChanged();
            }
        });

        viewHolder.selectLeftSensorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLeftSelected()){
                    showConflictDialog(sensorDTO, position, true);
                }else {
                    sensorDTO.isLeft = true;
                    sensorDTO.isSet = true;
                    currentPosition = -1;
                    notifyDataSetChanged();

                    updateView();
                }
            }
        });

        viewHolder.selectRightSensorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRightSelected()){
                    showConflictDialog(sensorDTO, position, false);
                }else {
                    sensorDTO.isLeft = false;
                    sensorDTO.isSet = true;
                    currentPosition = -1;
                    notifyDataSetChanged();

                    updateView();
                }
            }
        });

        return convertView;
    }

    public ArrayList<SensorDTO> getSensorDTOs(){
        return sensorDTOs;
    }

    private boolean isLeftSelected(){
        for (int i = 0; i < sensorDTOs.size(); i++){
            if (sensorDTOs.get(i).isSet && sensorDTOs.get(i).isLeft)
                return true;
        }

        return false;
    }

    private boolean isRightSelected(){
        for (int i = 0; i < sensorDTOs.size(); i++){
            if (sensorDTOs.get(i).isSet && !sensorDTOs.get(i).isLeft)
                return true;
        }

        return false;
    }

    public static class ViewHolder {

        @BindView(R.id.addressparentview) LinearLayout addressParentView;
        @BindView(R.id.sensor_address) TextView addressView;
        @BindView(R.id.checkicon) ImageView checkiconView;
        @BindView(R.id.selectsensorparent) RelativeLayout selectsensorParentView;
        @BindView(R.id.left_sensor) LinearLayout selectLeftSensorView;
        @BindView(R.id.right_sensor) LinearLayout selectRightSensorView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class ConflictDialogHolder {
        @BindView(R.id.conflictmessage)  TextView conflictView;
        @BindView(R.id.positive) Button positiveBtn;
        @BindView(R.id.negotive) Button negotiveBtn;

        ConflictDialogHolder(Dialog dialog) {ButterKnife.bind(this, dialog);}
    }

    public void showConflictDialog(final SensorDTO sensorDTO, final int position, final boolean isLeft) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        dialog.setCancelable(false);

        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_conflictsensor);

        final ConflictDialogHolder dialogHolder = new ConflictDialogHolder(dialog);

        String message;

        if (isLeft)
            message = String.format(mContext.getResources().getString(R.string.conflict_message), mContext.getResources().getString(R.string.leftarm));
        else
            message = String.format(mContext.getResources().getString(R.string.conflict_message), mContext.getResources().getString(R.string.rightarm));

        dialogHolder.conflictView.setText(message);

        dialogHolder.positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i <  sensorDTOs.size(); i++){
                    if (i != position){
                        if (sensorDTOs.get(i).isSet && (sensorDTOs.get(i).isLeft == isLeft)){
                            sensorDTOs.get(i).isSet = false;
                        }
                    }
                }

                sensorDTO.isSet = true;
                sensorDTO.isLeft = isLeft;
                currentPosition = -1;

                dialog.dismiss();

                updateView();

                notifyDataSetChanged();
            }
        });

        dialogHolder.negotiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateView(){

    }

}

