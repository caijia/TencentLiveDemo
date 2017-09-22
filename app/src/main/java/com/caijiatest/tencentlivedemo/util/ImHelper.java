package com.caijiatest.tencentlivedemo.util;

import android.util.Log;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

/**
 * Created by cai.jia on 2017/9/5 0005.
 */

public class ImHelper {

    private static final String TAG = "tecent_im_log";
    private static volatile ImHelper instance = null;

    private ImHelper() {

    }

    public static ImHelper getInstance() {
        if (instance == null) {
            synchronized (ImHelper.class) {
                if (instance == null) {
                    instance = new ImHelper();
                }
            }
        }
        return instance;
    }

    public void sendTextMessage(String txtMsg, String toUser, TIMConversationType type) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        //添加文本内容
        TIMTextElem elem = new TIMTextElem();

        elem.setText(txtMsg);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

        TIMConversation conversation = TIMManager.getInstance().getConversation(type, toUser);

        //发送消息
        conversation.sendOnlineMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.d(TAG, "SendMsg ok");
            }
        });
    }
}
