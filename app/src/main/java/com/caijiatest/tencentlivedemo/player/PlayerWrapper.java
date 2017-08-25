package com.caijiatest.tencentlivedemo.player;

import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

/**
 * Created by cai.jia on 2017/8/23.
 */

public interface PlayerWrapper{

    void setSurface(Surface surface);

    void setDataSource(String uri);

    void prepare();

    void start();

    void stop();

    boolean canPlay();

    boolean isPlaying();

    void seekTo(int progress);

    void resume();

    void pause();

    void destroy();

    void startPlay(String uri);

    void setVideoDisplayMode(int displayMode);

    void setTextureView(TextureView textureView);

    void setOnPlayEventListener(OnPlayEventListener playEventListener);

    interface OnPlayEventListener{

        void onPlayEvent(int event, Bundle params);
    }
}
