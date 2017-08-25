package com.caijiatest.tencentlivedemo.playController;

import android.view.ViewGroup;

import com.caijiatest.tencentlivedemo.player.IjkPlayerWrapper;

/**
 * Created by cai.jia on 2017/8/23.
 */

public interface IjkPlayController {

    void setPlayer(IjkPlayerWrapper player);

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
     * 控制器的一些UI回到原始状态
     */
    void release();

}
