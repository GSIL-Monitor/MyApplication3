package com.xiaomi.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

/**
 * 您可以参考本类中的代码来接入小米游戏广告SDK中的插播广告。在接入过程中，有如下事项需要注意：
 * 1.请将POSITION_ID值替换成您在小米开发者网站上申请的插播广告位。
 * 2.showInterstitialAd的调用和requestInterstitialAd 要配对。不能调用一次requestInterstitialAd之后，多次调用showInterstitialAd
 */
public class VerticalInterstitialActivity extends Activity {

    public static final String TAG = "VerticalInterstitial";
    //以下的POSITION_ID 需要使用您申请的值替换下面内容
    private static final String POSITION_ID = "67b05e7cc9533510d4b8d9d4d78d0ae9";

    private Button mPreCacheBtn;
    private Button mShowBtn;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interstitialad);

        mPreCacheBtn = (Button) this.findViewById(R.id.cache_btn);
        mShowBtn = (Button) this.findViewById(R.id.show_btn);

//        mInterstitialAd = new InterstitialAd(getApplicationContext(), VerticalInterstitialActivity.this);
        //在这里,mPlayBtn是作为一个锚点传入的，可以换成任意其他的view，比如getWindow().getDecorView()
        mInterstitialAd = new InterstitialAd(getApplicationContext(), mPreCacheBtn);
//        mInterstitialAd = new InterstitialAd(getApplicationContext(), getWindow().getDecorView());
        mPreCacheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mInterstitialAd.isReady()) {
                        Log.e(TAG, "ad has been cached");
                    } else {
                        mInterstitialAd.requestAd(POSITION_ID, new AdListener() {
                            @Override
                            public void onAdError(AdError adError) {
                                Log.e(TAG, "onAdError : " + adError.toString());
                                mShowBtn.setEnabled(false);
                            }

                            @Override
                            public void onAdEvent(AdEvent adEvent) {
                                try {
                                    switch (adEvent.mType) {
                                        case AdEvent.TYPE_SKIP:
                                            //用户关闭了广告
                                            Log.e(TAG, "ad skip!");
                                            break;
                                        case AdEvent.TYPE_CLICK:
                                            //用户点击了广告
                                            Log.e(TAG, "ad click!");
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onAdLoaded() {
                                Log.e(TAG, "ad is ready : " + mInterstitialAd.isReady());
                                mShowBtn.setEnabled(true);
                            }

                            @Override
                            public void onViewCreated(View view) {
                                //won't be invoked
                            }
                        });
                        Toast.makeText(getApplicationContext(), "加载中...", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//
        });

        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mInterstitialAd.isReady()) {
                        mShowBtn.setEnabled(false);
                        Log.e(TAG, "ad is not ready!");
                    } else {
                        mInterstitialAd.show();
                    }
                } catch (Exception e) {
                } finally {
                    //单次预缓存的广告无论结果只显示一次,请求新的广告需要再次调用预缓存接口
                    mShowBtn.setEnabled(false);
                }
            }
        });
    }
}