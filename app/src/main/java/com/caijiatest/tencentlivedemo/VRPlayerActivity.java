package com.caijiatest.tencentlivedemo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.asha.vrlib.MDVRLibrary;
import com.caijiatest.tencentlivedemo.playController.util.ControllerUtil;
import com.caijiatest.tencentlivedemo.player.IjkPlayerWrapper;
import com.caijiatest.tencentlivedemo.player.PlayerWrapper;
import com.caijiatest.tencentlivedemo.widget.TXPlayerView;
import com.google.android.apps.muzei.render.GLTextureView;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by cai.jia on 2017/8/23.
 */

public class VRPlayerActivity extends AppCompatActivity{

    private GLTextureView glTextureView;
    private MDVRLibrary mVRLibrary;
    private PlayerWrapper livePlayer;
    private static final String VR_URL = "http://oss-cdn.gzcnad.com/room/VR/fccedfadee31d9a5881dd92f2fa7f5c6.mp4";
    private FrameLayout container;
    private TXPlayerView txPlayerView;

    private TXCloudVideoView txVideoView;
    private TXLivePlayer txPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_player);
        container = (FrameLayout) findViewById(R.id.fl_controller);
        glTextureView = (GLTextureView) findViewById(R.id.gl_textureview);
        txPlayerView = (TXPlayerView) findViewById(R.id.ijk_player_view);
        livePlayer = new IjkPlayerWrapper();
        txPlayer = new TXLivePlayer(this);
        TXLivePlayConfig config = new TXLivePlayConfig();
//        txPlayer.setPlayerView(txVideoView);

        // init VR Library
        container.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d("layoutChange", "left = " + left + "--top=" + top + "--bottom=" + bottom + "--right=" + right);

                if (mVRLibrary != null) {
                    mVRLibrary.onTextureResize(right - left,bottom - top);
                }
            }
        });


        initVRLibrary();
//        ControllerUtil.toggleActionBarAndStatusBar(this,true);

        String flvUrl = "http://baobab.wdjcdn.com/14564977406580.mp4";
//        txPlayerView.attachPlayController(new FullScreenController(this));
//        txPlayerView.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_VOD_MP4);
//        livePlayer.startPlay(VR_URL);


    }

    public void playVRVideo(View view) {
        livePlayer.startPlay(VR_URL);
//        txPlayer.startPlay(VR_URL, TXLivePlayer.PLAY_TYPE_VOD_MP4);
    }

    /**
     * 在rootView里面找出 大于glTextureView层级的所有View并隐藏, glTextureView的父布局（glTextureView Parent）
     * glP保存大小,并充满rootView,隐藏出glTextureView的所有控件,横屏显示
     * @param view
     */
    public void fullScreen(View view) {
        ViewGroup rootViewGroup = (ViewGroup) findViewById(android.R.id.content);
        find(container, rootViewGroup);

    }

    private int glpWidth;
    private int glpHeight;

    private boolean isFullScreen;

    private void find(ViewGroup glPView,ViewGroup container) {
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = container.getChildAt(i);
//            childView.setVisibility(View.GONE);
        }

        glPView.setVisibility(View.VISIBLE);
        glpWidth = glPView.getWidth();
        glpHeight = glPView.getHeight();
        ViewGroup.LayoutParams glParentParams = glPView.getLayoutParams();
        glParentParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        glParentParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        ControllerUtil.toggleActionBarAndStatusBar(this,true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isFullScreen = true;
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            ViewGroup.LayoutParams glParentParams = container.getLayoutParams();
            glParentParams.width = glpWidth;
            glParentParams.height = glpHeight;
            ControllerUtil.toggleActionBarAndStatusBar(this,false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullScreen = false;

        }else{
            super.onBackPressed();
        }
    }

    private void initVRLibrary(){
        // new instance
        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_GLASS)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        // IjkMediaPlayer or MediaPlayer
                        livePlayer.setSurface(surface);
//                        txPlayer.setSurface(surface);
                    }
                })
                .build(glTextureView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
        livePlayer.resume();
        txPlayerView.resume();
        txPlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
        livePlayer.pause();
        txPlayerView.pause();
        txPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
        livePlayer.destroy();
        txPlayerView.destroy();
        txPlayer.stopPlay(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
    }
}
