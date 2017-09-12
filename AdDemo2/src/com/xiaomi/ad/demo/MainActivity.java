package com.xiaomi.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 您可以参考本类中的代码来接入小米游戏广告SDK中的开屏广告。在接入过程中，有如下事项需要注意：
 * 1.请将SPLASH_POSITION_ID值替换成您在小米开发者网站上申请的开屏广告位。
 */
public class MainActivity extends Activity {

    public static final String TAG = "AD-SplashAd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.vertical_splash).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, VerticalSplashAdActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.horizontal_splash).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HorizonSplashAdActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.vertical_interstitial).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入插屏广告示例Activity
                Intent i = new Intent(MainActivity.this, VerticalInterstitialActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.horizontal_interstitial).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入插屏广告示例Activity
                Intent i = new Intent(MainActivity.this, HorizonInterstitialActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.news_feed_ad).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CustomNewsFeedActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.standard_news_feed_ad).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StandardNewsFeedActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.standard_news_feed_list_ad).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StandardNewsFeedListActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.banner).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BannerActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

}
