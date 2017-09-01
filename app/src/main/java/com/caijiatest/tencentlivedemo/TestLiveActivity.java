package com.caijiatest.tencentlivedemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asha.vrlib.MDVRLibrary;
import com.caijiatest.tencentlivedemo.player.IjkPlayerWrapper;
import com.google.android.apps.muzei.render.GLTextureView;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by cai.jia on 2017/8/31 0031.
 */

public class TestLiveActivity extends AppCompatActivity implements View.OnClickListener {

    private GLTextureView glTextureView;
    private TXCloudVideoView cloudVideoView;

    private static final String VR_URL = "rtmp://8768.liveplay.myqcloud.com/live/8768_f12b7c27ca?bizid=8768&txSecret=8059e36053e2a51ff96236e68017bcae&txTime=59A8327F";
    private IjkPlayerWrapper vrPlayer;
    private MDVRLibrary mVRLibrary;

    private TXLivePlayer livePlayer;

    private EditText etRole;
    private Button btnStart;

    // 0主播  1连麦者
    private static final int LIVE_MASTER = 0;
    private static final int LIVE_GUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_live);

        glTextureView = (GLTextureView) findViewById(R.id.gl_textureview);
        cloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);

        etRole = (EditText) findViewById(R.id.et_role);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        //play vr
        vrPlayer = new IjkPlayerWrapper();
        initVRLibrary();
        vrPlayer.startPlay(VR_URL);


        livePlayer = new TXLivePlayer(this);
        livePlayer.setPlayerView(cloudVideoView);
    }

    private void initVRLibrary(){
        // new instance
        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.PROJECTION_MODE_DOME180)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        // IjkMediaPlayer or MediaPlayer
                        vrPlayer.setSurface(surface);
                    }
                })
                .build(glTextureView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cloudVideoView.onResume();
        vrPlayer.resume();
        mVRLibrary.onResume(this);
        livePlayer.resume();

        if (mLivePusher != null)
        mLivePusher.resumePusher();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cloudVideoView.onPause();
        vrPlayer.pause();
        mVRLibrary.onPause(this);
        livePlayer.pause();

        if (mLivePusher != null)
        mLivePusher.pausePusher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cloudVideoView.onDestroy();
        vrPlayer.destroy();
        mVRLibrary.onDestroy();
        livePlayer.stopPlay(true);
        stopRtmpPublish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
    }

    @Override
    public void onClick(View v) {
        int role = Integer.parseInt(etRole.getText().toString());
        switch (role) {
            case LIVE_MASTER:{
                //拉连麦者
                play();
                break;
            }

            case LIVE_GUEST:{
                //推流
                push(cloudVideoView);
                break;
            }
        }
    }

    private TXLivePusher mLivePusher;
    private static final String PUSH_URL = "rtmp://8768.livepush.myqcloud.com/live/8768_f12b7c27ce?bizid=8768&txSecret=6d19793d363f56dec04a6fa53dbad0d9&txTime=59A8327F";

    public void stopRtmpPublish() {
        mLivePusher.stopCameraPreview(true); //停止摄像头预览
        mLivePusher.stopPusher();            //停止推流
        mLivePusher.setPushListener(null);   //解绑 listener
    }

    private void push(TXCloudVideoView txCloudVideoView) {
        mLivePusher = new TXLivePusher(this);
        mLivePusher.startPusher(PUSH_URL);
        mLivePusher.startCameraPreview(txCloudVideoView);
        //设定清晰度
        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER, true, false);
        //美颜
        mLivePusher.setBeautyFilter(7, 3);
    }

    private static final String GUEST_URL = "rtmp://8768.liveplay.myqcloud.com/live/8768_f12b7c27ce?bizid=8768&txSecret=6d19793d363f56dec04a6fa53dbad0d9&txTime=59A8327F";
    private void play() {
        livePlayer.startPlay(GUEST_URL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
    }
}
