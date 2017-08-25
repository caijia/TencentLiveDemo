package com.caijiatest.tencentlivedemo.playController;

import android.view.ViewGroup;

import com.tencent.rtmp.TXLivePlayer;

/**
 * Created by cai.jia on 2017/8/22.
 */

public interface Controller {

    void setLivePlayer(TXLivePlayer txLivePlayer);

    /**
     * 全屏要把播放控件加到android.R.id.content的容器
     * 小屏要把播放控件原来容器所以记录原来容器
     * @param playerContainer 包裹播放控件和控制器的Layout 全屏时将其加入到android.R.id.content的容器
     * @param playerContainerParent playerContainer的父控件 缩回全屏时将playerContainer加入到此控件
     */
    void setFullScreenLayout(ViewGroup playerContainer, ViewGroup playerContainerParent);

    /**
     * 是否全屏
     * @return
     */
    boolean isFullScreen();

    /**
     * 全屏模式下会缩回全屏,非全屏会展开到全屏
     */
    void toggleFullScreen();

    /**
     * 开始调用播放,此时需要进度条等待,还没有收到开始播放的事件。后续的开始和暂停等事件通过播放器的回调处理
     */
    void startPlay();
}
