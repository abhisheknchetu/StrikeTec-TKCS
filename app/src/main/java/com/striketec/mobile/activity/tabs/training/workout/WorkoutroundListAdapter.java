package com.striketec.mobile.activity.tabs.training.workout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.training.comboset.CombinationListAdapter;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.WorkoutRoundDto;
import com.striketec.mobile.util.ComboSetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutroundListAdapter extends RecyclerView.Adapter<WorkoutroundListAdapter.ViewHolder> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<WorkoutRoundDto> roundDTOs;
    WorkoutPlanActivity workoutActivity;

    public WorkoutroundListAdapter(Context context, ArrayList<WorkoutRoundDto> roundDTOs){
        mContext = context;
        workoutActivity = (WorkoutPlanActivity)context;
        this.roundDTOs = roundDTOs;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<WorkoutRoundDto> roundDTOs){
        this.roundDTOs = roundDTOs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_roundworkout_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WorkoutRoundDto roundDTO = roundDTOs.get(position);

        holder.roundNameView.setText(roundDTO.getName());

        ArrayList<ComboDto> comboDtos = new ArrayList<>();

        for (int i = 0; i < roundDTO.getComboIDLists().size(); i++){
            comboDtos.add(ComboSetUtil.getComboDtowithID(roundDTO.getComboIDLists().get(i)));
        }

        CombinationListAdapter listAdapter = new CombinationListAdapter(mContext, comboDtos);
        listAdapter.setCurrentPosition(-1);

        holder.comboListView.setAdapter(listAdapter);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return roundDTOs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        Button roundNameView;

        @BindView(R.id.listview)
        ListView comboListView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

