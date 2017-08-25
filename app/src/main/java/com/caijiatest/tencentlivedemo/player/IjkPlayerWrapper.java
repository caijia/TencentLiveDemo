package com.caijiatest.tencentlivedemo.player;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;

import com.tencent.ijk.media.player.IMediaPlayer;
import com.tencent.ijk.media.player.IjkMediaPlayer;

import java.lang.ref.WeakReference;

import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.DISPLAY_CENTER_CROP;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.DISPLAY_WRAP_CONTENT;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_NET_SPEED;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_PLAY_DURATION;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_PLAY_ERROR;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_PLAY_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_PLAY_SECOND_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_VIDEO_HEIGHT;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_VIDEO_ROTATION;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PARAMS_VIDEO_WIDTH;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_BEGIN;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_END;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_ERROR;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_INVOKE_PLAY;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_LOADING;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_PAUSE;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_PREPARED;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_PROGRESS;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_VIDEO_ROTATION;
import static com.caijiatest.tencentlivedemo.player.IjkPlayerConstants.PLAY_EVENT_VIDEO_SIZE_CHANGE;

/**
 * Created by cai.jia on 2017/8/23.
 */

public class IjkPlayerWrapper implements PlayerWrapper, IMediaPlayer.OnPreparedListener,
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
    private IMediaPlayer player;
    private OnPlayEventListener playEventListener;
    private Bundle params;
    private int cachePercent;
    private TextureView textureView;
    private int displayMode;
    private int rotation;
    private int videoWidth;
    private int videoHeight;
    private SurfaceTexture surfaceTexture;

    public IjkPlayerWrapper() {
        player = new IjkMediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnInfoListener(this);
        player.setOnVideoSizeChangedListener(this);
        params = new Bundle();
        playProgressHandler = new ProgressHandler(this);
    }

    @Override
    public void startPlay(String uri) {
        setDataSource(uri);
        prepare();
    }

    @Override
    public void setSurface(Surface surface) {
        player.setSurface(surface);
    }

    @Override
    public void setDataSource(String uri) {
        try {
            player.setDataSource(uri);
        } catch (Exception e) {
            e.printStackTrace();
            state = STATE_ERROR;
            dispatchEventError("");
        }
    }

    @Override
    public void prepare() {
        state = STATE_PREPARING;
        player.prepareAsync();
        dispatchEventInvokePrepare();
    }

    @Override
    public boolean canPlay() {
        return state == STATE_PREPARED || state == STATE_PLAYING
                || state == STATE_PAUSED || state == STATE_COMPLETED;
    }

    @Override
    public void start() {
        if (canPlay()) {
            player.start();
            state = STATE_PLAYING;
            dispatchEventBegin();

        }else {
            if (state == STATE_STOPPED || state == STATE_ERROR) {
                prepare();
            }
        }
    }

    @Override
    public void stop() {
        if (canPlay()) {
            player.stop();
            state = STATE_STOPPED;
            dispatchEventEnd();
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
            dispatchEventPause();
        }
    }

    @Override
    public void destroy() {
        stop();
        if (player != null) {
            if (surfaceTexture != null) {
                surfaceTexture.release();
                surfaceTexture = null;
            }
            player.setSurface(null);
            player.setOnPreparedListener(null);
            player.setOnCompletionListener(null);
            player.setOnErrorListener(null);
            player.setOnBufferingUpdateListener(null);
            player.setOnInfoListener(null);
            player.setOnVideoSizeChangedListener(null);
            player.release();
        }
        player = null;
    }

    @Override
    public boolean isPlaying() {
        return canPlay() && player.isPlaying();
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

    @Override
    public void setOnPlayEventListener(OnPlayEventListener playEventListener) {
        this.playEventListener = playEventListener;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        state = STATE_PREPARED;
        dispatchEventPrepared();
        start();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        state = STATE_COMPLETED;
        dispatchEventEnd();
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        state = STATE_ERROR;
        dispatchEventError("");
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
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED: {
                rotation = extra;
                dispatchEventVideoRotation(extra);
                break;
            }

            case IMediaPlayer.MEDIA_INFO_BUFFERING_START: {
                dispatchEventLoading(extra);
                break;
            }

            case IMediaPlayer.MEDIA_INFO_BUFFERING_END: {
                dispatchEventBegin();
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
        dispatchEventVideoSizeChange(width, height);
    }

    private void dispatchEventInvokePrepare() {
        if (playEventListener != null) {
            playEventListener.onPlayEvent(PLAY_EVENT_INVOKE_PLAY, params);
        }
    }

    private void dispatchEventPrepared() {
        if (playEventListener != null) {
            playEventListener.onPlayEvent(PLAY_EVENT_PREPARED, params);
        }
        playProgressHandler.start();
    }

    private void dispatchEventBegin() {
        if (playEventListener != null) {
            playEventListener.onPlayEvent(PLAY_EVENT_BEGIN, params);
        }
        playProgressHandler.start();
    }

    private void dispatchEventPause(){
        if (playEventListener != null) {
            playEventListener.onPlayEvent(PLAY_EVENT_PAUSE, params);
        }
        playProgressHandler.stop();
    }

    private void dispatchEventLoading(int speed) {
        if (playEventListener != null) {
            params.putInt(PARAMS_NET_SPEED, speed);
            playEventListener.onPlayEvent(PLAY_EVENT_LOADING, params);
        }
        playProgressHandler.stop();
    }

    private void dispatchEventProgress(int duration, int progress, int secondProgress) {
        if (playEventListener != null) {
            params.putInt(PARAMS_PLAY_DURATION, duration);
            params.putInt(PARAMS_PLAY_PROGRESS, progress);
            params.putInt(PARAMS_PLAY_SECOND_PROGRESS, secondProgress);
            playEventListener.onPlayEvent(PLAY_EVENT_PROGRESS, params);
        }
    }

    private void dispatchEventEnd() {
        if (playEventListener != null) {
            playEventListener.onPlayEvent(PLAY_EVENT_END, params);
        }
        playProgressHandler.stop();
    }

    private void dispatchEventError(String errorMessage) {
        if (playEventListener != null) {
            params.putString(PARAMS_PLAY_ERROR, errorMessage);
            playEventListener.onPlayEvent(PLAY_EVENT_ERROR, params);
        }
        playProgressHandler.stop();
    }

    private void dispatchEventVideoRotation(int rotation) {
        if (playEventListener != null) {
            params.putInt(PARAMS_VIDEO_ROTATION, rotation);
            playEventListener.onPlayEvent(PLAY_EVENT_VIDEO_ROTATION, params);
        }
    }

    private void dispatchEventVideoSizeChange(int videoWidth, int videoHeight) {
        if (playEventListener != null) {
            params.putInt(PARAMS_VIDEO_WIDTH, videoWidth);
            params.putInt(PARAMS_VIDEO_HEIGHT, videoHeight);
            playEventListener.onPlayEvent(PLAY_EVENT_VIDEO_SIZE_CHANGE, params);
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
        WeakReference<IjkPlayerWrapper> ref;
        boolean isStop;

        ProgressHandler(IjkPlayerWrapper ijkPlayerWrapper) {
            super(Looper.getMainLooper());
            ref = new WeakReference<>(ijkPlayerWrapper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS: {
                    IjkPlayerWrapper player = ref.get();
                    if (player != null) {
                        player.dispatchEventProgress(player.getDuration(), player.getProgress(),
                                player.getCacheProgress());
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
                postDelayed(this, 1000);
            }
        }
    }
}
