package com.caijiatest.tencentlivedemo;

import android.app.Service;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Surface;

import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class MainActivity extends AppCompatActivity {

    TXCloudVideoView mCaptureView;
    TXLivePusher mLivePusher;
    TXLivePushConfig mLivePushConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePusher.setConfig(mLivePushConfig);

        String rtmpUrl = "rtmp://8768.livepush.myqcloud.com/live/8768_0b657a5ca2?bizid=8768&txSecret=57c8aaa80278ed3edd2283dac2a2d899&txTime=599B037F";
        mLivePusher.startPusher(rtmpUrl);

        mCaptureView = (TXCloudVideoView) findViewById(R.id.video_view);
        mLivePusher.startCameraPreview(mCaptureView);

        //设定清晰度
        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, true, false);
        //美颜
        mLivePusher.setBeautyFilter(7, 3);

        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //主播离开时的默认图片
        mLivePushConfig.setPauseImg(300,5);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);

        //切换摄像头
//        if (mLivePusher.isPushing()) {
//            mLivePusher.switchCamera();
//        }
//        mLivePushConfig.setFrontCamera(mFrontCamera);
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mLivePusher != null) mLivePusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mLivePusher != null) mLivePusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mLivePusher != null) mLivePusher.resumePusher();
                    break;
            }
        }
    };

    // activity 的 onStop 生命周期函数
    @Override
    public void onStop(){
        super.onStop();
        mCaptureView.onPause();  // mCaptureView 是摄像头的图像渲染view
        mLivePusher.pausePusher(); // 通知 SDK 进入“后台推流模式”了
    }

    @Override
    public void onResume() {
        super.onResume();
        mCaptureView.onResume();     // mCaptureView 是摄像头的图像渲染view
        mLivePusher.resumePusher();  // 通知 SDK 重回前台推流
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束推流，注意做好清理工作
        stopRtmpPublish();
    }

    public void stopRtmpPublish() {
        mLivePusher.stopCameraPreview(true); //停止摄像头预览
        mLivePusher.stopPusher();            //停止推流
        mLivePusher.setPushListener(null);   //解绑 listener
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        onActivityRotation();
        super.onConfigurationChanged(newConfig);
    }

    protected void onActivityRotation()
    {
        // 自动旋转打开，Activity随手机方向旋转之后，需要改变推流方向
        int mobileRotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
        boolean screenCaptureLandscape = false;
        switch (mobileRotation) {
            case Surface.ROTATION_0:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
                break;
            case Surface.ROTATION_90:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
                screenCaptureLandscape = true;
                break;
            case Surface.ROTATION_270:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_LEFT;
                screenCaptureLandscape = true;
                break;
            default:
                break;
        }
        mLivePusher.setRenderRotation(0); //因为activity也旋转了，本地渲染相对正方向的角度为0。
        mLivePushConfig.setHomeOrientation(pushRotation);
        if (mLivePusher.isPushing()) {
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.stopCameraPreview(false);
            mLivePusher.startCameraPreview(mCaptureView);
        }
    }
}
