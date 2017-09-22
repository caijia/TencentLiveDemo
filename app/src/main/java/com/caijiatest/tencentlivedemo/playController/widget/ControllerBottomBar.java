package com.caijiatest.tencentlivedemo.playController.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.caijiatest.tencentlivedemo.R;
import com.caijiatest.tencentlivedemo.playController.entities.VideoQuality;
import com.caijiatest.tencentlivedemo.playController.util.ControllerUtil;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by cai.jia on 2017/7/6 0006
 */

public class ControllerBottomBar extends LinearLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    private static final String TIME_RESET = "00:00";
    private TextView voiceTv;
    private TextView currentTimeTv;
    private SeekBar progressSeekBar;
    private TextView totalTimeTv;
    private TextView fullScreenTv;
    private ProgressBar progressBar;
    private LinearLayout controllerLl;
    private TextView videoQualityTv;
    private ViewGroup videoControllerContainerParent;
    private ViewGroup videoControllerContainer;
    private int maxVolume;

    public ControllerBottomBar(Context context) {
        this(context, null);
    }

    public ControllerBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ControllerBottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.controller_bottom_bar, this, true);
        setOrientation(VERTICAL);
        voiceTv = (TextView) findViewById(R.id.video_voice_tv);
        currentTimeTv = (TextView) findViewById(R.id.video_current_time_tv);
        progressSeekBar = (SeekBar) findViewById(R.id.video_play_progress_seek_bar);
        totalTimeTv = (TextView) findViewById(R.id.video_total_time_tv);
        fullScreenTv = (TextView) findViewById(R.id.video_full_screen_tv);
        progressBar = (ProgressBar) findViewById(R.id.video_play_progress_bar);
        controllerLl = (LinearLayout) findViewById(R.id.bottom_controller_bar);
        videoQualityTv = (TextView) findViewById(R.id.tv_video_quality);

        voiceTv.setOnClickListener(this);
        videoQualityTv.setOnClickListener(this);
        fullScreenTv.setOnClickListener(this);
        progressSeekBar.setOnSeekBarChangeListener(this);
        maxVolume = ControllerUtil.getMaxVolume(context);
        setCurrentVolume(ControllerUtil.getVolume(context));
        hide();
    }

    public void show() {
        setCurrentVolume(ControllerUtil.getVolume(getContext()));
        controllerLl.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public void hide() {
        controllerLl.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public void reset() {
        currentTimeTv.setText(TIME_RESET);
        totalTimeTv.setText(TIME_RESET);
        progressSeekBar.setProgress(0);
        progressSeekBar.setSecondaryProgress(0);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
    }

    private List<VideoQuality> videoQualityList;

    public void setVideoQuality(List<VideoQuality> videoQualityList) {
        this.videoQualityList = videoQualityList;
        boolean hasVideoQualityData = videoQualityList != null && !videoQualityList.isEmpty();
        videoQualityTv.setVisibility(hasVideoQualityData ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == voiceTv) {
            v.setSelected(!v.isSelected());
            boolean hasVolume = v.isSelected();
            int currentVolume = ControllerUtil.getVolume(getContext());
            currentVolume = currentVolume == 0 ? maxVolume / 3 : currentVolume;
            ControllerUtil.setVolume(getContext(), hasVolume ? currentVolume : 0);

        } else if (v == fullScreenTv) {
            toggleFullScreen();

        } else if (v == videoQualityTv) {
            //清晰度
            if (popVideoQuality == null) {
                popVideoQuality = new PopVideoQuality(getContext(), videoQualityList);
            }
            popVideoQuality.setOnVideoQualityItemClickListener(listener);
            popVideoQuality.showAtLocation(v, GravityCompat.END, 0, 0);
        }
    }

    private PopVideoQuality popVideoQuality;

    public void setFullScreenLayout(ViewGroup videoContainerParent, ViewGroup videoContainer) {
        this.videoControllerContainer = videoContainer;
        this.videoControllerContainerParent = videoContainerParent;
    }

    public boolean isFullScreen() {
        if (videoControllerContainerParent == null || videoControllerContainer == null) {
            return false;
        }
        int index = videoControllerContainerParent.indexOfChild(videoControllerContainer);
        return index == -1;
    }

    public void toggleFullScreen() {
        toggleFullScreen(false);
    }

    public void toggleFullScreen(boolean autoRotation) {
        if (videoControllerContainerParent == null || videoControllerContainer == null) {
            return;
        }

        int index = videoControllerContainerParent.indexOfChild(videoControllerContainer);
        boolean notFullScreen = index != -1;
        Activity activity = ControllerUtil.getActivity(getContext());
        if (activity == null) {
            return;
        }

        ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
        if (notFullScreen) {
            videoControllerContainerParent.removeView(videoControllerContainer);
            content.addView(videoControllerContainer, MATCH_PARENT, MATCH_PARENT);
            ControllerUtil.toggleActionBarAndStatusBar(getContext(), true);
            if (!autoRotation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

        } else {
            content.removeView(videoControllerContainer);
            videoControllerContainerParent.addView(videoControllerContainer, MATCH_PARENT, MATCH_PARENT);
            ControllerUtil.toggleActionBarAndStatusBar(getContext(), false);
            if (!autoRotation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    private void setCurrentVolume(int currentVolume) {
        boolean hasVolume = currentVolume > 0;
        voiceTv.setSelected(hasVolume);
    }

    public void setProgress(int progress) {
        String currentTime = ControllerUtil.formatTime(progress);
        currentTimeTv.setText(currentTime);
        progressSeekBar.setProgress(progress);
        progressBar.setProgress(progress);
    }

    public void setMax(int max) {
        String totalTime = ControllerUtil.formatTime(max);
        totalTimeTv.setText(totalTime);
        progressSeekBar.setMax(max);
        progressBar.setMax(max);
    }

    public void setSecondaryProgress(int secondaryProgress) {
        progressSeekBar.setSecondaryProgress(secondaryProgress);
        progressBar.setSecondaryProgress(secondaryProgress);
    }

    public int getMax() {
        return progressSeekBar.getMax();
    }

    public int getProgress() {
        return progressSeekBar.getProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentTimeTv.setText(ControllerUtil.formatTime(progress));
        totalTimeTv.setText(ControllerUtil.formatTime(seekBar.getMax()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (onStopTouchProgressChangeListener != null) {
            onStopTouchProgressChangeListener.onStopTouchProgressChange(seekBar.getProgress());
        }
    }

    private PopVideoQuality.OnVideoQualityItemClickListener listener;

    public void setOnVideoQualityItemClickListener(PopVideoQuality.OnVideoQualityItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnStopTouchProgressChangeListener{

        void onStopTouchProgressChange(int progress);
    }

    private OnStopTouchProgressChangeListener onStopTouchProgressChangeListener;

    public void setOnStopTouchProgressChangeListener(OnStopTouchProgressChangeListener l) {
        this.onStopTouchProgressChangeListener = l;
    }
}
