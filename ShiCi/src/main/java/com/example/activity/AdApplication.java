package com.example.activity;

/**
 * Created by cxy on 2017/4/18.
 */

import android.app.Application;

import com.xiaomi.ad.AdSdk;

/**
 * 您可以参考本类中的代码来接入小米游戏广告SDK。在接入过程中，有如下事项需要注意：
 * 1.请将 APP_ID 值替换成您在小米开发者网站上申请的 AppID。
 * 2.调试广告时，需要调用 AdSdk.setMockOn()；正式发布时，请勿调用 AdSdk.setMockOn()
 */
public class AdApplication extends Application {
    //请注意，千万要把以下的 APP_ID 替换成您在小米开发者网站上申请的 AppID。否则，可能会影响你的应用广告收益。
    private static final String APP_ID = "2882303761517566012";

    @Override
    public void onCreate() {
        super.onCreate();
        AdSdk.setDebugOn();
       // AdSdk.setMockOn();
        AdSdk.initialize(this, APP_ID);
    }

}
