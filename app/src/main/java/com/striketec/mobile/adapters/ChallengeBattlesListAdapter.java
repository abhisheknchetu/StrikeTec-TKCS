package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.BattleResult_Temp;
import com.striketec.mobile.util.AppConst;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class ChallengeBattlesListAdapter extends RecyclerView.Adapter<ChallengeBattlesListAdapter.ViewHolder> {

    private int itemWidth;
    private ArrayList<BattleResult_Temp> items;
    Context mContext;
    private String type;

    View.OnClickListener mClickListner;

    public ChallengeBattlesListAdapter(ArrayList<BattleResult_Temp> items, String type){
        this.items = items;
        this.type = type;
    }

    public void setData (ArrayList<BattleResult_Temp> items){
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_challenge_battle_list, parent, false);

        if (mContext == null)
            mContext = parent.getContext();

        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListner.onClick(view);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get owner user id from user
        BattleResult_Temp result_temp = items.get(position);
        holder.usernameView.setText(result_temp.oppoenentUserName);
        holder.scoreView.setText(result_temp.points + " PTS");

        if (type.equalsIgnoreCase(AppConst.TYPE_RECEIVED_BATTLES))
            holder.newView.setVisibility(View.VISIBLE);
        else
            holder.newView.setVisibility(View.INVISIBLE);
    }

    public void setClickListener(View.OnClickListener clickListener){
        this.mClickListner = clickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.profile) ImageView userprofileView;
        @BindView(R.id.username)    TextView usernameView;
        @BindView(R.id.score) TextView scoreView;
        @BindView(R.id.newview) TextView newView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

