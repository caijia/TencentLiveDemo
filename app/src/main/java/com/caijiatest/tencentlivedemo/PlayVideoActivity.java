package com.caijiatest.tencentlivedemo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Surface;
import android.view.View;

import com.asha.vrlib.MDVRLibrary;
import com.caijiatest.tencentlivedemo.playController.controllerImpl.KSYFullScreenController;
import com.caijiatest.tencentlivedemo.playController.entities.VideoQuality;
import com.caijiatest.tencentlivedemo.widget.KSYPlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * Created by cai.jia on 2017/8/21.
 */

public class PlayVideoActivity extends AppCompatActivity {

    KSYPlayerView ijkPlayerView;
    private MDVRLibrary mVRLibrary;
//    private GLTextureView glTextureView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ijkPlayerView = (KSYPlayerView) findViewById(R.id.ijk_player_view);
//        glTextureView = new GLTextureView(this);
//        ijkPlayerView.setTextureView(new TextureView(this));
        ijkPlayerView.attachPlayController(new KSYFullScreenController(this));
        List<VideoQuality> qualityList = getVideoQualityData();
        ijkPlayerView.setVideoQualityData(qualityList);
        // init VR Library
//        initVRLibrary();
        initDanmu();
    }

    String[] videoQualityDesc = {"标清","高清","超清","蓝光"};
    String[] videoQualityUrl = {
            "http://play.g3proxy.lecloud.com/vod/v2/MjUxLzE2LzgvbGV0di11dHMvMTQvdmVyXzAwXzIyLTExMDc2NDEzODctYXZjLTE5OTgxOS1hYWMtNDgwMDAtNTI2MTEwLTE3MDg3NjEzLWY1OGY2YzM1NjkwZTA2ZGFmYjg2MTVlYzc5MjEyZjU4LTE0OTg1NTc2ODY4MjMubXA0?b=259&mmsid=65565355&tm=1499247143&key=f0eadb4f30c404d49ff8ebad673d3742&platid=3&splatid=345&playid=0&tss=no&vtype=21&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super",
            "http://play.g3proxy.lecloud.com/vod/v2/MjQ5LzM3LzIwL2xldHYtdXRzLzE0L3Zlcl8wMF8yMi0xMTA3NjQxMzkwLWF2Yy00MTk4MTAtYWFjLTQ4MDAwLTUyNjExMC0zMTU1NTY1Mi00ZmJjYzFkNzA1NWMyNDc4MDc5OTYxODg1N2RjNzEwMi0xNDk4NTU3OTYxNzQ4Lm1wNA==?b=479&mmsid=65565355&tm=1499247143&key=98c7e781f1145aba07cb0d6ec06f6c12&platid=3&splatid=345&playid=0&tss=no&vtype=13&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super",
            "http://play.g3proxy.lecloud.com/vod/v2/MjQ5LzM3LzIwL2xldHYtdXRzLzE0L3Zlcl8wMF8yMi0xMTA3NjQxMzkwLWF2Yy00MTk4MTAtYWFjLTQ4MDAwLTUyNjExMC0zMTU1NTY1Mi00ZmJjYzFkNzA1NWMyNDc4MDc5OTYxODg1N2RjNzEwMi0xNDk4NTU3OTYxNzQ4Lm1wNA==?b=479&mmsid=65565355&tm=1499247143&key=98c7e781f1145aba07cb0d6ec06f6c12&platid=3&splatid=345&playid=0&tss=no&vtype=13&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super",
    "http://play.g3proxy.lecloud.com/vod/v2/MjQ5LzM3LzIwL2xldHYtdXRzLzE0L3Zlcl8wMF8yMi0xMTA3NjQxMzkwLWF2Yy00MTk4MTAtYWFjLTQ4MDAwLTUyNjExMC0zMTU1NTY1Mi00ZmJjYzFkNzA1NWMyNDc4MDc5OTYxODg1N2RjNzEwMi0xNDk4NTU3OTYxNzQ4Lm1wNA==?b=479&mmsid=65565355&tm=1499247143&key=98c7e781f1145aba07cb0d6ec06f6c12&platid=3&splatid=345&playid=0&tss=no&vtype=13&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super"};

    private List<VideoQuality> getVideoQualityData() {
        List<VideoQuality> list = new ArrayList<>();
        for (int i = 0; i < videoQualityDesc.length; i++) {
            VideoQuality videoQuality = new VideoQuality();
            videoQuality.setDesc(videoQualityDesc[i]);
            videoQuality.setUrl(videoQualityUrl[i]);
            list.add(videoQuality);
        }
        return list;
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
                        ijkPlayerView.getPlayer().setSurface(surface);
                    }
                })
                .build(ijkPlayerView.getVideoView());
    }

    private IDanmakuView mDanmakuView;
    DanmakuContext mContext;
    BaseDanmakuParser mParser;

    private void initDanmu(){
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuView = (IDanmakuView)findViewById(R.id.danmaku_view);
        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
//                .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair).setDanmakuMargin(40);
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mParser = new BaseDanmakuParser() {
                @Override
                protected IDanmakus parse() {
                    return new Danmakus();
                }
            };
            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    public void sendTextImgDm(View view) {
        addDanmaKuShowTextAndImage(false);
    }

    public void sendTextDm(View view) {
        addDanmaku(false);
    }

    private void addDanmaku(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        // for(int i=0;i<100;i++){
        // }
        danmaku.text = "这是一条弹幕" + System.nanoTime();
        danmaku.padding = 5;
        danmaku.priority = 1;  // 0可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);

    }

    private void addDanmaKuShowTextAndImage(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = getResources().getDrawable(R.drawable.btn_mute_mute);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    public void playVideo(View view) {
//        String flvUrl = "http://baobab.wdjcdn.com/14564977406580.mp4";
        String vrUrl = "http://baobab.wdjcdn.com/14564977406580.mp4";
        ijkPlayerView.startPlay(videoQualityUrl[0]);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVRLibrary.onResume(this);
        ijkPlayerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mVRLibrary.onPause(this);
        ijkPlayerView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ijkPlayerView.destroy();
//        mVRLibrary.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!ijkPlayerView.isFullScreen()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mVRLibrary.onOrientationChanged(this);
        ijkPlayerView.onConfigurationChanged(this);
    }
}
