package com.caijiatest.tencentlivedemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.caijiatest.tencentlivedemo.playController.Controller;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by cai.jia on 2017/8/22.
 */

public class TXPlayerView extends FrameLayout {

    private Controller controller;
    private FrameLayout videoControllerFl;
    private TXLivePlayer livePlayer;
    private TXCloudVideoView videoView;

    public TXPlayerView(@NonNull Context context) {
        this(context, null);
    }
    public TXPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TXPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TXPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutParams p = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        videoControllerFl = new FrameLayout(context);
        videoControllerFl.setBackgroundColor(Color.BLACK);
        addView(videoControllerFl, p);

        videoView = new TXCloudVideoView(context);
        videoControllerFl.addView(videoView, p);

        livePlayer = new TXLivePlayer(context);
        livePlayer.setPlayerView(videoView);
    }

    public void attachPlayController(Controller controller) {
        //remove old controller
        if (videoControllerFl != null && videoControllerFl.indexOfChild((View) controller) != -1) {
            videoControllerFl.removeView((View) this.controller);
        }

        //add new controller
        this.controller = controller;
        LayoutParams p = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        videoControllerFl.addView((View) controller, p);
        controller.setLivePlayer(livePlayer);
        controller.setFullScreenLayout(videoControllerFl, this);
    }

    public boolean isFullScreen() {
        if (controller != null) {
            if (controller.isFullScreen()) {
                controller.toggleFullScreen();
                return true;
            }
        }
        return false;
    }

    public void startPlay(String playUrl, int playType) {
        if (livePlayer != null) {
            livePlayer.startPlay(playUrl, playType);
        }

        if (controller != null) {
            controller.startPlay();
        }
    }

    public void resume() {
        if (livePlayer != null) {
            livePlayer.resume();
        }
    }

    public void pause() {
        if (livePlayer != null) {
            livePlayer.pause();
        }
    }

    public void destroy() {
        if (livePlayer != null) {
            release();
        }

        if (controller != null) {
            controller.release();
        }
    }

    private void release(){
        if (livePlayer != null) {
            livePlayer.enableHardwareDecode(true); //关闭硬解码
            livePlayer.stopRecord();
            livePlayer.setPlayListener(null);
            livePlayer.stopPlay(true);
        }
    }

    public TXCloudVideoView getVideoView() {
        return videoView;
    }

    public TXLivePlayer getPlayer() {
        return livePlayer;
    }
}
