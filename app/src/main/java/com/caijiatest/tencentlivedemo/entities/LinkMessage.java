package com.caijiatest.tencentlivedemo.entities;

/**
 * Created by cai.jia on 2017/9/4 0004.
 */

public class LinkMessage {

    private String linkPlayUrl;
    private String linkName;

    public LinkMessage(String linkPlayUrl, String linkName) {
        this.linkPlayUrl = linkPlayUrl;
        this.linkName = linkName;
    }

    public String getLinkPlayUrl() {
        return linkPlayUrl;
    }

    public void setLinkPlayUrl(String linkPlayUrl) {
        this.linkPlayUrl = linkPlayUrl;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
}
