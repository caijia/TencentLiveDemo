package com.caijiatest.tencentlivedemo.im;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;

/**
 * Created by cai.jia on 2017/9/12 0012.
 */

public class TencentIMUserManager {

    public void login(@NonNull String identifier, @NonNull String userSig, @NonNull TIMCallBack callback) {
        String loginUser = getLoginUser();
        //当前要登录的账号已经登录
        if (TextUtils.equals(loginUser, userSig)) {
            callback.onSuccess();
            return;
        }
        TIMManager.getInstance().login(identifier, userSig, callback);
    }

    public void logout(@Nullable TIMCallBack callback) {
        TIMManager.getInstance().logout(callback);
    }

    public String getLoginUser() {
        return TIMManager.getInstance().getLoginUser();
    }
}
