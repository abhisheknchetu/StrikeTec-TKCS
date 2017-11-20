package com.striketec.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.tabs.guidance.VideoPlayActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.interfaces.VideoCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class VideoChildListAdapter extends ArrayAdapter<VideoDto>{

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<VideoDto> videoDtos;
    private MainActivity mainActivity;
    private boolean isSearch;

    public VideoChildListAdapter(Context context, ArrayList<VideoDto> videoDtos, boolean isSearch){
        super(context, 0, videoDtos);

        mContext = context;
        mainActivity = (MainActivity)mContext;
        this.videoDtos = videoDtos;
        this.isSearch = isSearch;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<VideoDto> videoDtos){
        this.videoDtos = videoDtos;
    }

    @Override
    public int getCount() {
        return videoDtos.size();
    }

    @Nullable
    @Override
    public VideoDto getItem(int position) {
        return videoDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_child_video_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final VideoDto videoDto = getItem(position);

        if (!TextUtils.isEmpty(videoDto.mThumnail)){
            Glide.with(mContext).load(videoDto.mThumnail).into(viewHolder.thumbView);
        }

        viewHolder.titleView.setText(videoDto.mTitle);
        viewHolder.authorView.setText(videoDto.mAuthorName);
        viewHolder.durationView.setText(videoDto.mDuration);

        if (videoDto.mFavorited){
            viewHolder.favoriteView.setImageResource(R.drawable.unfavourite);
        }else
            viewHolder.favoriteView.setImageResource(R.drawable.favourite);

        if (isSearch){
            viewHolder.favoriteView.setVisibility(View.INVISIBLE);
        }else
            viewHolder.favoriteView.setVisibility(View.VISIBLE);

        viewHolder.playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(mainActivity, VideoPlayActivity.class);
                SharedUtils.saveVideoLists(videoDtos);
                playIntent.putExtra(AppConst.VIDEO_POSITION, position);
                mainActivity.startActivity(playIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        viewHolder.favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRequest(videoDto);
            }
        });

        return convertView;
    }

    private void favoriteRequest(final VideoDto videoDto){

        if (CommonUtils.isOnline()){

            Call call = null;

            if (videoDto.mFavorited){
                call = RetrofitSingleton.VIDEO_REST.unfavoriteVideo(SharedUtils.getHeader(), videoDto.mId);
            }else {
                call = RetrofitSingleton.VIDEO_REST.favoriteVideo(SharedUtils.getHeader(), videoDto.mId);
            }

            if (call != null){
                call.enqueue(new IndicatorCallback<DefaultResponseDto>(mContext, false) {
                    @Override
                    public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            DefaultResponseDto responseDto = response.body();

                            if (!responseDto.mError){
                                videoDto.mFavorited = !videoDto.mFavorited;

                                mainActivity.favoriteChanged(videoDto);

                                notifyDataSetChanged();
                            }else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(mContext, t.getLocalizedMessage());
                    }
                });
            }

        }else {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.nointernet));
        }
    }



    public static class ViewHolder {
        @BindView(R.id.thumb)  ImageView thumbView;
        @BindView(R.id.play) ImageView playIcon;
        @BindView(R.id.favorite) ImageView favoriteView;
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.author) TextView authorView;
        @BindView(R.id.duration) TextView durationView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

