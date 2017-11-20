package com.striketec.mobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.RecordingTypeDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * user : rakeshk2
 * date : 11/4/2017
 * description : Adapter class to show trainings type list for record voices.
 **/
public class RecordingsListAdapter extends RecyclerView.Adapter<RecordingsListAdapter.ViewHolder> implements View.OnClickListener {

    private MediaRecorder mediaRecorder;
    private int row_index = -1;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<RecordingTypeDto> items;
    //    private OnItemClickRequest onItemClickRequest;
    private String selectedAudioPath = null;
    private MediaPlayer mediaPlayer;
    private String selectedRecordingType = null;
    private String fileName = null;
    private boolean isRecordingProgress = false;

    public RecordingsListAdapter(Context context, ArrayList<RecordingTypeDto> items) {
        this.items = items;
        mContext = context;
        mActivity = (Activity) context;
//        onItemClickRequest = (OnItemClickRequest) context;
    }

    public void setData(ArrayList<RecordingTypeDto> items) {
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecordingsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordingsListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_training_set, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            final RecordingTypeDto recordingList = items.get(position);

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            holder.delete_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = holder.getLayoutPosition();
                    selectedAudioPath = CommonUtils.searchFile(recordingList.getName());
                    deleteThisFile(selectedAudioPath);

                }
            });
            holder.upload_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = holder.getLayoutPosition();
                    if (recordingList.isFileExists) {
                        CommonUtils.showToastMessage(mContext.getString(R.string.uploading));
                    }
                }
            });
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = holder.getLayoutPosition();

                    if (holder.play.getDrawable().getConstantState() == mContext.getResources().getDrawable(R.drawable.tab_homeselected).getConstantState()) {
                        if (recordingList.isRecording) {
                            stopRecording();
                        }
                    } else {
                        if (recordingList.isFileExists) {
                            selectedAudioPath = CommonUtils.searchFile(recordingList.getName());
                            stopPlayingAudio();
                            playAudio();
//                        onItemClickRequest.btnClickSeeAllResponse("Play", recordingList.getName());
                        } else {
                            selectedRecordingType = recordingList.getName();
                            fileName = recordingList.getName();
                            startRecording();
                        }
                    }
                }
            });
            /*if (row_index == position) {
                holder.container.setBackgroundResource(R.drawable.bg_fill_rectangle_punches);
            } else {
                holder.container.setBackgroundResource(R.drawable.bg_rectangle_default);
            }*/
            //disabled other recording buttons while recording is in progress
            if (isRecordingProgress) {
                holder.play.setEnabled(false);
            } else {
                holder.play.setEnabled(true);
            }

            if (recordingList.isRecording) {
                holder.play.setImageResource(R.drawable.tab_homeselected);
                holder.play.setEnabled(true);
            } else {

                if (recordingList.isFileExists) {
                    holder.play.setImageResource(R.drawable.video_play);
                    holder.delete_audio.setVisibility(View.VISIBLE);
                    holder.upload_audio.setVisibility(View.VISIBLE);
                } else {

                    holder.play.setImageResource(R.drawable.icon_plus);
                    holder.delete_audio.setVisibility(View.GONE);
                    holder.upload_audio.setVisibility(View.GONE);
                }
            }
            holder.type.setText(String.valueOf(recordingList.name));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnClick({R.id.upload_audio, R.id.delete_audio, R.id.play, R.id.container})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.play:
                    break;
                case R.id.delete_audio:
                    break;

                case R.id.container:
                    break;
            }
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
        return items.size();
    }

    /**
     * user : rakeshk2
     * date : 11/7/2017
     * description : Start Recording.
     **/
    public void startRecording() {
        if (checkPermission()) {

            if (selectedRecordingType != null) {

                String path = CommonUtils.createFile(fileName);
                selectedAudioPath = path + File.separator +
                        CommonUtils.createRandomAudioFileName(fileName);

                MediaRecorderReady();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                    isRecordingProgress = true;

                    items.get(row_index).setRecording(true);
                    notifyDataSetChanged();

                    CommonUtils.showToastMessage(mContext.getString(R.string.recording_started));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CommonUtils.showToastMessage(mContext.getString(R.string.select_audio_type));
            }
        } else {
            requestPermission();
        }
    }

    /**
     * Prepare Media recorder.
     */
    private void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(selectedAudioPath);
    }

    /**
     * Check Permissions
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(mActivity, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, AppConst.RequestPermissionCode);
    }

    private void stopRecording() {
        try {
//            mStartRecording.setImageResource(R.drawable.video_play_new);
            if (mediaRecorder != null) {
                mediaRecorder.stop();
            }
            fileName = null;
            Toast.makeText(mContext, R.string.recording_completed,
                    Toast.LENGTH_LONG).show();

            selectedRecordingType = null;

            isRecordingProgress = false;
            items.get(row_index).setRecording(false);
            items.get(row_index).setFileExists(true);
            notifyDataSetChanged();
//            bindList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * user : rakeshk2
     * date : 11/2/2017
     * description : Play Audio file.
     **/
    private void playAudio() {
        try {
            if (selectedAudioPath != null) {

                mediaPlayer = new MediaPlayer();

                mediaPlayer.setDataSource(selectedAudioPath);
                mediaPlayer.prepare();

                mediaPlayer.start();
                Toast.makeText(mContext, R.string.playing,
                        Toast.LENGTH_LONG).show();

            } else {
                CommonUtils.showToastMessage(mContext.getString(R.string.file_not_found));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteThisFile(String selectedPath) {

        if (selectedPath != null) {
            File file = new File(selectedPath);
            if (file.delete()) {
                CommonUtils.showToastMessage(mContext.getString(R.string.file_deleted));
                selectedAudioPath = null;

                items.get(row_index).setFileExists(false);
                notifyDataSetChanged();

            } else {
                CommonUtils.showToastMessage(mContext.getString(R.string.error_delete));
            }
        }
    }

    public void stopPlayingAudio() {
//        mPlayRecording.setImageResource(R.drawable.icon_play);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Interface to be implemented by calling activity
    public interface OnItemClickRequest {
        void btnClickSeeAllResponse(String strButtonType, String strButtonName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.delete_audio)
        ImageView delete_audio;
        @BindView(R.id.upload_audio)
        ImageView upload_audio;
        @BindView(R.id.play)
        ImageView play;
        @BindView(R.id.container)
        LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

