package com.striketec.mobile.mediacontroller;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.striketec.mobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerControlView extends FrameLayout {

    public static final int DEFAULT_FAST_REWIND_MS = 10_000;
    public static final int DEFAULT_FAST_FORWARD_MS = 10_000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 4_000;

    private final ViewHolder viewHolder;

    boolean alwaysShow;

    private MediaController.MediaPlayerControl player;
    private boolean showing;
    private boolean dragging;
    private int fastRewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private boolean skipPrevEnable;
    private boolean skipNextEnable;

//    private Drawable pauseDrawable, playDrawable;

    private OnClickListener nextListener, prevListener;
    private OnVisibilityChangedListener onVisibilityChangedListener;

    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int pos = updateProgress();
            if (!dragging && showing && player != null && player.isPlaying()) {
                postDelayed(updateProgressRunnable, 1000 - (pos % 1000));
            }
        }
    };
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.custom_media_controller, this);
        viewHolder = new ViewHolder(this);
        fastRewindMs = DEFAULT_FAST_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerControlView, 0, 0);
            fastRewindMs = a.getInt(R.styleable.PlayerControlView_pcv_fast_rewind_ms, DEFAULT_FAST_REWIND_MS);
            fastForwardMs = a.getInt(R.styleable.PlayerControlView_pcv_fast_forward_ms, DEFAULT_FAST_FORWARD_MS);
            showTimeoutMs = a.getInt(R.styleable.PlayerControlView_pcv_show_timeout_ms, DEFAULT_SHOW_TIMEOUT_MS);
            alwaysShow = a.getBoolean(R.styleable.PlayerControlView_pcv_always_show, false);
            a.recycle();
        }

        if (isInEditMode()) {
            return;
        }
        ComponentListener componentListener = new ComponentListener();
        viewHolder.pausePlayButton.setOnClickListener(componentListener);
        viewHolder.fastForwardButton.setOnClickListener(componentListener);
        viewHolder.fastRewindButton.setOnClickListener(componentListener);
        viewHolder.skipPrevButton.setOnClickListener(componentListener);
        viewHolder.skipNextButton.setOnClickListener(componentListener);
        viewHolder.seekBar.setOnSeekBarChangeListener(componentListener);
        viewHolder.seekBar.setMax(1000);

//        pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.video_pause_new);
//        playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.video_play_new);

//        viewHolder.pausePlayButton.setImageDrawable(toStateListDrawable(viewHolder.pausePlayButton.getDrawable()));
//        viewHolder.fastForwardButton.setImageDrawable(toStateListDrawable(viewHolder.fastForwardButton.getDrawable()));
//        viewHolder.fastRewindButton.setImageDrawable(toStateListDrawable(viewHolder.fastRewindButton.getDrawable()));
//        viewHolder.skipNextButton.setImageDrawable(toStateListDrawable(viewHolder.skipNextButton.getDrawable()));
//        viewHolder.skipPrevButton.setImageDrawable(toStateListDrawable(viewHolder.skipPrevButton.getDrawable()));

        viewHolder.fastForwardButton.setVisibility(View.GONE);
        viewHolder.fastRewindButton.setVisibility(View.GONE);

        hide();
    }

    protected Drawable toStateListDrawable(Drawable drawable) {
        int drawableColor = ContextCompat.getColor(getContext(), android.R.color.white);
        return Util.createStateListDrawable(drawable, drawableColor);
    }

    public void setPlayer(MediaController.MediaPlayerControl player) {
        this.player = player;
        updatePausePlayImage();
    }

    public void attach(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        attach(rootView);
    }

    public void attach(ViewGroup rootView) {
        rootView.removeView(this);
        if (rootView instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(this, layoutParams);
        } else {
            LayoutParams layoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );
            rootView.addView(this, layoutParams);
        }
    }

    public void show() {
        show(showTimeoutMs);
    }

    void show(int showTimeoutMs) {
        showing = true;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onShown(this);
        }
        setVisibility(VISIBLE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        requestFocus();

        updateProgress();
        viewHolder.pausePlayButton.requestFocus();
        disableUnsupportedButtons();
        updatePausePlayImage();

        removeCallbacks(updateProgressRunnable);
        post(updateProgressRunnable);

        removeCallbacks(hideRunnable);
        if (!alwaysShow) {
            postDelayed(hideRunnable, showTimeoutMs);
        }
    }


    public boolean isShowing() {
        return showing;
    }

    public void hide() {
        showing = false;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onHidden(this);
        }
        removeCallbacks(hideRunnable);
        removeCallbacks(updateProgressRunnable);
        setVisibility(GONE);
    }

    public void toggleVisibility() {
        if (showing) {
            hide();
        } else {
            show();
        }
    }

    private int updateProgress() {
        if (dragging || player == null) {
            return 0;
        }
        updatePausePlayImage();
        int currentTime = player.getCurrentPosition();
        int totalTime = player.getDuration();
        if (totalTime > 0) {
            long position = 1000L * currentTime / totalTime;
            viewHolder.seekBar.setProgress((int) position);
        }
        int percent = player.getBufferPercentage();
        viewHolder.seekBar.setSecondaryProgress(percent * 10);

        viewHolder.currentTimeText.setText(Util.formatTime(currentTime));
        viewHolder.totalTimeText.setText(Util.formatTime(totalTime));
        return currentTime;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null) {
            return super.dispatchKeyEvent(event);
        }
        final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_SPACE:
                if (uniqueDown) {
                    doPauseResume();
                    show();
                    viewHolder.pausePlayButton.requestFocus();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (uniqueDown && !player.isPlaying()) {
                    player.start();
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (uniqueDown && player.isPlaying()) {
                    player.pause();
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_MENU:
                return super.dispatchKeyEvent(event);
            case KeyEvent.KEYCODE_BACK:
                if (alwaysShow) {
                    return super.dispatchKeyEvent(event);
                }
                if (uniqueDown) {
                    hide();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (player.canSeekForward()) {
                    player.seekTo(fastForwardMs);
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (player.canSeekForward()) {
                    player.seekTo(fastRewindMs);
                    show();
                }
                return true;
            default:
                break;
        }

        show();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(updateProgressRunnable);
        removeCallbacks(hideRunnable);
    }

    private void updatePausePlayImage() {
        if (player == null) {
            return;
        }

        if (player.isPlaying()){
            viewHolder.pausePlayButton.setImageResource(R.drawable.video_pause_new);
        }else {
            viewHolder.pausePlayButton.setImageResource(R.drawable.video_play_new);
        }
//        viewHolder.pausePlayButton.toggleImage(player.isPlaying());
    }

    public void updateSkipPrevButton (boolean enable){
        skipPrevEnable = enable;

        if (enable){
            viewHolder.skipPrevButton.setImageResource(R.drawable.video_prev_active);
            viewHolder.skipPrevButton.setEnabled(true);
        }else {
            viewHolder.skipPrevButton.setImageResource(R.drawable.video_prev_inactive);
            viewHolder.skipPrevButton.setEnabled(false);
        }
    }

    public void updateSkipNextButton(boolean enable){
        skipNextEnable = enable;

        if (enable){
            viewHolder.skipNextButton.setImageResource(R.drawable.video_next_active);
            viewHolder.skipNextButton.setEnabled(true);
        }else {
            viewHolder.skipNextButton.setImageResource(R.drawable.video_next_inactive);
            viewHolder.skipNextButton.setEnabled(false);
        }
    }

    private void doPauseResume() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }

        updatePausePlayImage();
    }

    @Override
    public void setEnabled(boolean enabled) {
        viewHolder.pausePlayButton.setEnabled(enabled);
        viewHolder.fastForwardButton.setEnabled(enabled);
        viewHolder.fastRewindButton.setEnabled(enabled);
        viewHolder.skipNextButton.setEnabled(enabled && nextListener != null);
        viewHolder.skipPrevButton.setEnabled(enabled && prevListener != null);
        viewHolder.seekBar.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    private void disableUnsupportedButtons() {
        if (player == null) {
            return;
        }
        if (!player.canPause()) {
            viewHolder.pausePlayButton.setEnabled(false);
        }
        if (!player.canSeekBackward()) {
            viewHolder.fastRewindButton.setEnabled(false);
        }
        if (!player.canSeekForward()) {
            viewHolder.fastForwardButton.setEnabled(false);
        }
        if (!player.canSeekBackward() && !player.canSeekForward()) {
            viewHolder.seekBar.setEnabled(false);
        }
    }

    public void setPrevListener(@Nullable OnClickListener prevListener) {
        this.prevListener = prevListener;
        viewHolder.skipPrevButton.setVisibility(prevListener == null ? View.INVISIBLE : View.VISIBLE);
    }

    public void setNextListener(@Nullable OnClickListener nextListener) {
        this.nextListener = nextListener;
        MediaController a;
        viewHolder.skipNextButton.setVisibility(nextListener == null ? View.INVISIBLE : View.VISIBLE);
    }

    public void setFastRewindMs(int fastRewindMs) {
        this.fastRewindMs = fastRewindMs;
    }

    public void setFastForwardMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
    }

    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
    }

    public void setAlwaysShow(boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
        if (alwaysShow) {
            removeCallbacks(hideRunnable);
        }
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener listener) {
        this.onVisibilityChangedListener = listener;
    }

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void fastForwardAction(){
        Log.e("Super", "fast forward = " + player.getCurrentPosition() / 1000);
        viewHolder.fastForwardButton.performClick();
    }

    public void fastRewindAction(){
        Log.e("Super", "back forward = " + player.getCurrentPosition() / 1000);
        viewHolder.fastRewindButton.performClick();
    }

    public MediaController getMediaControllerWrapper() {
        return new MediaControllerWrapper(this);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return PlayerControlView.class.getName();
    }

    public interface OnVisibilityChangedListener {
        void onShown(PlayerControlView view);

        void onHidden(PlayerControlView view);
    }

    public static final class ViewHolder {

        @BindView(R.id.control_background) RelativeLayout controlsBackground;
        @BindView(R.id.seek_bar) SeekBar seekBar;
        @BindView(R.id.total_time_text) TextView totalTimeText;
        @BindView(R.id.current_time_text) TextView currentTimeText;
        @BindView(R.id.pause_play) ImageView pausePlayButton;
        @BindView(R.id.fast_forward) ImageView fastForwardButton;
        @BindView(R.id.fast_rewind) ImageView fastRewindButton;
        @BindView(R.id.skip_next) ImageView skipNextButton;
        @BindView(R.id.skip_previous) ImageView skipPrevButton;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    private final class ComponentListener implements SeekBar.OnSeekBarChangeListener, OnClickListener {

        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }
            if (v == viewHolder.pausePlayButton) {
                doPauseResume();
            } else if (v == viewHolder.fastRewindButton) {
                int position = player.getCurrentPosition();
                position -= fastRewindMs;
                player.seekTo(position);
                updateProgress();
            } else if (v == viewHolder.fastForwardButton) {
                int position = player.getCurrentPosition();
                position += fastForwardMs;
                player.seekTo(position);
                updateProgress();
            } else if (v == viewHolder.skipPrevButton) {
                if (prevListener != null && skipPrevEnable) {
                    removeCallbacks(updateProgressRunnable);
                    removeCallbacks(hideRunnable);
                    hide();
                    prevListener.onClick(v);
                }
            } else if (v == viewHolder.skipNextButton) {
                if (nextListener != null && skipNextEnable) {
                    removeCallbacks(updateProgressRunnable);
                    removeCallbacks(hideRunnable);
                    hide();
                    nextListener.onClick(v);
                }
            }
            //show();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }
            long duration = player.getDuration();
            long newPosition = (duration * progress) / 1000L;
            player.seekTo((int) newPosition);
            viewHolder.currentTimeText.setText(Util.formatTime((int) newPosition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            show();
            dragging = true;
            removeCallbacks(hideRunnable);
            removeCallbacks(updateProgressRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;
            show();
            post(updateProgressRunnable);
        }
    }
}