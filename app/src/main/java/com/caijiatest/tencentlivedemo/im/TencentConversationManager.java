package com.caijiatest.tencentlivedemo.im;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;

/**
 * Created by cai.jia on 2017/9/12 0012.
 */

public class TencentConversationManager {

    public TIMConversationExt getConversation(TIMConversationType type, String peer) {
        return new TIMConversationExt(TIMManager.getInstance().getConversation(type, peer));
    }

    public void disableStorage(TIMConversationExt conversationExt) {
        if (conversationExt != null) {
            conversationExt.disableStorage();
        }
    }

    public void sendText(TIMConversation conversation,String text,
                         TIMValueCallBack<TIMMessage> callBack) {
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(text);
        if(msg.addElement(elem) != 0) {
            return;
        }
        conversation.sendMessage(msg,callBack);
    }

    public void sendOnlineText(TIMConversation conversation,String text,
                               TIMValueCallBack<TIMMessage> callBack) {
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(text);
        if(msg.addElement(elem) != 0) {
            return;
        }
        conversation.sendOnlineMessage(msg,callBack);
    }
}
