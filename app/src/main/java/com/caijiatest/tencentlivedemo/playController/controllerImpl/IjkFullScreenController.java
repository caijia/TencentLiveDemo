package com.caijiatest.tencentlivedemo.playController.controllerImpl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caijiatest.tencentlivedemo.R;
import com.caijiatest.tencentlivedemo.playController.IjkPlayController;
import com.caijiatest.tencentlivedemo.playController.util.ControllerUtil;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerBottomBar;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerLoading;
import com.caijiatest.tencentlivedemo.playController.widget.ControllerSwitcher;
import com.caijiatest.tencentlivedemo.playController.widget.GestureRelativeLayout;
import com.caijiatest.tencentlivedemo.player.IjkPlayerConstants;
import com.caijiatest.tencentlivedemo.player.IjkPlayerWrapper;
import com.caijiatest.tencentlivedemo.player.PlayerWrapper;

import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_ERROR;

/**
 * Created by cai.jia on 2017/8/23.
 */

public class IjkFullScreenController extends GestureRelativeLayout implements IjkPlayController,
        GestureRelativeLayout.OnGestureListener, ControllerSwitcher.OnPlayStateListener,
        ControllerBottomBar.OnStopTouchProgressChangeListener, View.OnClickListener,
        PlayerWrapper.OnPlayEventListener {

    private ControllerLoading controllerLoading;
    private ControllerSwitcher controllerSwitcher;
    private ControllerBottomBar controllerBottomBar;
    private IjkPlayerWrapper player;
    private boolean isShowController;
    private Handler handler = new Handler();
    private Runnable hideControllerTask = new Runnable() {
        @Override
        public void run() {
            hideController();
        }
    };

    public IjkFullScreenController(Context context) {
        this(context, null);
    }

    public IjkFullScreenController(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public IjkFullScreenController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public IjkFullScreenController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        return player != null && player.isPlaying();
    }

    private boolean canPlay() {
        return player != null && player.canPlay();
    }

    @Override
    public void setPlayer(IjkPlayerWrapper player) {
        this.player = player;
        player.setOnPlayEventListener(this);
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
    public void release() {
        controllerLoading.hide();
        controllerSwitcher.hide();
        controllerBottomBar.hide();
        controllerBottomBar.reset();
    }

    public void onLeftVerticalMove(@MoveState int state, float distance, float deltaY) {
        if (!canPlay()) {
            return;
        }
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
        if (!canPlay()) {
            return;
        }
        int dp = ControllerUtil.spToDp(getContext(), distance);
        int volume = (int) (dp * 2f);
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
        if (!canPlay()) {
            return;
        }
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
            case IjkPlayerConstants.PLAY_EVENT_INVOKE_PLAY:{
                if (controllerLoading != null) {
                    controllerLoading.show();
                }
                break;
            }

            case IjkPlayerConstants.PLAY_EVENT_BEGIN: {
                controllerLoading.hide();
                controllerSwitcher.setPlayingState(true);
                break;
            }

            case IjkPlayerConstants.PLAY_EVENT_PAUSE:{
                controllerSwitcher.setPlayingState(false);
                break;
            }

            case IjkPlayerConstants.PLAY_EVENT_LOADING: {
                controllerLoading.show();
                int netSpeed = param.getInt(IjkPlayerConstants.PARAMS_NET_SPEED);
                controllerLoading.setNetSpeed(netSpeed);
                controllerSwitcher.setPlayingState(false);
                break;
            }

            case IjkPlayerConstants.PLAY_EVENT_PROGRESS: {
                int duration = param.getInt(IjkPlayerConstants.PARAMS_PLAY_DURATION); //进度（秒数）
                int progress = param.getInt(IjkPlayerConstants.PARAMS_PLAY_PROGRESS); //时间（秒数）
                int secondProgress = param.getInt(IjkPlayerConstants.PARAMS_PLAY_SECOND_PROGRESS); //时间（秒数）
                controllerBottomBar.setMax(duration);
                controllerBottomBar.setProgress(progress);
                controllerBottomBar.setSecondaryProgress(secondProgress);
                break;
            }

            case PLAY_EVENT_ERROR:
            case IjkPlayerConstants.PLAY_EVENT_END: {
                controllerSwitcher.setPlayingState(false);
                stopPlayer();
                break;
            }
        }
    }

    private void stopPlayer() {
        if (player != null) {
            player.destroy();
        }
    }

    @Override
    public void onPlayState(boolean playingState) {
        if (playingState) {
            player.pause();

        } else {
            player.resume();
        }
        controllerSwitcher.setPlayingState(!playingState);
    }

    @Override
    public void onStopTouchProgressChange(int progress) {
        setPlayProgress(progress);
    }

    private void setPlayProgress(int progress) {
        if (isPlaying()) {
            player.seekTo(progress);
        }
    }

    @Override
    public void onClick(View v) {
        if (!canPlay()) {
            return;
        }
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
