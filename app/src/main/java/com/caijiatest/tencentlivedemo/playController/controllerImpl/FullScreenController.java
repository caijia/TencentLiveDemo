package com.caijiatest.tencentlivedemo.playController.controllerImpl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caijiatest.tencentlivedemo.R;
import com.caijiatest.tencentlivedemo.playController.Controller;
import com.caijiatest.tencentlivedemo.playController.util.ControllerUtil;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerBottomBar;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerLoading;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerSwitcher;
import com.caijiatest.tencentlivedemo.playController.widget.GestureRelativeLayout;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;

/**
 * Created by cai.jia on 2017/8/22.
 */

public class FullScreenController extends GestureRelativeLayout implements Controller,
        GestureRelativeLayout.OnGestureListener, ITXLivePlayListener,
        ControllerSwitcher.OnPlayStateListener, ControllerBottomBar.OnStopTouchProgressChangeListener,
        View.OnClickListener {

    private ControllerLoading controllerLoading;
    private ControllerSwitcher controllerSwitcher;
    private ControllerBottomBar controllerBottomBar;
    private TXLivePlayer livePlayer;
    private boolean isShowController;
    private Handler handler = new Handler();
    private Runnable hideControllerTask = new Runnable() {
        @Override
        public void run() {
            hideController();
        }
    };

    public FullScreenController(Context context) {
        this(context, null);
    }

    public FullScreenController(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FullScreenController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FullScreenController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOnGestureListener(this);
        LayoutInflater.from(context).inflate(R.layout.video_controller, this, true);
        controllerLoading = (ControllerLoading) findViewById(R.id.controller_loading);
        controllerSwitcher = (ControllerSwitcher) findViewById(R.id.view_switcher);
        controllerBottomBar = (ControllerBottomBar) findViewById(R.id.controller_bottom_bar);
        controllerSwitcher.setOnPlayStateListener(this);
        controllerBottomBar.setOnStopTouchProgressChangeListener(this);
        this.setOnClickListener(this);
    }

    private boolean isPlaying() {
        return livePlayer != null && livePlayer.isPlaying();
    }

    @Override
    public void setLivePlayer(TXLivePlayer txLivePlayer) {
        livePlayer = txLivePlayer;
        txLivePlayer.setPlayListener(this);
    }

    @Override
    public void setFullScreenLayout(ViewGroup playerContainer, ViewGroup playerContainerParent) {
        if (controllerBottomBar != null) {
            controllerBottomBar.setFullScreenLayout(playerContainerParent, playerContainer);
        }
    }

    @Override
    public boolean isFullScreen() {
        return controllerBottomBar != null && controllerBottomBar.isFullScreen();
    }

    @Override
    public void toggleFullScreen() {
        if (controllerBottomBar != null) {
            controllerBottomBar.toggleFullScreen();
        }
    }

    @Override
    public void startPlay() {
        if (controllerLoading != null) {
            controllerLoading.show();
        }
    }

    public void onLeftVerticalMove(@MoveState int state, float distance, float deltaY) {
        int dp = ControllerUtil.spToDp(getContext(), distance);
        int brightness = dp * 2;
        switch (state) {
            case GestureRelativeLayout.START:
                controllerSwitcher.startSetBrightness();
                break;

            case GestureRelativeLayout.MOVE:
                controllerSwitcher.incrementBrightness(brightness);
                break;

            case GestureRelativeLayout.END:
                controllerSwitcher.hide();
                break;
        }
    }

    public void onRightVerticalMove(@MoveState int state, float distance, float deltaY) {
        int dp = ControllerUtil.spToDp(getContext(), distance);
        int volume = (int) (dp * 0.1f);
        switch (state) {
            case GestureRelativeLayout.START:
                controllerSwitcher.startSetVolume();
                break;

            case GestureRelativeLayout.MOVE:
                controllerSwitcher.incrementVolume(volume);
                break;

            case GestureRelativeLayout.END:
                controllerSwitcher.hide();
                break;
        }
    }

    public void onHorizontalMove(@MoveState int state, float distance, float deltaX) {
        int dp = ControllerUtil.spToDp(getContext(), distance);
        int time = Math.round(dp * 0.5f);
        switch (state) {
            case GestureRelativeLayout.START:
                int currentProgress = controllerBottomBar.getProgress();
                int max = controllerBottomBar.getMax();
                controllerSwitcher.startTimeProgress(currentProgress, max);
                break;

            case GestureRelativeLayout.MOVE:
                controllerSwitcher.incrementTimeProgress(time);
                long currentTime = controllerSwitcher.getCurrentProgress();
                controllerBottomBar.setProgress((int) (currentTime + time));
                break;

            case GestureRelativeLayout.END:
                controllerSwitcher.stopTimeProgress();
                setPlayProgress(controllerBottomBar.getProgress());
                break;
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        switch (event) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN: {
                controllerLoading.hide();
                controllerSwitcher.setPlayingState(true);
                break;
            }

            case TXLiveConstants.PLAY_EVT_PLAY_LOADING: {
                controllerLoading.show();
                int netSpeed = param.getInt(TXLiveConstants.NET_STATUS_NET_SPEED);
                controllerLoading.setNetSpeed(netSpeed);
                controllerSwitcher.setPlayingState(false);
                break;
            }

            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS: {
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS); //进度（秒数）
                int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION); //时间（秒数）
                controllerBottomBar.setMax(duration);
                controllerBottomBar.setProgress(progress);
                break;
            }

            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
            case TXLiveConstants.PLAY_EVT_PLAY_END: {
                controllerSwitcher.setPlayingState(false);
                stopPlayer();
                break;
            }
        }
    }

    private void stopPlayer() {
        if (livePlayer != null) {
            livePlayer.enableHardwareDecode(true); //关闭硬解码
            livePlayer.stopRecord();
            livePlayer.setPlayListener(null);
            livePlayer.stopPlay(true);
        }
    }

    @Override
    public void onNetStatus(Bundle param) {
    }

    @Override
    public void onPlayState(boolean playingState) {
        if (playingState) {
            livePlayer.pause();

        } else {
            livePlayer.resume();
        }
        controllerSwitcher.setPlayingState(!playingState);
    }

    @Override
    public void onStopTouchProgressChange(int progress) {
        setPlayProgress(progress);
    }

    private void setPlayProgress(int progress) {
        if (isPlaying()) {
            livePlayer.seek(progress);
        }
    }

    @Override
    public void onClick(View v) {
        if (isShowController) {
            hideController();
        } else {
            showController();
        }
    }

    public void showController() {
        controllerBottomBar.show();
        controllerSwitcher.show();
        isShowController = true;
        handler.removeCallbacks(hideControllerTask);
        handler.postDelayed(hideControllerTask, 4000);
    }

    public void hideController() {
        controllerBottomBar.hide();
        controllerSwitcher.hide();
        isShowController = false;
    }
}
