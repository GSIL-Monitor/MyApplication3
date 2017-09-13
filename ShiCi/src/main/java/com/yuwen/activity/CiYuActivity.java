package com.yuwen.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yuwen.Entity.CiYu;
import com.yuwen.myapplication.R;
import com.yuwen.tool.DBHelper;
import com.yuwen.tool.DBOperate;
import com.yuwen.tool.PermissionHelper;

import java.util.List;


public class CiYuActivity extends AppCompatActivity {
    TextView tvTitle,tvPinyin,tvJieshi;
    private PermissionHelper mPermissionHelper;
    public static final String TAG = "AD-StandardNewsFeed";
    private final static String APP_POSITION_ID ="29bb635c614bdd0b8056a00cf74743d2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciyu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {

            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑

        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {

            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i("info", "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();

            }
        }
        tvTitle=(TextView)findViewById(R.id.ci_title);
        tvPinyin=(TextView)findViewById(R.id.ci_pinyin);
        tvJieshi=(TextView)findViewById(R.id.ci_jieshi);
        final ViewGroup container = (ViewGroup) findViewById(R.id.containerCi);

        Intent intent=this.getIntent();
        final CiYu ciYu=(CiYu) intent.getSerializableExtra("ciYu");
       // Log.i("info",ciYu.getName());
       // Log.i("info",ciYu.getContent());
        tvTitle.setText(ciYu.getName());
        tvPinyin.setText(ciYu.getContent().split("<br>")[0]);
        tvJieshi.setText(ciYu.getContent().split("<br>")[1]);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ciYuFb);
        fab.setOnClickListener(new View.OnClickListener() {
            DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {
                //添加收藏action
                DBHelper dbHelper=new DBHelper(CiYuActivity.this);
                dBOperate=new DBOperate(dbHelper);
                Gson gson = new Gson();
                String json=gson.toJson(ciYu);



                dBOperate.insert("2",ciYu.getName(),json);    //插入到数据库
                ConstraintLayout layout=(ConstraintLayout)findViewById(R.id.ciYuConstraintLayout);

                Snackbar.make(layout, "已收藏该词语", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(CiYuActivity.this,CollectActivity.class);
                        startActivity(intent);
                    }
                }).show();

            }
        });


    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }
}
