package com.cxy.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.Chengyu;
import com.cxy.yuwen.MyApplication;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.DBOperate;
import com.cxy.yuwen.tool.PermissionHelper;
import com.cxy.yuwen.tool.Util;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import ddd.eee.fff.nm.cm.ErrorCode;
import ddd.eee.fff.nm.sp.SpotListener;
import ddd.eee.fff.nm.sp.SpotManager;

public class ChengyuActivity extends BasicActivity {
   // public static final String TAG = "AD-StandardNewsFeed";
    public static final String TAG2 = "AD-StandardFeed";
    //for app
    private final static String APP_POSITION_ID = "babc24ad9259219380f42c1d625a49d5";
    private static final String CHAPING_ID = "4a69307fd900443f8352830d2e22d962";   //小米插屏广告id
    TextView nametv,pinyintv,jiehsitv,fromtv,exampletv,yufatv,yinzhengtv,tongyitv,fanyitv,yinzhengjs;
    ScrollView scrollView;
    private InterstitialAd mInterstitialAd;
    boolean adFlag=false,isFirst=true;
    private PermissionHelper mPermissionHelper;
    private FloatingActionButton fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengyu);
        MyApplication.getInstance().addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true


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
        yinzhengjs=(TextView)findViewById(R.id.yinzhengjs);     //引证解释

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
        setupSpotAd();  //设置有米插屏广告
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

        /*//初始化插屏广告
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
        });*/

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

      /*  final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        final TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHiddenAction.setDuration(500);*/
        yinzhengjs.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (yinzhengtv.getVisibility()==View.GONE){
                   //yinzhengtv.startAnimation(mShowAction);
                    yinzhengtv.setVisibility(View.VISIBLE);
                }else {
                  //  yinzhengtv.startAnimation(mHiddenAction);
                    yinzhengtv.setVisibility(View.GONE);
                }

            }
        });

    }

    /**
     * 设置插屏广告
     */
    private void setupSpotAd() {
        // 设置插屏图片类型，默认竖图
        //	// 横图
        //		SpotManager.getInstance(mContext).setImageType(SpotManager
        // .IMAGE_TYPE_HORIZONTAL);
        // 竖图
        SpotManager.getInstance(this).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);

        // 设置动画类型，默认高级动画
        //		// 无动画
        //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
        // .ANIMATION_TYPE_NONE);
        //		// 简单动画
        //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
        // .ANIMATION_TYPE_SIMPLE);
        // 高级动画
        SpotManager.getInstance(this)
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断 scrollView 当前滚动位置在顶部
                if(scrollView.getScrollY() <= 0&&adFlag){
                    Log.i(TAG, "滑到了顶部!");
                    adFlag=false;

                    // 展示插屏广告
                    SpotManager.getInstance(ChengyuActivity.this).showSpot(ChengyuActivity.this, new SpotListener() {

                        @Override
                        public void onShowSuccess() {
                            Log.i(YOUMI_AD_TAG,"插屏展示成功");
                        }

                        @Override
                        public void onShowFailed(int errorCode) {
                            Log.e(YOUMI_AD_TAG,"插屏展示失败");
                            switch (errorCode) {
                                case ErrorCode.NON_NETWORK:
                                    Log.e(YOUMI_AD_TAG,"网络异常");
                                    break;
                                case ErrorCode.NON_AD:
                                    Log.e(YOUMI_AD_TAG,"暂无插屏广告");
                                    break;
                                case ErrorCode.RESOURCE_NOT_READY:
                                    Log.e(YOUMI_AD_TAG,"插屏资源还没准备好");
                                    break;
                                case ErrorCode.SHOW_INTERVAL_LIMITED:
                                    Log.e(YOUMI_AD_TAG,"请勿频繁展示");
                                    break;
                                case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                    Log.e(YOUMI_AD_TAG,"请设置插屏为可见状态");
                                    break;
                                default:
                                    Log.e(YOUMI_AD_TAG,"请稍后再试");
                                    break;
                            }
                        }

                        @Override
                        public void onSpotClosed() {
                            Log.e(YOUMI_AD_TAG,"插屏被关闭");
                        }

                        @Override
                        public void onSpotClicked(boolean isWebPage) {
                            Log.e(YOUMI_AD_TAG,"插屏被点击");
                            Log.e(YOUMI_AD_TAG,isWebPage? "是" : "不是"+"网页广告");
                        }
                    });  //

                }
                // 判断scrollview 滑动到底部
                // scrollY 的值和子view的高度一样，滑动到了底部
                if (scrollView.getChildAt(0).getHeight() - scrollView.getHeight()== scrollView.getScrollY()){

                    Log.i(TAG, "滑到了底部!");
                    adFlag=true;

                }

                return false;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        // 点击后退关闭插屏广告
        if (SpotManager.getInstance(ChengyuActivity.this).isSpotShowing()) {
            SpotManager.getInstance(ChengyuActivity.this).hideSpot();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(ChengyuActivity.this).onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 插屏广告
        SpotManager.getInstance(ChengyuActivity.this).onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(ChengyuActivity.this).onDestroy();
    }

}
