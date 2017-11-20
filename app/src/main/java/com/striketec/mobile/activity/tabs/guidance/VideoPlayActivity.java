package com.striketec.mobile.activity.tabs.guidance;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.interfaces.VideoCallback;
import com.striketec.mobile.mediacontroller.MediaPlayerControlImpl;
import com.striketec.mobile.mediacontroller.PlayerControlView;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Response;

public class VideoPlayActivity extends BaseActivity implements TextureView.SurfaceTextureListener,
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, MediaPlayer.OnCompletionListener
{
    @BindView(R.id.title)  TextView titleView;
    @BindView(R.id.parent) RelativeLayout parentView;
    @BindView(R.id.thumb)  ImageView thumbView;
    @BindView(R.id.videoview) TextureView videoView;
    @BindView(R.id.fast_prev_parent) RelativeLayout fastPrevParentView;
    @BindView(R.id.fast_prev) ImageView fastPrevImageView;
    @BindView(R.id.fast_prev_text) TextView fastPrevTextView;
    @BindView(R.id.fast_next_parent) RelativeLayout fastNextParentView;
    @BindView(R.id.fast_next) ImageView fastNextImageView;
    @BindView(R.id.fast_next_text) TextView fastNextTextView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    AnimationDrawable fastRewindAnim, fastForwardAnim;

    private PlayerControlView mediaController;
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private GestureDetectorCompat mDetector;

    int videoPosition = 0;
    ArrayList<VideoDto> videoList = new ArrayList<>();
    VideoCallback videoCallback;

    Handler handler = new Handler();
    boolean inAnimation = false;

    MyCountDownTimer timer;
    Timer forwardanimationTimer;
    Timer rewindanimationTimer;
    TimerTask forwardTimerTask, rewindTimerTask;

    int forwardClickCount = 0;
    int rewindClickCount = 0;
    boolean isPortrait = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_videoplay);

        videoPosition = getIntent().getIntExtra(AppConst.VIDEO_POSITION, 0);
        videoList = SharedUtils.getVideoLists();

        ButterKnife.bind(this);
        videoCallback = MainActivity.getInstance();
        initViews();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            setSize(false);
        else
            setSize(true);
    }

    private void initViews(){
        progressBar.setVisibility(View.INVISIBLE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaController = new PlayerControlView(this);
        mediaController.attach(parentView);
        mediaController.setPlayer(new MediaPlayerControlImpl(mediaPlayer));

        videoView.setSurfaceTextureListener(this);

        if (mDetector == null){
            mDetector = new GestureDetectorCompat(this, this);
            mDetector.setOnDoubleTapListener(this);
        }

        mediaController.setOnVisibilityChangedListener(new PlayerControlView.OnVisibilityChangedListener() {
            @Override
            public void onShown(PlayerControlView view) {
                titleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHidden(PlayerControlView view) {
                if (!isPortrait)
                    titleView.setVisibility(View.INVISIBLE);
                else
                    titleView.setVisibility(View.VISIBLE);
            }
        });

        mediaController.setPrevListener(skipPrevListener);
        mediaController.setNextListener(skipNextListener);

        if (fastForwardAnim == null) {
            fastForwardAnim = (AnimationDrawable) fastNextImageView.getDrawable();
        }

        if (fastRewindAnim == null){
            fastRewindAnim = (AnimationDrawable) fastPrevImageView.getDrawable();
        }
    }

    private void playVideo(){

//        startLoading();
        progressBar.setVisibility(View.VISIBLE);
        if (mediaController.isShowing())
            titleView.setVisibility(View.VISIBLE);
        else {
            if (!isPortrait)
                titleView.setVisibility(View.INVISIBLE);
            else
                titleView.setVisibility(View.VISIBLE);
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaController.setPlayer(new MediaPlayerControlImpl(mediaPlayer));
            mediaPlayer.setLooping(true);
        }

        if (mediaPlayer != null){
            mediaPlayer.setSurface(null);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }

            mediaPlayer.reset();
        }

        fastNextImageView.setVisibility(View.INVISIBLE);
        fastNextTextView.setVisibility(View.INVISIBLE);
        fastPrevImageView.setVisibility(View.INVISIBLE);
        fastPrevTextView.setVisibility(View.INVISIBLE);


        final VideoDto currentVideo = videoList.get(videoPosition);
        videoView.setVisibility(View.INVISIBLE);
        thumbView.setVisibility(View.VISIBLE);

        if (videoPosition == 0 && videoList.size() == 1){
            mediaController.updateSkipNextButton(false);
            mediaController.updateSkipPrevButton(false);
        }else if (videoPosition == 0){
            mediaController.updateSkipPrevButton(false);
            mediaController.updateSkipNextButton(true);
        }else if (videoPosition == videoList.size() - 1){
            mediaController.updateSkipNextButton(false);
            mediaController.updateSkipPrevButton(true);
        }else {
            mediaController.updateSkipNextButton(true);
            mediaController.updateSkipPrevButton(true);
        }

        if (!TextUtils.isEmpty(currentVideo.mTitle))
            titleView.setText(currentVideo.mTitle);
        else
            titleView.setText("Video");

        if (!TextUtils.isEmpty(currentVideo.mThumnail)){
            Glide.with(this).load(currentVideo.mThumnail).into(thumbView);
        }

        try {
            mediaPlayer.setDataSource(currentVideo.mVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (surface != null)
            mediaPlayer.setSurface(surface);

        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                thumbView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                parentView.setOnTouchListener(fastPrevTounchListener);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
//                endLoading();
                progressBar.setVisibility(View.INVISIBLE);
                increaseViewCount(currentVideo);
                mediaController.show();
            }
        });
    }

    private void increaseViewCount(final VideoDto videoDto){

        if (videoDto == null)
            return;

        if (CommonUtils.isOnline()){
            RetrofitSingleton.VIDEO_REST.increaseViewCount(SharedUtils.getHeader(), videoDto.mId).enqueue(new IndicatorCallback<DefaultResponseDto>(this, false) {
                @Override
                public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        DefaultResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            videoDto.mViewCounts++;
                            videoCallback.viewCountChanged(videoDto);
                        }else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(VideoPlayActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (fastRewindAnim != null) {
            fastRewindAnim.stop();
            fastRewindAnim = null;
        }

        if (fastForwardAnim != null){
            fastForwardAnim.stop();
            fastForwardAnim = null;
        }

        timerStop();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        surface = new Surface(surfaceTexture);
        playVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setSize(false);
            showhideStatusbar(false);
        }else {
            titleView.setVisibility(View.VISIBLE);
            showhideStatusbar(true);
            timerStop();
            setSize(true);
        }
        super.onConfigurationChanged(newConfig);
    }

    private void showhideStatusbar(boolean isshow){
        if (isshow){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void animation(){

        int visibleState = titleView.getVisibility();

        if (!inAnimation){
            if (visibleState == View.INVISIBLE){
                titleView.setVisibility(View.VISIBLE);
                Animator showAnimator = ObjectAnimator.ofFloat(titleView, "translationY", -titleView.getHeight(), 0f).setDuration(AppConst.ANIMATION_DURATION);
                showAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        inAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        inAnimation = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                showAnimator.start();

                timerStart();
                //hide title after 4 sec


            }else {
                Animator hideAnimator = ObjectAnimator.ofFloat(titleView, "translationY", 0f, -titleView.getHeight()).setDuration(AppConst.ANIMATION_DURATION);
                hideAnimator.start();

                hideAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        inAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        titleView.setVisibility(View.INVISIBLE);
                        inAnimation = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                timerStop();
            }
        }
    }

    public void timerStart(){
        timer = new MyCountDownTimer(4000, 1000);
        timer.start();
    }

    public void timerStop(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private View.OnClickListener skipPrevListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            videoPosition--;
            parentView.setOnTouchListener(null);

            playVideo();
        }
    };

    private View.OnClickListener skipNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            videoPosition++;

            parentView.setOnTouchListener(null);

            playVideo();
        }
    };

    private void startForwardTimer(){
        forwardanimationTimer = new Timer();
        initializeForwardTimerTask();
        forwardanimationTimer.schedule(forwardTimerTask, 0, 150);

    }

    private void stopForwardTimer(){
        if (forwardanimationTimer != null){
            forwardanimationTimer.cancel();
            forwardanimationTimer = null;
        }

        forwardcount = 0;
    }

    int forwardcount = 0;
    private void initializeForwardTimerTask(){
        forwardTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        forwardcount++;
                        if (forwardcount == 6){
                            fastNextImageView.setVisibility(View.INVISIBLE);
                            fastNextTextView.setVisibility(View.INVISIBLE);
                            forwardClickCount = 0;
                            stopForwardTimer();
                        }
                    }
                });
            }
        };
    }

    private void startRewindTimer(){
        rewindanimationTimer = new Timer();
        initializeRewindTimerTask();
        rewindanimationTimer.schedule(rewindTimerTask, 0, 150);
    }

    private void stopRewindTimer(){
        if (rewindanimationTimer != null){
            rewindanimationTimer.cancel();
            rewindanimationTimer = null;
        }

        rewindcount = 0;
    }

    int rewindcount = 0;
    private void initializeRewindTimerTask(){
        rewindTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        rewindcount++;
                        if (rewindcount == 6){
                            fastPrevImageView.setVisibility(View.INVISIBLE);
                            fastPrevTextView.setVisibility(View.INVISIBLE);
                            rewindClickCount = 0;
                            stopRewindTimer();
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (videoPosition < videoList.size() - 1) {
            videoPosition++;
            playVideo();
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        int width = getResources().getDisplayMetrics().widthPixels;

        if (forwardcount < 6 && forwardcount > 0){
            if ((int)e.getX() > width / 2){
                fastForwardAction();
            }
        }

        if (rewindcount < 6 && rewindcount > 0){
            if ((int)e.getX() < width / 2){
                fastRewindAction();
            }
        }

        if (rewindcount == 0 && forwardcount == 0){
            if (mediaController.isShowing())
                mediaController.hide();
            else
                mediaController.show();
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        int width = getResources().getDisplayMetrics().widthPixels;
        if ((int)e.getX() < width / 2){
            fastRewindAction();
        }else {
            fastForwardAction();
        }

        return true;
    }

    private void fastForwardAction(){
        if (mediaController.isShowing())
            mediaController.hide();

        fastNextImageView.setVisibility(View.VISIBLE);
        fastNextTextView.setVisibility(View.VISIBLE);
        fastForwardAnim.stop();
        fastForwardAnim.start();
        stopForwardTimer();
        forwardClickCount++;
        fastNextTextView.setText(String.format(getResources().getString(R.string.fast_text), forwardClickCount * 10));
        fastForwardAnim.setOneShot(true);
        mediaController.fastForwardAction();
        startForwardTimer();
    }

    private void fastRewindAction(){

        if (mediaController.isShowing())
            mediaController.hide();

        fastPrevImageView.setVisibility(View.VISIBLE);
        fastPrevTextView.setVisibility(View.VISIBLE);
        fastRewindAnim.stop();
        fastRewindAnim.start();
        stopRewindTimer();
        rewindClickCount++;
        fastPrevTextView.setText(String.format(getResources().getString(R.string.fast_text), rewindClickCount * 10));
        fastRewindAnim.setOneShot(true);
        mediaController.fastRewindAction();
        startRewindTimer();
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            int visibleState = titleView.getVisibility();
            if ((visibleState == View.VISIBLE) && !inAnimation) {
                animation();
            }
        }
    }

    private void setSize(boolean isPortrait){
        int width, height;

        if (isPortrait){
            width = getResources().getDisplayMetrics().widthPixels;
            height = width * 9 / 16;
        }else {
            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        this.isPortrait = isPortrait;
        parentView.setLayoutParams(layoutParams);
    }

    private View.OnTouchListener fastPrevTounchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mDetector.onTouchEvent(event);
        }
    };
}
