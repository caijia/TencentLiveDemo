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

/**
 * Created by cai.jia on 2017/9/4 0004.
 */

public class LiveLinkAct extends AppCompatActivity {

    private static final String tag = "tecent_im_log";
    private static final String identifier = "liveLink";
    private static final String userSig = "eJxFkE1Pg0AQQP8LV40uHwvFpAeyqVqDJqWtUS*bLQxlgC50WWiN8b*LG5pe38tk5s2PtYnXd6JtMeNCc1dl1oNFrFuD4dyiAi5yDWrENqXUIeRiB1AdNnIUDrGp7biEXCVmIDXmaAZrHCBGWU2uw-0IXxdbtmTL2Uv-nBxOm-eykOu3tmiGhtXy2BU7gO5G3X-s4zIXQfIU4SKiNetdtipT-zGWn7Q*K5Icw4iedmzlx1*Vhx5shRx6t5pflmUVN3n-Ad54oOOHwWySGg9gwojnhX7gBxMXadr0UnP93YL5x*8fkDFaEQ__";
    private String groupId = "199141921";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_guest);
    }

    public void joinRoom(View view) {
//        skipPlayUI();

        //im登录
        // identifier为用户名，userSig 为用户登录凭证
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
                joinR();
            }
        });
    }

    private void joinR() {
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
                skipPlayUI();
            }
        });
    }

    private void skipPlayUI() {
        Intent intent = TestLiveActivity.getIntent(this, TestLiveActivity.LIVE_LINK);
        startActivity(intent);
    }
}
