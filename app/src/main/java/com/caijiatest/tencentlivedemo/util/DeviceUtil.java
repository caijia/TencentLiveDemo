package com.caijiatest.tencentlivedemo.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by cai.jia on 2017/9/5 0005.
 */

public class DeviceUtil {

    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dpToPx(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }
}
