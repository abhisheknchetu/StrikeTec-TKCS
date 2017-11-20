package com.striketec.mobile.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.SubscriptionDtoNew;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * user : rakeshk2
 * date : 10/25/2017
 * description : Adapter class to show subscription list
 **/
public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {

    private int mRowIndex = -1;
    private Context mContext;
    private ArrayList<SubscriptionDtoNew> mItems;
    private OnSubscriptionClickRequest mOnSubscriptionClickRequest;

    public SubscriptionsListAdapter(Context context, ArrayList<SubscriptionDtoNew> items) {
        this.mItems = items;
        mContext = context;
        mOnSubscriptionClickRequest = (OnSubscriptionClickRequest) context;
    }

    public void setData(ArrayList<SubscriptionDtoNew> items) {
        this.mItems = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SubscriptionsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionsListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_subscription, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            final SubscriptionDtoNew subscriptionList = mItems.get(position);

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRowIndex = holder.getLayoutPosition();
                    notifyDataSetChanged();
                    mOnSubscriptionClickRequest.btnClickSeeAllResponse(subscriptionList.id, subscriptionList.getSKU());

                    /*Bundle bundle = new Bundle();
                    bundle.putParcelable(EVBundleConstants.KEY_SUBSCRIPTION_MODEL, SubscriptionDtoNew);
                    mActivity.startNewActivity(PurchaseActivity.class, bundle, -1);*/
                }
            });

            if (mRowIndex == position) {
                holder.container.setBackgroundResource(R.drawable.bg_fill_rectangle_punches);
            } else {
                holder.container.setBackgroundResource(R.drawable.bg_rectangle_default);
                holder.container.setBackground(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));
//                holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));//
            }

            holder.tutorial_number.setText(subscriptionList.tutorials);
            holder.tutorial_value.setText(subscriptionList.tutorial_details);
            holder.tournament_number.setText(subscriptionList.tournaments);
            holder.tournament_value.setText(subscriptionList.tournament_details);
            holder.battle_number.setText(subscriptionList.battles);
            holder.battle_value.setText(subscriptionList.battle_details);
            holder.plan_type.setText(String.valueOf(subscriptionList.name));
            holder.plan_value.setText(String.valueOf(subscriptionList.price));
            holder.plan_duration.setText(subscriptionList.duration);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Interface to be implemented by calling activity
    public interface OnSubscriptionClickRequest {
        void btnClickSeeAllResponse(int strButtonId, String strButtonName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tutorial_number)
        TextView tutorial_number;
        @BindView(R.id.tutorial_value)
        TextView tutorial_value;
        @BindView(R.id.tournament_number)
        TextView tournament_number;
        @BindView(R.id.tournament_value)
        TextView tournament_value;
        @BindView(R.id.battle_number)
        TextView battle_number;
        @BindView(R.id.battle_value)
        TextView battle_value;
        @BindView(R.id.plan_type)
        TextView plan_type;
        @BindView(R.id.plan_value)
        TextView plan_value;
        @BindView(R.id.plan_duration)
        TextView plan_duration;
        @BindView(R.id.container)
        LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

