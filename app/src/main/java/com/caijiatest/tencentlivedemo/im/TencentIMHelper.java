package com.caijiatest.tencentlivedemo.im;

import android.content.Context;

import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.group.TIMGroupAssistantListener;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.tencent.imsdk.ext.sns.TIMFriendshipProxyListener;
import com.tencent.imsdk.ext.sns.TIMUserConfigSnsExt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cai.jia on 2017/9/12 0012.
 */

public class TencentIMHelper {

    private static final int TENCENT_IM_APP_ID = 1400026978;
    private static final String TAG = "tencent_im_log";

    private Set<TIMUserStatusListener> userStatusListeners;
    private Set<TIMConnListener> connListeners;
    private Set<TIMGroupEventListener> groupEventListeners;
    private Set<TIMRefreshListener> refreshListeners;
    private Set<TIMFriendshipProxyListener> friendshipProxyListeners;
    private Set<TIMGroupAssistantListener> groupAssistantListeners;
    private Set<TIMMessageListener> messageListeners;
    private TencentIMUserManager userManager;
    private TencentConversationManager conversationManager;
    private TencentGroupManager groupManager;

    /**
     * 加入用户状态变更事件监听器
     * @param listener
     */
    public void addTIMUserStatusListener(TIMUserStatusListener listener) {
        if (listener != null && userStatusListeners != null) {
            userStatusListeners.add(listener);
        }
    }

    /**
     * 移除用户状态变更事件监听器，如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMUserStatusListener(TIMUserStatusListener listener) {
        if (listener != null && userStatusListeners != null) {
            userStatusListeners.remove(listener);
        }
    }

    /**
     * 加入连接状态事件监听器
     * @param listener
     */
    public void addTIMConnListener(TIMConnListener listener) {
        if (listener != null && connListeners != null) {
            connListeners.add(listener);
        }
    }

    /**
     * 移除连接状态事件监听器 如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMConnListener(TIMConnListener listener) {
        if (listener != null && connListeners != null) {
            connListeners.remove(listener);
        }
    }

    /**
     * 加入群组事件监听器
     * @param listener
     */
    public void addTIMGroupEventListener(TIMGroupEventListener listener) {
        if (listener != null && groupEventListeners != null) {
            groupEventListeners.add(listener);
        }
    }

    /**
     * 移除群组事件监听器 如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMGroupEventListener(TIMGroupEventListener listener) {
        if (listener != null && groupEventListeners != null) {
            groupEventListeners.remove(listener);
        }
    }

    /**
     * 加入会话刷新监听器
     * @param listener
     */
    public void addTIMRefreshListener(TIMRefreshListener listener) {
        if (listener != null && refreshListeners != null) {
            refreshListeners.add(listener);
        }
    }

    /**
     * 移除会话刷新监听器 如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMRefreshListener(TIMRefreshListener listener) {
        if (listener != null && refreshListeners != null) {
            refreshListeners.remove(listener);
        }
    }

    /**
     * 加入关系链变更事件监听器
     * @param listener
     */
    public void addTIMFriendshipProxyListener(TIMFriendshipProxyListener listener) {
        if (listener != null && friendshipProxyListeners != null) {
            friendshipProxyListeners.add(listener);
        }
    }

    /**
     * 移除关系链变更事件监听器 如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMFriendshipProxyListener(TIMFriendshipProxyListener listener) {
        if (listener != null && friendshipProxyListeners != null) {
            friendshipProxyListeners.remove(listener);
        }
    }

    /**
     * 加入群组资料变更事件监听器
     * @param listener
     */
    public void addTIMGroupAssistantListener(TIMGroupAssistantListener listener) {
        if (listener != null && groupAssistantListeners != null) {
            groupAssistantListeners.add(listener);
        }
    }

    /**
     * 移除群组资料变更事件监听器 如果加入了监听器当组件销毁的时候务必调用，否则会发生内存泄漏
     * @param listener
     */
    public void removeTIMGroupAssistantListener(TIMGroupAssistantListener listener) {
        if (listener != null && groupAssistantListeners != null) {
            groupAssistantListeners.remove(listener);
        }
    }

    /**
     * 加入收发事件监听器
     * @param listener
     */
    public void addTIMMessageListener(TIMMessageListener listener) {
        if (listener != null && messageListeners != null) {
            messageListeners.add(listener);
        }
    }

    /**
     * 移除收发事件监听器
     * @param listener
     */
    public void removeTIMMessageListener(TIMMessageListener listener) {
        if (listener != null && messageListeners != null) {
            messageListeners.remove(listener);
        }
    }

    /**
     * 应用销毁时调用
     */
    public void destroy() {
        if (userStatusListeners != null && !userStatusListeners.isEmpty()) {
            userStatusListeners.clear();
        }

        if (connListeners != null && !connListeners.isEmpty()) {
            connListeners.clear();
        }

        if (groupEventListeners != null && !groupEventListeners.isEmpty()) {
            groupEventListeners.clear();
        }

        if (refreshListeners != null && !refreshListeners.isEmpty()) {
            refreshListeners.clear();
        }

        if (friendshipProxyListeners != null && !friendshipProxyListeners.isEmpty()) {
            friendshipProxyListeners.clear();
        }

        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
            groupAssistantListeners.clear();
        }

        if (messageListeners != null && !messageListeners.isEmpty()) {
            messageListeners.clear();
        }
    }

    public TencentIMUserManager getIMUserManager() {
        return userManager;
    }

    public TencentConversationManager getConversationManager() {
        return conversationManager;
    }

    public TencentGroupManager getIMGroupManager() {
        return groupManager;
    }

    public void init(Context context, int appId) {
        userStatusListeners = new HashSet<>();
        connListeners = new HashSet<>();
        groupEventListeners = new HashSet<>();
        refreshListeners = new HashSet<>();
        friendshipProxyListeners = new HashSet<>();
        groupAssistantListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        userManager = new TencentIMUserManager();
        conversationManager = new TencentConversationManager();
        groupManager = new TencentGroupManager();

        //初始化SDK基本配置
        TIMSdkConfig config = new TIMSdkConfig(appId)
                .enableCrashReport(false)
                .enableLogPrint(false);

        //初始化SDK
        TIMManager.getInstance().init(context, config);

        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        if (userStatusListeners != null&& !userStatusListeners.isEmpty()) {
                            for (TIMUserStatusListener listener : userStatusListeners) {
                                listener.onForceOffline();
                            }
                        }
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新userSig重新登录SDK
                        if (userStatusListeners != null&& !userStatusListeners.isEmpty()) {
                            for (TIMUserStatusListener listener : userStatusListeners) {
                                listener.onUserSigExpired();
                            }
                        }
                    }
                })

                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        if (connListeners != null&& !connListeners.isEmpty()) {
                            for (TIMConnListener listener : connListeners) {
                                listener.onConnected();
                            }
                        }
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        if (connListeners != null&& !connListeners.isEmpty()) {
                            for (TIMConnListener listener : connListeners) {
                                listener.onDisconnected(code,desc);
                            }
                        }
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        if (connListeners != null && !connListeners.isEmpty()) {
                            for (TIMConnListener listener : connListeners) {
                                listener.onWifiNeedAuth(name);
                            }
                        }
                    }
                })

                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                        if (groupEventListeners != null && !groupEventListeners.isEmpty()) {
                            for (TIMGroupEventListener listener : groupEventListeners) {
                                listener.onGroupTipsEvent(elem);
                            }
                        }
                    }
                })

                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (refreshListeners != null & !refreshListeners.isEmpty()) {
                            for (TIMRefreshListener listener : refreshListeners) {
                                listener.onRefresh();
                            }
                        }
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        if (refreshListeners != null & !refreshListeners.isEmpty()) {
                            for (TIMRefreshListener listener : refreshListeners) {
                                listener.onRefreshConversation(conversations);
                            }
                        }
                    }
                });

        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //禁用消息存储
                .enableStorage(false)
                //禁用最近联系人漫游
                .enableRecentContact(false)
                //开启消息已读回执
                .enableReadReceipt(false);

        //资料关系链扩展用户配置
        userConfig = new TIMUserConfigSnsExt(userConfig)
                //开启资料关系链本地存储
                .enableFriendshipStorage(false)
                //设置关系链变更事件监听器
                .setFriendshipProxyListener(new TIMFriendshipProxyListener() {
                    @Override
                    public void OnAddFriends(List<TIMUserProfile> users) {
                        if (friendshipProxyListeners != null && !friendshipProxyListeners.isEmpty()) {
                            for (TIMFriendshipProxyListener listener : friendshipProxyListeners) {
                                listener.OnAddFriends(users);
                            }
                        }
                    }

                    @Override
                    public void OnDelFriends(List<String> identifiers) {
                        if (friendshipProxyListeners != null && !friendshipProxyListeners.isEmpty()) {
                            for (TIMFriendshipProxyListener listener : friendshipProxyListeners) {
                                listener.OnDelFriends(identifiers);
                            }
                        }
                    }

                    @Override
                    public void OnFriendProfileUpdate(List<TIMUserProfile> profiles) {
                        if (friendshipProxyListeners != null && !friendshipProxyListeners.isEmpty()) {
                            for (TIMFriendshipProxyListener listener : friendshipProxyListeners) {
                                listener.OnFriendProfileUpdate(profiles);
                            }
                        }
                    }

                    @Override
                    public void OnAddFriendReqs(List<TIMSNSChangeInfo> reqs) {
                        if (friendshipProxyListeners != null && !friendshipProxyListeners.isEmpty()) {
                            for (TIMFriendshipProxyListener listener : friendshipProxyListeners) {
                                listener.OnAddFriendReqs(reqs);
                            }
                        }
                    }
                });

        //群组管理扩展用户配置
        userConfig = new TIMUserConfigGroupExt(userConfig)
                //开启群组资料本地存储
                .enableGroupStorage(false)
                //设置群组资料变更事件监听器
                .setGroupAssistantListener(new TIMGroupAssistantListener() {
                    @Override
                    public void onMemberJoin(String groupId, List<TIMGroupMemberInfo> memberInfos) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onMemberJoin(groupId, memberInfos);
                            }
                        }
                    }

                    @Override
                    public void onMemberQuit(String groupId, List<String> members) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onMemberQuit(groupId, members);
                            }
                        }
                    }

                    @Override
                    public void onMemberUpdate(String groupId, List<TIMGroupMemberInfo> memberInfos) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onMemberUpdate(groupId, memberInfos);
                            }
                        }
                    }

                    @Override
                    public void onGroupAdd(TIMGroupCacheInfo groupCacheInfo) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onGroupAdd(groupCacheInfo);
                            }
                        }
                    }

                    @Override
                    public void onGroupDelete(String groupId) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onGroupDelete(groupId);
                            }
                        }
                    }

                    @Override
                    public void onGroupUpdate(TIMGroupCacheInfo groupCacheInfo) {
                        if (groupAssistantListeners != null && !groupAssistantListeners.isEmpty()) {
                            for (TIMGroupAssistantListener listener : groupAssistantListeners) {
                                listener.onGroupUpdate(groupCacheInfo);
                            }
                        }
                    }
                });

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                if (messageListeners != null && !messageListeners.isEmpty()) {
                    for (TIMMessageListener messageListener : messageListeners) {
                        messageListener.onNewMessages(list);
                    }
                }
                return true; //返回true将终止回调链，不再调用下一个新消息监听器
            }
        });
    }
}
