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

public class LiveGuestAct extends AppCompatActivity {

    private static final String tag = "tecent_im_log";
    private static final String identifier = "liveGuest";
    private static final String userSig = "eJxFkNtOg0AQQP*FV41ZlkvBxAdqWy61Cimo8WWzsLNmqVIuC5Ya-13clPh6TiYzZ7619GF-Q*taMEIlMVqm3WpIu1YYTrVogVAuoZ2wblkWRmi2A7SdOFaTwEi3dGwg9C8Fg0oKLtTghxjA76GTF9mJ94nu1tl9uNo69XDwcLKMB8N8bIC*JCl3k3HF*2AjaVgA5EH5lA*jJ9be*Wqz4zFPvt6istqeF4FTLYHl*66MojhssPfa*Omzn0Hm3M3L2IGovr8Cc7oQ2*7CuUgpPkGVIdN0bceeOS2KY19JIsca1EN*fgG0lFoO";
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
        Intent intent = TestLiveActivity.getIntent(this, TestLiveActivity.LIVE_GUEST);
        startActivity(intent);
    }
}
