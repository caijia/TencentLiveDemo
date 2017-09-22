package com.caijiatest.tencentlivedemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.asha.vrlib.MDVRLibrary;
import com.caijiatest.tencentlivedemo.player.IjkPlayerWrapper;
import com.caijiatest.tencentlivedemo.util.DeviceUtil;
import com.caijiatest.tencentlivedemo.util.ImHelper;
import com.caijiatest.tencentlivedemo.util.MixStreamHelper;
import com.google.android.apps.muzei.render.GLTextureView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by cai.jia on 2017/8/31 0031.
 */

public class TestLiveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String tag = "tecent_im_log";

    private Button btnLink;

    private GLTextureView glTextureView;

    /**
     * 负责播放
     */
    private TXCloudVideoView playView;

    /**
     * 负责推流
     */
    private TXCloudVideoView pushView;

    private static final String VR_URL = "rtmp://live.vr-mu.com/live/LIVEQM15E93AB26A8";
    private IjkPlayerWrapper vrPlayer;
    private MDVRLibrary mVRLibrary;

    private TXLivePlayer livePlayer;

    /**
     * 主播
     */
    public static final int LIVE_MASTER = 0;

    /**
     * 观众
     */
    public static final int LIVE_GUEST = 1;

    /**
     * 连麦者
     */
    public static final int LIVE_LINK = 2;

    private static final String ROLE_KEY = "params:role";

    private int role = LIVE_MASTER;

    public static Intent getIntent(Context context,int role) {
        Intent i = new Intent(context, TestLiveActivity.class);
        i.putExtra(ROLE_KEY, role);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_live);
        btnLink = (Button) findViewById(R.id.btn_link);
        btnLink.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            role = intent.getExtras().getInt(ROLE_KEY);
        }

        glTextureView = (GLTextureView) findViewById(R.id.gl_textureview);
        playView = (TXCloudVideoView) findViewById(R.id.video_view1);
        pushView = (TXCloudVideoView) findViewById(R.id.video_view2);
        if (role == LIVE_GUEST) {
            playView.getLayoutParams().width = DeviceUtil.dpToPx(this, 300);
        }

        //play vr
        vrPlayer = new IjkPlayerWrapper();
        initVRLibrary();
        vrPlayer.startPlay(VR_URL);

        roleState();

        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                //消息的内容解析请参考消息收发文档中的消息解析说明
                if (list != null && !list.isEmpty()) {
                    for (TIMMessage timMessage : list) {
                        long count = timMessage.getElementCount();
                        for (int i = 0; i < count; i++) {
                            TIMElem elem = timMessage.getElement(i);
                            TIMElemType type = elem.getType();
                            if (type == TIMElemType.Text) {
                                //处理文本消息
                                TIMTextElem txtElem = (TIMTextElem) elem;
                                String text = txtElem.getText();

                                try {
                                    JSONObject jo = new JSONObject(text);
                                    String linkName = jo.optString("name");//连麦者
                                    String playUrl = jo.optString("playUrl");
                                    String secret = jo.optString("secret");
                                    play(playUrl,secret);

                                    String mainStreamId = "8768_f5e6031f80";
                                    String subStreamId = "8768_f5e6031fcc";
                                    new MixStreamHelper().asyncMixStream(getApplicationContext(), mainStreamId, subStreamId);

                                    Log.d(tag, jo.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                return true; //返回true将终止回调链，不再调用下一个新消息监听器
            }
        });
    }

    private void initVRLibrary(){
        // new instance
        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
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
        playView.onResume();
        pushView.onResume();
        vrPlayer.resume();
        mVRLibrary.onResume(this);

        if (livePlayer != null)
        livePlayer.resume();

        if (mLivePusher != null)
        mLivePusher.resumePusher();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playView.onPause();
        pushView.onPause();
        vrPlayer.pause();
        mVRLibrary.onPause(this);

        if (livePlayer != null)
        livePlayer.pause();

        if (mLivePusher != null)
        mLivePusher.pausePusher();
    }

    private String groupId = "199141921";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playView.onDestroy();
        pushView.onDestroy();
        vrPlayer.destroy();
        mVRLibrary.onDestroy();

        if (livePlayer != null)
        livePlayer.stopPlay(true);
        stopRtmpPublish();

        if (role != LIVE_MASTER)
        TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {

            }
        });

        if (role == LIVE_MASTER)
        TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(tag, "delete group onError");
            }

            @Override
            public void onSuccess() {
                Log.e(tag, "delete group onSuccess");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
    }

    public void roleState() {
        switch (role) {
            case LIVE_MASTER:{
                push(MASTER_PUSH_URL, new ITXLivePushListener() {
                    @Override
                    public void onPushEvent(int i, Bundle bundle) {
                        if (i == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
                            Log.d(tag, "master push success");
                        }
                    }

                    @Override
                    public void onNetStatus(Bundle bundle) {

                    }
                });
                btnLink.setVisibility(View.GONE);
                break;
            }

            case LIVE_GUEST:{
                btnLink.setVisibility(View.GONE);
                play(MASTER_PLAY_URL,MASTER_SECRET);
                break;
            }

            case LIVE_LINK:{
                btnLink.setVisibility(View.VISIBLE);
                play(MASTER_PLAY_URL,MASTER_SECRET);
                break;
            }
        }
    }

    private TXLivePusher mLivePusher;


    public void stopRtmpPublish() {
        if (mLivePusher != null) {
            mLivePusher.stopCameraPreview(true); //停止摄像头预览
            mLivePusher.stopPusher();            //停止推流
            mLivePusher.setPushListener(null);   //解绑 listener
        }
    }

    /**
     * 主播推流
     */
    private void push(String pushUrl, ITXLivePushListener livePushListener) {
        mLivePusher = new TXLivePusher(this);
        mLivePusher.startPusher(pushUrl);
        mLivePusher.startCameraPreview(pushView);
        //设定清晰度
        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER, true, false);
        //美颜
        mLivePusher.setBeautyFilter(7, 3);
        mLivePusher.setPushListener(livePushListener);
    }

    private static final String MASTER_PUSH_URL = "rtmp://8768.livepush.myqcloud.com/live/8768_f5e6031f80?bizid=8768&txSecret=e44e183dafb90ecfcd10e4348400a45d&txTime=59B16CFF";
    private static final String MASTER_SECRET = "?bizid=8768&txSecret=e44e183dafb90ecfcd10e4348400a45d&txTime=59B16CFF";
    private static final String MASTER_PLAY_URL = "rtmp://8768.liveplay.myqcloud.com/live/8768_f5e6031f80";

    private void play(String playUrl,String secret) {
        livePlayer = new TXLivePlayer(this);
        livePlayer.setPlayerView(playView);
        livePlayer.startPlay(playUrl + (role == LIVE_LINK || role == LIVE_MASTER ? secret : ""),
                role == LIVE_LINK || role == LIVE_MASTER
                ? TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC
                : TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_link:{
                //请求连麦发给主播
                final JSONObject jo = new JSONObject();
                try {
                    jo.put("playUrl", LINKER_PLAY_URL);
                    jo.put("secret", LINKER_SECRET);
                    jo.put("name", "liveLink");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //连麦者推流
                push(LINKER_PUSH_URL, new ITXLivePushListener() {
                    @Override
                    public void onPushEvent(int i, Bundle bundle) {
                        if (i == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
                            Log.d(tag, "linker push success");
                            ImHelper.getInstance().sendTextMessage(jo.toString(),"xiaojun", TIMConversationType.C2C);
                        }
                    }

                    @Override
                    public void onNetStatus(Bundle bundle) {

                    }
                });
                break;
            }
        }
    }

    private static final String LINKER_PUSH_URL = "rtmp://8768.livepush.myqcloud.com/live/8768_f5e6031fcc?bizid=8768&txSecret=5980314565050027cf0d7cdf1656423d&txTime=59CFBF7F";
    private static final String LINKER_PLAY_URL = "rtmp://8768.liveplay.myqcloud.com/live/8768_f5e6031fcc";
    private static final String LINKER_SECRET = "?bizid=8768&txSecret=5980314565050027cf0d7cdf1656423d&txTime=59CFBF7F";


}
