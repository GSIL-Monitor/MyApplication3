package com.xiaomi.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.SplashAd;


/**
 * 您可以参考本类中的代码来接入小米游戏广告SDK中的开屏广告。在接入过程中，有如下事项需要注意：
 * 1.请将POSITION_ID值替换成您在小米开发者网站上申请的插播广告位。
 */
public class VerticalSplashAdActivity extends Activity {
    private static final String TAG = "VerticalSplash";
    //以下的POSITION_ID 需要使用您申请的值替换下面内容
    private static final String POSITION_ID = "b373ee903da0c6fc9c9da202df95a500";
    private ViewGroup mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashad);
        mContainer = (ViewGroup) findViewById(R.id.splash_ad_container);
        SplashAd splashAd = new SplashAd(this, mContainer, R.drawable.default_splash, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                // 开屏广告展示
                Log.d(TAG, "onAdPresent");
            }

            @Override
            public void onAdClick() {
                //用户点击了开屏广告
                Log.d(TAG, "onAdClick");
            }

            @Override
            public void onAdDismissed() {
                //这个方法被调用时，表示从开屏广告消失。
                Log.d(TAG, "onAdDismissed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "onAdFailed, message: " + s);
            }
        });
        splashAd.requestAd(POSITION_ID);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            if (mContainer.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}