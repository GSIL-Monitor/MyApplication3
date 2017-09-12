package com.xiaomi.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.H5BannerAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

//import static javafx.scene.input.KeyCode.R;

/**
 * Created by fangzheyuan on 16-7-13.
 */
public class BannerActivity extends Activity {

    public static final String TAG = "AD-BannerActivity";

    static final String STAGE_UNION_BANNER_API_NATIVE_SMALL_H5_POS_ID = "1dc7afe1f217bc278ad6b1a1914c60ae";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        final ViewGroup container = (ViewGroup) findViewById(R.id.container);

        final Button fetchBtn = (Button) findViewById(R.id.fetchAd);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e(TAG, "----------------");
                    final H5BannerAd h5BannerAd = new H5BannerAd(getApplicationContext());
                    h5BannerAd.requestAd(STAGE_UNION_BANNER_API_NATIVE_SMALL_H5_POS_ID, container.getWidth(), new AdListener() {
                        @Override
                        public void onAdError(AdError adError) {
                            Log.e(TAG, "onAdError e : " + adError);
                            container.removeAllViews();
                        }

                        @Override
                        public void onAdEvent(AdEvent adEvent) {
                            if (adEvent.mType == AdEvent.TYPE_CLICK) {
                                Log.d(TAG, "ad has been clicked!");
                            } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                                Log.d(TAG, "x button has been clicked!");
                            } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                                Log.d(TAG, "ad has been showed!");
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            Log.e(TAG, "ad is ready");
                        }

                        @Override
                        public void onViewCreated(View view) {
                            Log.e(TAG, "onViewCreated");
                            container.removeAllViews();
                            container.addView(view);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }//
            }
        });//
    }
}
