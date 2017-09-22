package com.caijiatest.tencentlivedemo.im;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.imsdk.ext.group.TIMGroupSelfInfo;

/**
 * Created by cai.jia on 2017/9/12 0012.
 */

public class TencentGroupManager {

    /**
     * 创建直播聊天室
     *
     * @param roomId   聊天室id
     * @param roomName 聊天室名称
     * @param intro    聊天室简介
     * @param callBack
     */
    public void createLiveRoom(final String roomId, final String roomName, final String intro,
                               final TIMValueCallBack<String> callBack) {
        isJoinRoom(roomId, new IsJoinRoomListener() {
            @Override
            public void onIsJoinRoom(boolean isJoinRoom, boolean isSelfRoom) {
                if (!isJoinRoom) {
                    //AVChatRoom 直播群人数无限制
                    TIMGroupManager.CreateGroupParam param = new TIMGroupManager
                            .CreateGroupParam("AVChatRoom", roomName);
                    param.setGroupId(roomId);
                    //指定群简介
                    param.setIntroduction(intro);
                    //创建群组
                    TIMGroupManager.getInstance().createGroup(param, callBack);
                }

                //自己的群创建成功
                if (isSelfRoom && callBack != null) {
                    callBack.onSuccess(roomId);
                }
            }

            @Override
            public void onError(int code, String desc) {
                if (callBack != null) {
                    callBack.onError(code, desc);
                }
            }
        });
    }

    /**
     * 加入聊天室
     *
     * @param roomId   聊天室id
     * @param callBack
     */
    public void joinLiveRoom(final String roomId, final TIMCallBack callBack) {
        isJoinRoom(roomId, new IsJoinRoomListener() {
            @Override
            public void onIsJoinRoom(boolean isJoinRoom, boolean isSelfRoom) {
                if (!isJoinRoom) {
                    TIMGroupManager.getInstance().applyJoinGroup(roomId, "", callBack);

                }else{
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int code, String desc) {
                if (callBack != null) {
                    callBack.onError(code, desc);
                }
            }
        });
    }

    /**
     * 退出聊天室
     * 聊天室和直播大群，群主不能退出
     *
     * @param roomId 聊天室id
     * @param cb
     */
    public void quitRoom(String roomId, TIMCallBack cb) {
        TIMGroupManager.getInstance().quitGroup(roomId, cb);
    }

    /**
     * 解散聊天室
     * 聊天室和直播大群，群主可以解散群组
     *
     * @param roomId 聊天室id
     * @param cb
     */
    public void deleteGroup(String roomId, TIMCallBack cb) {
        TIMGroupManager.getInstance().deleteGroup(roomId, cb);
    }

    /**
     * 是否已经加入聊天室
     *
     * @param roomId 聊天室id
     * @param listener
     * @return
     */
    private void isJoinRoom(final String roomId, final IsJoinRoomListener listener) {
        //获取自己在群中的信息
        TIMGroupManagerExt.getInstance().getSelfInfo(roomId, new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int code, String desc) {
                if (listener == null) {
                    return;
                }

                switch (code) {
                    case ROOM_NOT_EXISTS:
                        listener.onIsJoinRoom(false, false);
                        break;

                    default:
                        listener.onError(code, desc);
                        break;
                }
            }

            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                TIMGroupMemberRoleType role = timGroupSelfInfo.getRole();
                boolean isSelfRoom = TIMGroupMemberRoleType.Owner == role;
                if (listener != null) {
                    listener.onIsJoinRoom(true, isSelfRoom);
                }
            }
        });
    }

    /**
     * 群组不存在，或者曾经存在过，但是目前已经被解散。
     */
    private static final int ROOM_NOT_EXISTS = 10010;

    private interface IsJoinRoomListener {

        /**
         * 是否加入群
         *
         * @param isJoinRoom 是否加入聊天室
         * @param isSelfRoom 是否自己创建的群
         */
        void onIsJoinRoom(boolean isJoinRoom, boolean isSelfRoom);

        void onError(int code, String desc);
    }
}
