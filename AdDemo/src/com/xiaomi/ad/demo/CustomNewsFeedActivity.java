package com.xiaomi.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.CustomNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.xiaomi.ad.internal.CustomNewsFeedJson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangzheyuan on 16-6-13.
 */
public class CustomNewsFeedActivity extends Activity {
    public static final String TAG = "AD-CustomNewsFeed";

    private static final int TYPE_BIG_PIC = 0;
    private static final int TYPE_SMALL_PIC = 1;
    private static final int TYPE_GROUP_PIC = 2;

    //以下的POSITION_IDS 需要使用您申请的值替换下面内容
    private final static String[] APP_POSITION_IDS = {"2cae1a1f63f60185630f78a1d63923b0",
            "0c220d9bf7029e71461f247485696d07", "b38f454156852941f3883c736c79e7e1"};

    private final static String[] POSITION_NAMES = {"信息流大图", "信息流小图", "信息流组图"};
    private final static String[] LAYOUT_NAMES = {"大图布局", "小图布局", "组图布局"};

    private int mPositionIndex;
    private int mLayoutIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_news_feed_layout);
        final ViewGroup container = (ViewGroup) findViewById(R.id.container);

        final Spinner adTypeSpinner = (Spinner) findViewById(R.id.custom_ad_type);
        adTypeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, POSITION_NAMES));
        final Spinner adLayoutSpinner = (Spinner) findViewById(R.id.custom_layout_type);
        adLayoutSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, LAYOUT_NAMES));
        adTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPositionIndex = position;
                adLayoutSpinner.setSelection(mPositionIndex);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adLayoutSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLayoutIndex = position;
                adTypeSpinner.setSelection(mLayoutIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final CustomNewsFeedAd customNewsFeedAd = new CustomNewsFeedAd(this);
        Button fetchBtn = (Button) findViewById(R.id.btn_fetch);
        //在设定请求参数时，应确定广告位素材是何种布局，如果是特殊要求的app下载类广告，应通过setIsInstallApp来设定参数
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    customNewsFeedAd.requestAd(APP_POSITION_IDS[mPositionIndex], 1, new NativeAdListener() {
                        @Override
                        public void onNativeInfoFail(AdError adError) {
                            Log.e(TAG, "onNativeInfoFail e : " + adError);
                        }

                        @Override
                        public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                            final NativeAdInfoIndex adInfoResponse = list.get(0);
                            customNewsFeedAd.buildViewAsync(adInfoResponse, getExtra(), new AdListener() {
                                @Override
                                public void onAdError(AdError adError) {
                                    Log.e(TAG, "error : remove all views");
                                    container.removeAllViews();
                                }

                                @Override
                                public void onAdEvent(AdEvent adEvent) {
                                    // 目前考虑了下述情况：
                                    // 1.用户点击信息流广告（整个的范围内）
                                    // 2.用户点击dislike按钮(目前dislike按钮暂时不启用)
                                    // 3.信息流广告展示
                                    // 4.下载类广告中的下载按钮被点击
                                    if (adEvent.mType == AdEvent.TYPE_CLICK) {
                                        Log.d(TAG, "ad has been clicked!");
                                    } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                                        Log.d(TAG, "x button has been clicked!");
                                    } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                                        Log.d(TAG, "ad has been showed!");
                                    } else if (adEvent.mType == AdEvent.TYPE_APP_START_DOWNLOAD) {
                                        Log.d(TAG, "install button has been clicked");
                                    }
                                }

                                @Override
                                public void onAdLoaded() {

                                }

                                @Override
                                public void onViewCreated(View view) {
                                    Log.e(TAG, "onViewCreated " + view);
                                    container.removeAllViews();
                                    container.addView(view);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONObject getExtra() {
        JSONObject extra = null;
        if (LAYOUT_NAMES[mLayoutIndex].equals("大图布局")) {
            extra = requestBigPicAd(true);
        } else if (LAYOUT_NAMES[mLayoutIndex].equals("小图布局")) {
            extra = requestSmallPicAd(true);
        } else if (LAYOUT_NAMES[mLayoutIndex].equals("组图布局")) {
            extra = requestGroupPicAd(true);
        } else {
            Log.d(TAG, "layout type unknown!");
        }
        return extra;
    }

    private JSONObject requestBigPicAd(boolean isInstallApp) {
        try {
            CustomNewsFeedJson.Builder builder = new CustomNewsFeedJson.Builder(TYPE_BIG_PIC);
            ArrayList<Integer> idList = new ArrayList<Integer>();
            idList.add(R.id.big_image);
            CustomNewsFeedJson customNewsFeedJson = builder.setTitleId(R.id.title)//.setDislikeIcon(R.id.dislike_icon)
                    .setLayoutId(R.layout.custom_sample_big_ad_layout).setIsInstallApp(isInstallApp)
                    .setSumaryId(R.id.summary).setBigImageIds(idList).setPopularizeId(R.id.popularize)
                    //如果不是下载类广告，可以不用设置install id，但是如果想使用一套布局来适应比如大图布局下的下载类样式和默认样式，
                    //就可以传入install 的id，我们将设置它的可见性，实例中我们将用一套布局来适应2种样式。
                    .setInstallId(R.id.install).build();
            return customNewsFeedJson.toJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject requestSmallPicAd(boolean isInstallApp) {
        try {
            //这个例子中我们使用2套布局来分别处理下载类样式和默认样式
            CustomNewsFeedJson.Builder builder = new CustomNewsFeedJson.Builder(TYPE_SMALL_PIC);
            ArrayList<Integer> idList = new ArrayList<Integer>();
            CustomNewsFeedJson customNewsFeedJson = null;
            if (isInstallApp) {
                idList.add(R.id.icon);
                customNewsFeedJson = builder.setTitleId(R.id.title).setPopularizeId(R.id.popularize)
                        .setLayoutId(R.layout.custom_sample_small_app_ad_layout).setIsInstallApp(isInstallApp)
                        .setSumaryId(R.id.summary).setSmallImageIds(idList).setInstallId(R.id.install).build();
            } else {
                idList.add(R.id.small_pic);
                customNewsFeedJson = builder.setTitleId(R.id.title).setPopularizeId(R.id.popularize)
                        .setLayoutId(R.layout.custom_sample_small_ad_layout).setIsInstallApp(isInstallApp)
                        .setSumaryId(R.id.summary).setSmallImageIds(idList).build();
            }
            return customNewsFeedJson.toJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject requestGroupPicAd(boolean isInstallApp) {
        try {
            CustomNewsFeedJson.Builder builder = new CustomNewsFeedJson.Builder(TYPE_GROUP_PIC);
            ArrayList<Integer> idList = new ArrayList<Integer>();
            idList.add(R.id.image1);
            idList.add(R.id.image2);
            idList.add(R.id.image3);
            CustomNewsFeedJson customNewsFeedJson = builder.setTitleId(R.id.title)//.setDislikeIcon(R.id.dislike_icon)
                    .setLayoutId(R.layout.custom_sample_group_ad_layout).setIsInstallApp(isInstallApp).setPopularizeId(R.id.popularize)
                    .setSumaryId(R.id.summary).setGroupImageIds(idList).setInstallId(R.id.install).build();
            return customNewsFeedJson.toJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
