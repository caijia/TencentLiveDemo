package com.caijiatest.tencentlivedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMValueCallBack;

/**
 * Created by cai.jia on 2017/9/4 0004.
 */

public class LiveControllerAct extends AppCompatActivity {

    private static final String tag = "tecent_im_log";
    private static final String identifier = "xiaojun";
    private static final String userSig = "eJxFkFFvgjAUhf8Lry6zFAqyxIc6ySBTs8R1Ib4QYi-uQoBKq1" +
            "OW-XcZwfj6fbk559xf63O1fc6UQplmJnVaab1YxHoaMFwUtpBmuYG2xzZjjBJyt2doNTZ1LyixmU0dQh4" +
            "SJdQGcxwOL5g1xakelcZDz9aheI0jFkO8XJTThDmL*COaJeF7Z7pDDlEh*KoqhF3tEm*jxZZjyI9y4hVS-O" +
            "jdVS3P02Sdf29qM4l4jKSDN-COvp-oMii-*PweJst0WPff3*37US-wZ6M0WMGwi7huwAJCR57t982pNqm5K" +
            "hje8XcDcyRYiA__";

    private String groupId = "199141921";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_controller);

        //im登录
    }

    /**
     * 1.创建im组
     * 2.拿到当前的obs推流的地址播放VR
     * 3.请求服务端拿到推流地址
     * 4.推流
     * @param view
     */
    public void createRoom(View view) {
        TIMManager.getInstance().login(identifier, userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.d(tag, "login failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                Log.d(tag, "login succ");
                createRoom();
            }
        });
    }

    public void createRoom() {
        //创建公开群，且不自定义群ID
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("AVChatRoom", "test_group");
        param.setGroupId(groupId);
        //指定群简介
        param.setIntroduction("hello world");
        //指定群公告
        param.setNotification("welcome to our group");

        //创建群组
        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Log.d(tag, "create group failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(String groupId) {
                Log.d(tag, "create group succ, groupId:" + groupId);
                LiveControllerAct.this.groupId = groupId;
//                joinRoom(groupId);

                skipPlayUI();
            }
        });
    }

    private void skipPlayUI() {
        Intent intent = TestLiveActivity.getIntent(this, TestLiveActivity.LIVE_MASTER);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void joinRoom(String groupId) {
        TIMGroupManager.getInstance().applyJoinGroup(groupId, "some reason", new TIMCallBack() {
            @java.lang.Override
            public void onError(int code, String desc) {
                //接口返回了错误码code和错误描述desc，可用于原因
                //错误码code列表请参见错误码表
                Log.e(tag, "disconnected");
            }

            @java.lang.Override
            public void onSuccess() {
                Log.i(tag, "join group");
            }
        });
    }
}
