package com.caijiatest.tencentlivedemo.playController.entities;

/**
 * 视频清晰度
 * Created by cai.jia on 2017/9/18 0018.
 */

public class VideoQuality {

    /**
     * 标清,高清,超清描述
     */
    private String desc;

    /**
     * 视频清晰度地址
     */
    private String url;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
