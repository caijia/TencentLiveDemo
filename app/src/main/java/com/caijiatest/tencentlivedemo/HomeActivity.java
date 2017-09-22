package com.caijiatest.tencentlivedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.caijiatest.tencentlivedemo.util.MixStreamHelper;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupManager;

/**
 * Created by cai.jia on 2017/9/4 0004.
 */

public class HomeActivity extends AppCompatActivity {

    private static final String tag = "HomeActivity";
    private String groupId = "199141921";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            new MixStreamHelper().mixStream(null, "8768_c84790876c", "8768_c84790874b");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onController(View view) {
        Intent i = new Intent(this, LiveControllerAct.class);
        startActivity(i);
    }

    public void onLink(View view) {
        Intent i = new Intent(this, LiveLinkAct.class);
        startActivity(i);
    }

    public void onGuest(View view) {
        Intent i = new Intent(this, LiveGuestAct.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(tag, "delete group onError");
            }

            @Override
            public void onSuccess() {
                Log.e(tag, "delete group onSuccess");
            }
        });
    }
}
