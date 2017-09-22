package com.caijiatest.tencentlivedemo.entities;

import com.tencent.imsdk.TIMMessage;

import java.util.List;

/**
 * Created by cai.jia on 2017/9/4 0004.
 */

public class MessageEvent {

    public List<TIMMessage> list;

    public MessageEvent(List<TIMMessage> list) {
        this.list = list;
    }
}
