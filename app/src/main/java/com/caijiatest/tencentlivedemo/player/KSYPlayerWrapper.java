package com.caijiatest.tencentlivedemo.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer.KSYDecodeMode;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import static com.caijiatest.tencentlivedemo.player.PlayerConstants.DISPLAY_CENTER_CROP;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.DISPLAY_WRAP_CONTENT;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_CACHE_PERCENT;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_MEDIA_DECODE;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_NET_SPEED;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_PLAY_DURATION;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_PLAY_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_PLAY_SECOND_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_VIDEO_HEIGHT;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_VIDEO_ROTATION;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PARAMS_VIDEO_WIDTH;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_BEGIN;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_BUFFERING_END;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_DECODE;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_END;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_ERROR;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_INVOKE_PLAY;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_LOADING;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_PAUSE;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_PREPARED;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_RELOAD_SUCCESS;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_VIDEO_ROTATION;
import static com.caijiatest.tencentlivedemo.player.PlayerConstants.PLAY_EVENT_VIDEO_SIZE_CHANGE;
import static com.ksyun.media.player.IMediaPlayer.MEDIA_INFO_HARDWARE_DECODE;
import static com.ksyun.media.player.IMediaPlayer.MEDIA_INFO_SOFTWARE_DECODE;
import static com.ksyun.media.player.KSYMediaPlayer.Builder;
import static com.ksyun.media.player.KSYMediaPlayer.MEDIA_INFO_BUFFERING_START;
import static com.ksyun.media.player.KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD;
import static com.ksyun.media.player.KSYMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED;

/**
 * Created by cai.jia on 2017/8/23.
 */

public class KSYPlayerWrapper implements PlayerWrapper, IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnVideoSizeChangedListener, TextureView.SurfaceTextureListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_STOPPED = 5;
    private static final int STATE_COMPLETED = 6;
    private int state = STATE_IDLE;

    private ProgressHandler playProgressHandler;
    private KSYMediaPlayer player;
    private Set<OnPlayEventListener> playEventListeners;
    private Bundle params;
    private int cachePercent;
    private TextureView textureView;
    private int displayMode;
    private int rotation;
    private int videoWidth;
    private int videoHeight;
    private SurfaceTexture surfaceTexture;
    private Context context;

    public KSYPlayerWrapper(Context context) {
        this.context = context;
        player = new Builder(context).build();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnInfoListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.shouldAutoPlay(false);
        params = new Bundle();
        playProgressHandler = new ProgressHandler(this);
        playEventListeners = new HashSet<>();
    }

    public void setMediaPlayerWrapper(KSYPlayerWrapper wrapper) {
        this.player = wrapper.player;
        this.params = wrapper.params;
        this.state = wrapper.state;
        this.playProgressHandler.stop();
        this.playProgressHandler = wrapper.playProgressHandler;
        if (playEventListeners != null) {
            for (OnPlayEventListener playEventListener : playEventListeners) {
                wrapper.addOnPlayEventListener(playEventListener);
            }
        }
        this.playEventListeners = wrapper.playEventListeners;
    }

    @Override
    public void startPlay(String uri) {
        setDataSource(uri);
        prepare();
    }

    @Override
    public void setSurface(Surface surface) {
        if (player != null) {
            player.setSurface(surface);
        }
    }

    @Override
    public void setDataSource(String uri) {
        try {
            if (player != null) {
                player.setDataSource(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            state = STATE_ERROR;
            dispatchEvent(PLAY_EVENT_ERROR);
        }
    }

    @Override
    public void prepare() {
        Log.d("playerWrapper", "prepare");
        setDecodeMode(KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        state = STATE_PREPARING;
        player.setTimeout(10,10);
        player.prepareAsync();
        dispatchEvent(PLAY_EVENT_INVOKE_PLAY);
    }

    @Override
    public boolean canPlay() {
        return player != null && (state == STATE_PREPARED || state == STATE_PLAYING
                || state == STATE_PAUSED || state == STATE_COMPLETED);
    }

    @Override
    public void start() {
        if (canPlay()) {
            player.start();
            state = STATE_PLAYING;
            dispatchEvent(PLAY_EVENT_BEGIN);

        } else {
            if (state == STATE_STOPPED) {
                prepare();

            } else if (state == STATE_ERROR) {
                //金山云如果错误重连,需要重置
                String dataSource = player.getDataSource();
                player.reset();
                setDataSource(dataSource);
                prepare();
                player.setSurface(new Surface(textureView.getSurfaceTexture()));
            }
        }
    }

    @Override
    public void stop() {
        if (canPlay()) {
            player.stop();
            state = STATE_STOPPED;
            dispatchEvent(PLAY_EVENT_END);
        }
    }

    @Override
    public void resume() {
        start();
    }

    @Override
    public void pause() {
        if (canPlay()) {
            player.pause();
            state = STATE_PAUSED;
            dispatchEvent(PLAY_EVENT_PAUSE);
        }
    }

    private void releaseMediaPlayer(){
        if (player != null) {
            player.setSurface(null);
            player.setOnPreparedListener(null);
            player.setOnCompletionListener(null);
            player.setOnErrorListener(null);
            player.setOnBufferingUpdateListener(null);
            player.setOnInfoListener(null);
            player.setOnVideoSizeChangedListener(null);
            player.release();
        }
        if (playProgressHandler != null) {
            playProgressHandler.stop();
        }
    }

    @Override
    public void destroy() {
        stop();
        if (playEventListeners != null) {
            playEventListeners.clear();
        }

        releaseMediaPlayer();

        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        player = null;
    }

    @Override
    public boolean isPlaying() {
        return canPlay() && player.isPlaying();
    }

    private void seekTo(long progress) {
        if (canPlay()) {
            player.seekTo(progress);
        }
    }

    @Override
    public void seekTo(int progress) {
        if (canPlay()) {
            player.seekTo(progress * 1000);
        }
    }

    @Override
    public void setTextureView(TextureView textureView) {
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(this);
    }

    /**
     * 重新加载新的播放地址(适应于视频清晰度播放等)
     * 重开一个新的播放器,播放新的视频源(禁音),准备完成后,seekTo到当前播放的位置(同步当前播放器的位置),当播放不卡顿时,
     * 设置surfaceView，释放掉前一个播放器(不禁音)。
     */
    @Override
    public void reload(String url) {
        if (canPlay() && !TextUtils.isEmpty(url)) {
            //新的播放器
            final KSYPlayerWrapper wrapper = new KSYPlayerWrapper(context);
            wrapper.setPlayerMute(1); //禁音
            wrapper.addOnPlayEventListener(new OnPlayEventListener() {
                @Override
                public void onPlayEvent(int event, Bundle params) {
                    switch (event) {
                        case PlayerConstants.PLAY_EVENT_PREPARED:{
                            Log.d("playerWrapper", "new player prepared");
                            break;
                        }

                        case PlayerConstants.PLAY_EVENT_PROGRESS:{
                            long newProgress = wrapper.getLongProgress();
                            long oldProgress = getLongProgress();
                            if (!canPlay()) {
                                //旧的播放器不能播了,那么清晰度切换终止
                                Log.d("playerWrapper", "old player error");
                                wrapper.destroy();

                            }else{
                                //同步与当前播放器的进度
                                wrapper.seekTo(oldProgress);
                                if (wrapper.isPlaying()) {
                                    wrapper.removePlayEventListener(this);
                                    wrapper.setSurface(new Surface(textureView.getSurfaceTexture()));
                                    releaseMediaPlayer();
                                    wrapper.setPlayerMute(0); //不禁音
                                    setMediaPlayerWrapper(wrapper);
                                    dispatchEvent(PLAY_EVENT_RELOAD_SUCCESS);
                                    Log.d("playerWrapper", "success");
                                }
                                Log.d("playerWrapper", "oldProgress = " + oldProgress + "newProgress = "+newProgress);
                            }
                            break;
                        }

                        case PlayerConstants.PLAY_EVENT_END:
                        case PlayerConstants.PLAY_EVENT_ERROR:{
                            Log.d("playerWrapper", "new player end or error");
                            //新播放器不能播了,那么清晰度切换终止
                            wrapper.destroy();
                            break;
                        }
                    }
                }
            });
            wrapper.startPlay(url);
        }
    }

    /**
     * 设置解码模式
     *
     * @param decodeMode 解码模式
     *                   播放器使用软解
     *                   {@link KSYDecodeMode#KSY_DECODE_MODE_SOFTWARE}
     *                   <p>
     *                   SDK尝试使用硬解，当机器不在白名单里时，自动切换至软解
     *                   {@link KSYDecodeMode#KSY_DECODE_MODE_AUTO}
     *                   <p>
     *                   SDK尝试使用硬解，当机器不在白名单里时，自动切换至软解
     *                   {@link KSYDecodeMode#KSY_DECODE_MODE_HARDWARE}
     */
    public void setDecodeMode(KSYDecodeMode decodeMode) {
        if (player != null) {
            player.setDecodeMode(decodeMode);
        }
    }

    /**
     * 视频截图
     * Note:
     * 在播放视频时即可调用
     */
    public @Nullable Bitmap getScreenShot() {
        if (canPlay()) {
           return player.getScreenShot();
        }
        return null;
    }

    /**
     * 支持播放过程中变速 1.0f为正常播放速度
     * @param speed
     */
    public void setPlaySpeed(float speed) {
        if (player != null) {
            player.setSpeed(speed);
        }
    }

    public float getPlaySpeed() {
        return player != null ? player.getSpeed() : 1.0f;
    }

    @Override
    public void setPlayerMute(int mute) {
        if (player != null) {
            player.setPlayerMute(mute);
        }
    }

    @Override
    public void addOnPlayEventListener(OnPlayEventListener playEventListener) {
        if (playEventListener != null) {
            playEventListeners.add(playEventListener);
        }
    }

    public void removePlayEventListener(OnPlayEventListener playEventListener) {
        if (playEventListener != null) {
            playEventListeners.remove(playEventListener);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        state = STATE_PREPARED;
        dispatchEvent(PLAY_EVENT_PREPARED);
        start();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        state = STATE_COMPLETED;
        dispatchEvent(PLAY_EVENT_END);
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        state = STATE_ERROR;
        dispatchEvent(PLAY_EVENT_ERROR);
        return true;
    }

    @Override
    public void setVideoDisplayMode(int displayMode) {
        this.displayMode = displayMode;
        if (canPlay()) {
            applyDisplayMode();
        }
    }

    private void applyDisplayMode() {
        if (textureView == null) {
            return;
        }
        switch (displayMode) {
            case DISPLAY_WRAP_CONTENT: {
                VideoDisplayHelper.wrapContent(textureView, videoWidth, videoHeight, rotation);
                break;
            }

            case DISPLAY_CENTER_CROP: {
                VideoDisplayHelper.centerCrop(textureView, videoWidth, videoHeight, rotation);
                break;
            }
        }
    }

    private int getDuration() {
        if (canPlay()) {
            return (int) player.getDuration() / 1000;
        }
        return 0;
    }

    private int getProgress() {
        if (canPlay()) {
            return (int) player.getCurrentPosition() / 1000;
        }
        return 0;
    }

    private long getLongProgress() {
        if (canPlay()) {
            return (int) player.getCurrentPosition();
        }
        return 0;
    }

    private int getCacheProgress() {
        if (canPlay()) {
            return (int) (getDuration() * (cachePercent / 100f));
        }
        return 0;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        cachePercent = percent;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case MEDIA_INFO_VIDEO_ROTATION_CHANGED: {
                rotation = extra;
                params.putInt(PARAMS_VIDEO_ROTATION, rotation);
                dispatchEvent(PLAY_EVENT_VIDEO_ROTATION);
                break;
            }

            case MEDIA_INFO_BUFFERING_START: {
                params.putInt(PARAMS_NET_SPEED, what);
                dispatchEvent(PLAY_EVENT_LOADING);
                Log.d("playerWrapper", "onInfo = " + what + "extra = " + extra);
                break;
            }

            case IMediaPlayer.MEDIA_INFO_BUFFERING_END: {
                dispatchEvent(PLAY_EVENT_BUFFERING_END);
                Log.d("playerWrapper", "onInfo = " + what + "extra = " + extra);
                break;
            }

            case MEDIA_INFO_SUGGEST_RELOAD: {
                // 播放SDK有做快速开播的优化，在流的音视频数据交织并不好时，可能只找到某一个流的信息
                // 当播放器读到另一个流的数据时会发出此消息通知
                // 请务必调用reload接口
                if (player != null) {
                    player.reload(player.getDataSource(), false);
                }
                break;
            }

            case MEDIA_INFO_SOFTWARE_DECODE:
            case MEDIA_INFO_HARDWARE_DECODE:{
                params.putInt(PARAMS_MEDIA_DECODE, what);
                dispatchEvent(PLAY_EVENT_DECODE);
                break;
            }
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int i2, int i3) {
        videoWidth = width;
        videoHeight = height;
        applyDisplayMode();
        params.putInt(PARAMS_VIDEO_WIDTH, videoWidth);
        params.putInt(PARAMS_VIDEO_HEIGHT, videoHeight);
        dispatchEvent(PLAY_EVENT_VIDEO_SIZE_CHANGE);
    }

    private void dispatchEvent(int event) {
        for (OnPlayEventListener playEventListener : playEventListeners) {
            if (playEventListener != null) {
                playEventListener.onPlayEvent(event, params);
            }
        }

        switch (event) {
            case PLAY_EVENT_PREPARED:
            case PLAY_EVENT_BEGIN:
            case PLAY_EVENT_BUFFERING_END:
                if (playProgressHandler != null) {
                    playProgressHandler.start();
                }
                break;

            case PLAY_EVENT_PAUSE:
            case PLAY_EVENT_LOADING:
            case PLAY_EVENT_END:
            case PLAY_EVENT_ERROR:
                if (playProgressHandler != null) {
                    playProgressHandler.stop();
                }
                break;
        }
    }

    private void dispatchEventProgress(int duration, int progress, int secondProgress) {
        params.putInt(PARAMS_PLAY_DURATION, duration);
        params.putInt(PARAMS_PLAY_PROGRESS, progress);
        params.putInt(PARAMS_PLAY_SECOND_PROGRESS, secondProgress);
        params.putInt(PARAMS_CACHE_PERCENT, cachePercent);

        for (OnPlayEventListener playEventListener : playEventListeners) {
            if (playEventListener != null) {
                playEventListener.onPlayEvent(PLAY_EVENT_PROGRESS, params);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (this.surfaceTexture == null) {
            this.surfaceTexture = surfaceTexture;
            Surface surface = new Surface(this.surfaceTexture);
            setSurface(surface);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textureView.setSurfaceTexture(this.surfaceTexture);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        applyDisplayMode();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        applyDisplayMode();
    }

    private static class ProgressHandler extends Handler implements Runnable {

        private static final int MSG_PROGRESS = 3001;
        WeakReference<KSYPlayerWrapper> ref;
        boolean isStop;

        ProgressHandler(KSYPlayerWrapper ijkPlayerWrapper) {
            super(Looper.getMainLooper());
            ref = new WeakReference<>(ijkPlayerWrapper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS: {
                    KSYPlayerWrapper player = ref.get();
                    if (player != null) {
                        player.dispatchEventProgress(player.getDuration(), player.getProgress(),
                                player.getCacheProgress());
                    }else{
                        Log.d("playerWrapper", "KSYPlayerWrapper is null");
                    }
                    break;
                }
            }
        }

        void start() {
            isStop = false;
            removeCallbacks(this);
            removeMessages(MSG_PROGRESS);

            post(this);
        }

        private Message obtainProgressMessage() {
            Message msg = Message.obtain();
            msg.what = MSG_PROGRESS;
            return msg;
        }

        void stop() {
            isStop = true;
            removeCallbacks(this);
            removeMessages(MSG_PROGRESS);
        }

        @Override
        public void run() {
            if (!isStop) {
                sendMessage(obtainProgressMessage());
                postDelayed(this, 500);
            }
        }
    }
}
