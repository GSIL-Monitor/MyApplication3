package com.example.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.myapp.tool.Chengyu;
import com.myapp.tool.PermissionHelper;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.util.List;

public class ChengyuActivity extends BasicActivity {
    public static final String TAG = "AD-StandardNewsFeed";

    //for app
    private final static String APP_POSITION_ID = "babc24ad9259219380f42c1d625a49d5";
   // static final String H5_POS_ID = "1dc7afe1f217bc278ad6b1a1914c60ae";
    TextView nametv,pinyintv,jiehsitv,fromtv,exampletv,yufatv,yinzhengtv,tongyitv,fanyitv;
    private PermissionHelper mPermissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengyu);
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(TAG, "All of requested permissions has been granted, so run app logic.");
                //  AdManager.getInstance(MainActivity.this).init(appId, appSecret,false, true);
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
            //AdManager.getInstance(MainActivity.this).init(appId, appSecret,false, true);
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
                //  AdManager.getInstance(MainActivity.this).init(appId, appSecret,false, true);
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();

            }
        }

        nametv=(TextView)findViewById(R.id.chengyuTitle);
        pinyintv=(TextView)findViewById(R.id.pinyin);
        jiehsitv=(TextView)findViewById(R.id.jieshi);
        fromtv=(TextView) findViewById(R.id.from);
        exampletv=(TextView)findViewById(R.id.example);
        yufatv=(TextView)findViewById(R.id.yufa);
        yinzhengtv=(TextView) findViewById(R.id.yinzheng);
        tongyitv=(TextView) findViewById(R.id.tongyi);
        fanyitv=(TextView)findViewById(R.id.fanyi);

        final ViewGroup container = (ViewGroup) findViewById(R.id.container2);

        Intent intent=this.getIntent();
        Chengyu chengyu=(Chengyu) intent.getSerializableExtra("chengyu");

        nametv.setText(chengyu.getName());
        pinyintv.setText(chengyu.getPinyin());
        jiehsitv.setText(chengyu.getJieshi());
        fromtv.setText(chengyu.getFrom());
        exampletv.setText(chengyu.getExample());
        yufatv.setText(chengyu.getYufa());
        yinzhengtv.setText(chengyu.getYinzheng());
        tongyitv.setText(chengyu.getTongyi());
        fanyitv.setText(chengyu.getFanyi());
        final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);
        container.post(new Runnable() {
            @Override
            public void run() {
                try {
                    standardNewsFeedAd.requestAd(APP_POSITION_ID, 1, new NativeAdListener() {
                        @Override
                        public void onNativeInfoFail(AdError adError) {
                            Log.e(TAG, "onNativeInfoFail e : " + adError);
                        }

                        @Override
                        public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                            NativeAdInfoIndex response = list.get(0);
                            standardNewsFeedAd.buildViewAsync(response, container.getWidth(), new AdListener() {
                                @Override
                                public void onAdError(AdError adError) {
                                    Log.e(TAG, "error : remove all views");
                                    container.removeAllViews();
                                }

                                @Override
                                public void onAdEvent(AdEvent adEvent) {
                                    //目前考虑了３种情况，用户点击信息流广告，用户点击x按钮，以及信息流展示的３种回调，范例如下
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

                                }

                                @Override
                                public void onViewCreated(View view) {
                                    Log.e(TAG, "onViewCreated");
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
    /* });
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }
}
