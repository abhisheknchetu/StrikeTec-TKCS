package com.striketec.mobile.mediacontroller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.striketec.mobile.R;

public class PausePlayButton extends AppCompatImageView {

    private Drawable playDrawable;
    private Drawable pauseDrawable;

    public PausePlayButton(Context context) {
        this(context, null);
    }

    public PausePlayButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public PausePlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            setImageResource(R.drawable.video_play_new);
            return;
        }
        pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.video_pause_new);
        playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.video_play_new);
        toggleImage(false);
    }

    public Drawable getPlayDrawable() {
        return playDrawable;
    }

    public void setPlayDrawable(Drawable playDrawable) {
        this.playDrawable = playDrawable;
    }

    public Drawable getPauseDrawable() {
        return pauseDrawable;
    }

    public void setPauseDrawable(Drawable pauseDrawable) {
        this.pauseDrawable = pauseDrawable;
    }

    public void toggleImage(boolean isPlaying) {
        if (isPlaying) {
            setImageResource(R.drawable.video_pause_new);
        } else {
            setImageResource(R.drawable.video_play_new);
        }
    }
}
