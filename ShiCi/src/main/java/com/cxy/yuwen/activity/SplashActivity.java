package com.cxy.yuwen.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;

import ddd.eee.fff.AdManager;
import ddd.eee.fff.nm.cm.ErrorCode;
import ddd.eee.fff.nm.sp.SplashViewSettings;
import ddd.eee.fff.nm.sp.SpotListener;
import ddd.eee.fff.nm.sp.SpotManager;


public class SplashActivity extends AppCompatActivity {
    private static final String YOUMI_APPID="a8a6ea3c54813bc2";
    private static final String YOUMI_APPSECRET="a94b2fa629d71895";
    private static final String TAG = "com.example.yuwen";
    private static final String YOUMI_AD_TAG="youmi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 移除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        MyApplication.getInstance().addActivity(this);

        //初始化有米广告sdk
        AdManager.getInstance(this).init(YOUMI_APPID, YOUMI_APPSECRET, true);

        setupSplashAd();
    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
     /*   RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.view_divider);*/

        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //		splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(this)
                .showSplash(this, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        Log.i(YOUMI_AD_TAG,"开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        Log.e(YOUMI_AD_TAG,"开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                               Log.e(YOUMI_AD_TAG,"网络异常");
                                break;
                            case ErrorCode.NON_AD:
                               Log.e(YOUMI_AD_TAG,"暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                               Log.e(YOUMI_AD_TAG,"开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                               Log.e(YOUMI_AD_TAG,"开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                               Log.e(YOUMI_AD_TAG,"开屏控件处在不可见状态");
                                break;
                            default:
                               Log.e(YOUMI_AD_TAG,"errorCode:" +errorCode);
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.d(YOUMI_AD_TAG,"开屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        Log.d(YOUMI_AD_TAG,"开屏被点击");
                        Log.i(YOUMI_AD_TAG,isWebPage ? "是" : "不是"+"是否是网页广告？");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(this).onDestroy();
    }




}
