package com.caijiatest.tencentlivedemo.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by cai.jia on 2017/9/5 0005.
 */

public class MixStreamHelper {

    public String mixStream(Context context,String... subStream) throws Exception {
        JSONArray streamList = new JSONArray();
        int length = subStream.length;
        JSONObject layoutParams = new JSONObject();
        layoutParams.put("image_layer", 1);
        layoutParams.put("color", "0x20FFFFFF");
        layoutParams.put("input_type", 3);
        layoutParams.put("image_width", 500);
        layoutParams.put("image_height", 240);
        JSONObject jo = new JSONObject();
        jo.put("input_stream_id", subStream[0]);
        jo.put("layout_params", layoutParams);
        streamList.put(jo);

        for (int i = 0; i < length; i++) {
            JSONObject subParams = new JSONObject();
            subParams.put("image_layer", i + 2);
            subParams.put("image_width", 240);
            subParams.put("image_height",240);
            subParams.put("location_x", 260 * i);
            subParams.put("location_y", 0);

            JSONObject jo1 = new JSONObject();
            jo1.put("input_stream_id", subStream[i]);
            jo1.put("layout_params", subParams);
            streamList.put(jo1);

        }

        JSONObject para = new JSONObject();
        para.put("app_id", "1253440624"); //填写直播APPID
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", subStream[0]);  //# 填大主播的流ID
        para.put("output_stream_id", subStream[0]);
        para.put("input_stream_list", streamList);

        JSONObject interces = new JSONObject();
        interces.put("interfaceName", "Mix_StreamV2");
        interces.put("para", para);

        int time = (int) (new Date().getTime() / 1000);
        Log.d("mix", "time=" + time);
        JSONObject jo2 = new JSONObject();
        jo2.put("timestamp", time);
        jo2.put("eventId", time);
        jo2.put("interface", interces);

        String strPost = jo2.toString();
        Log.d("mix", "str_post=" + strPost);


        // str_get
        String key = "672abdaaf0fe6fc91156510c2bac8932";
        int expires = time + 60;
        String sign = MD5.getMD5(key + expires).toLowerCase();
        Map<String,Object> getParams = new LinkedHashMap<>();
        getParams.put("cmd", "1253440624");
        getParams.put("interface", "Mix_StreamV2");
        getParams.put("t", expires);
        getParams.put("sign", sign);

        String getUrl = getUrl(getParams);
        Log.d("mix", getUrl);
        URL url = new URL(getUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream outputStream = new DataOutputStream(conn.getOutputStream());
        outputStream.write(strPost.getBytes("utf-8"));
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            return FileUtil.streamToString(in);
        }
        return "";
    }

    public void asyncMixStream(final Context context, final String... subStream) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return mixStream(context,subStream);
            }
        };
        new ThreadSwitchHelper<String>().task(callable).execute(new ThreadSwitchHelper.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("mix", "mix stream onSuccess " + result);
            }

            @Override
            public void onFailure(String error) {
                Log.d("mix", "mix stream onFailure ");
            }
        });
    }

    private String getUrl(Map<String,Object> params) {
        String getUrl = "http://fcgi.video.qcloud.com/common_access?";
        StringBuilder sb = new StringBuilder(getUrl);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
