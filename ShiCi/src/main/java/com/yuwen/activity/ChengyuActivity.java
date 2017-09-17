package com.yuwen.activity;

import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yuwen.BmobBean.Collect;
import com.yuwen.BmobBean.User;
import com.yuwen.Entity.Chengyu;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.DBOperate;
import com.yuwen.tool.PermissionHelper;
import com.yuwen.tool.Util;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ChengyuActivity extends BasicActivity {
    public static final String TAG = "AD-StandardNewsFeed";
    public static final String TAG2 = "AD-StandardFeed";
    //for app
    private final static String APP_POSITION_ID = "babc24ad9259219380f42c1d625a49d5";
    private static final String CHAPING_ID = "75e8e9b5dfc5d08c07c3b3ef0aaa2a9f";   //插屏广告id
    TextView nametv,pinyintv,jiehsitv,fromtv,exampletv,yufatv,yinzhengtv,tongyitv,fanyitv;
    ScrollView scrollView;
    private InterstitialAd mInterstitialAd;
    boolean adFlag,isFirst=true;;
    private PermissionHelper mPermissionHelper;
    private FloatingActionButton fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengyu);
        MyApplication.getInstance().addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
        // 当系统为6.0以上时，需要申请权限
        checkPermmion(this);
       /* mPermissionHelper = new PermissionHelper(this);
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
        }*/

        nametv=(TextView)findViewById(R.id.chengyuTitle);
        pinyintv=(TextView)findViewById(R.id.pinyin);
        jiehsitv=(TextView)findViewById(R.id.jieshi);
        fromtv=(TextView) findViewById(R.id.from);
        exampletv=(TextView)findViewById(R.id.example);
        yufatv=(TextView)findViewById(R.id.yufa);
        yinzhengtv=(TextView) findViewById(R.id.yinzheng);
        tongyitv=(TextView) findViewById(R.id.tongyi);
        fanyitv=(TextView)findViewById(R.id.fanyi);
        scrollView=(ScrollView)findViewById(R.id.sc_chengyu);
        final ViewGroup container = (ViewGroup) findViewById(R.id.container2);
        fb=(FloatingActionButton)findViewById(R.id.chengYuFb);
        mInterstitialAd = new InterstitialAd(getApplicationContext(),getWindow().getDecorView());
        Intent intent=this.getIntent();
        final Chengyu chengyu=(Chengyu) intent.getSerializableExtra("chengyu");

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

        //初始化插屏广告
        initAd();
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断 scrollView 当前滚动位置在顶部
                if(scrollView.getScrollY() <= 0&&!isFirst){
                    Log.e(TAG2, "滑到了顶部!");
                    if (adFlag){
                        try {
                            if (!mInterstitialAd.isReady()) {
                                adFlag=false;
                                Log.e(TAG, "ad is not ready!");
                            } else {
                                mInterstitialAd.show();
                            }
                        } catch (Exception e) {
                        } finally {
                            //单次预缓存的广告无论结果只显示一次,请求新的广告需要再次调用预缓存接口
                            adFlag=false;
                        }
                    }


                }
                // 判断scrollview 滑动到底部
                // scrollY 的值和子view的高度一样，滑动到了底部
                if (scrollView.getChildAt(0).getHeight() - scrollView.getHeight()== scrollView.getScrollY()){

                    Log.e(TAG2, "滑到了底部!");
                    isFirst=false;
                    initAd();
                    adFlag=true;
                }

                return false;
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {
                //添加收藏action
                User user= BmobUser.getCurrentUser(User.class);
                if (user==null){
                    Util.showConfirmCancelDialog(ChengyuActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent(ChengyuActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                }else{
                    Collect collect=new Collect();
                    collect.setName(chengyu.getName());
                    collect.setUser(user);
                    collect.setType(Collect.CHENGYU);
                    Gson gson = new Gson();
                    String json=gson.toJson(chengyu);
                    collect.setContent(json);

                    collect.save(new SaveListener<String>(){
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("bmob","收藏保存成功");
                                ConstraintLayout layout=(ConstraintLayout)findViewById(R.id.chengYuConLayout);

                                Snackbar.make(layout, "已收藏该成语", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(ChengyuActivity.this,CollectActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
                            }else{
                                Log.i("bmob","收藏保存失败："+e.getMessage());
                            }
                        }
                    });

                }



            }
        });


    }


    public void initAd(){
        try {
            if (mInterstitialAd.isReady()) {
                Log.e(TAG2, "ad has been cached");
            } else {
                mInterstitialAd.requestAd(CHAPING_ID, new AdListener() {
                    @Override
                    public void onAdError(AdError adError) {
                        Log.e(TAG2, "onAdError : " + adError.toString());
                        adFlag=false;
                    }

                    @Override
                    public void onAdEvent(AdEvent adEvent) {
                        try {
                            switch (adEvent.mType) {
                                case AdEvent.TYPE_SKIP:
                                    //用户关闭了广告
                                    Log.e(TAG2, "ad skip!");
                                    break;
                                case AdEvent.TYPE_CLICK:
                                    //用户点击了广告
                                    Log.e(TAG2, "ad click!");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        Log.e(TAG2, "ad is ready : " + mInterstitialAd.isReady());
                        adFlag=true;
                    }

                    @Override
                    public void onViewCreated(View view) {
                        //won't be invoked
                    }
                });
                // Toast.makeText(getApplicationContext(), "加载中...", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }
}
