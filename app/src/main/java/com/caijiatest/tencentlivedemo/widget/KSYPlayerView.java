package com.caijiatest.tencentlivedemo.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.caijiatest.tencentlivedemo.playController.KSYPlayController;
import com.caijiatest.tencentlivedemo.playController.entities.VideoQuality;
import com.caijiatest.tencentlivedemo.player.KSYPlayerWrapper;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * usage:
 * <p>
 * step1:{@link #attachPlayController(KSYPlayController)}
 * step2:{@link #startPlay(String)}
 * <p>
 * 生命周期方法：
 * {@link #resume(),#pause(),#destroy(),#onConfigurationChanged(Activity)}
 * <p>
 * 返回键：
 * if (!KSYPlayerView.isFullScreen()) {
 * super.onBackPressed();
 * }
 * Created by cai.jia on 2017/8/22.
 */

public class KSYPlayerView extends FrameLayout {

    private KSYPlayController controller;
    private FrameLayout videoControllerFl;
    private KSYPlayerWrapper player;
    private TextureView textureView;

    public KSYPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public KSYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KSYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KSYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutParams p = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        videoControllerFl = new FrameLayout(context);
        videoControllerFl.setBackgroundColor(Color.BLACK);
        addView(videoControllerFl, p);

        player = new KSYPlayerWrapper(context);
        this.textureView = new TextureView(getContext());
        videoControllerFl.addView(textureView, p);
        player.setTextureView(textureView);
    }

    public void attachPlayController(KSYPlayController controller) {
        //remove old controller
        if (videoControllerFl != null && videoControllerFl.indexOfChild((View) controller) != -1) {
            videoControllerFl.removeView((View) this.controller);
        }

        //add new controller
        this.controller = controller;
        LayoutParams p = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        videoControllerFl.addView((View) controller, p);
        controller.setPlayer(player);
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

    public void startPlay(String playUrl) {
        if (player != null) {
            player.startPlay(playUrl);
        }
    }

    /**
     * 设置视频显示模式
     * {@link com.caijiatest.tencentlivedemo.player.PlayerConstants#DISPLAY_WRAP_CONTENT}
     * {@link com.caijiatest.tencentlivedemo.player.PlayerConstants#DISPLAY_CENTER_CROP}
     *
     * @param displayMode
     */
    public void setVideoDisplayMode(int displayMode) {
        if (player != null) {
            player.setVideoDisplayMode(displayMode);
        }
    }

    public void setVideoQualityData(List<VideoQuality> list) {
        if (controller != null) {
            controller.setVideoQualityData(list);
        }
    }

    public void resume() {
        if (player != null) {
            player.resume();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void destroy() {
        if (player != null) {
            player.destroy();
        }

        if (controller != null) {
            controller.release();
        }
    }

    public TextureView getVideoView() {
        return textureView;
    }

    public KSYPlayerWrapper getPlayer() {
        return player;
    }

    public void onConfigurationChanged(Activity activity) {
        if (controller != null) {
            controller.onConfigurationChanged(activity);
        }
    }
}
