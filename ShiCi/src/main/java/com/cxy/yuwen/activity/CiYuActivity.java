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
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxy.yuwen.tool.Constants;
import com.google.gson.Gson;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.CiYu;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.PermissionHelper;
import com.cxy.yuwen.tool.Utils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class CiYuActivity extends BasicActivity {
    TextView tvTitle,tvPinyin,tvJieshi;
    public static final String TAG = "AD-StandardNewsFeed";
    private final static String APP_POSITION_ID ="8d0d22595b6cf5057d6bd467eb09c3d8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciyu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

        tvTitle = (TextView) findViewById(R.id.ci_title);
        tvPinyin = (TextView) findViewById(R.id.ci_pinyin);
        tvJieshi = (TextView) findViewById(R.id.ci_jieshi);


        Intent intent = this.getIntent();
        final CiYu ciYu = (CiYu) intent.getSerializableExtra("ciYu");
        // Log.i("info",ciYu.getName());
        // Log.i("info",ciYu.getContent());
        tvTitle.setText(ciYu.getName());
        tvPinyin.setText(ciYu.getContent().split("<br>")[0]);
        tvJieshi.setText(ciYu.getContent().split("<br>")[1]);
      /*  final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);
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
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ciYuFb);
        fab.setOnClickListener(new View.OnClickListener() {
            //  DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {
                User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
                if (user == null) {   //未登录
                    Utils.showConfirmCancelDialog(CiYuActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent(CiYuActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                } else {
                    //添加收藏action
                    // User user= BmobUser.getCurrentUser(User.class);
                    Collect collect = new Collect();
                    collect.setName(ciYu.getName());
                    collect.setUser(user);
                    collect.setType(Collect.CIYU);
                    Gson gson = new Gson();
                    String json = gson.toJson(ciYu);
                    collect.setContent(json);

                    collect.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "收藏保存成功");

                                RelativeLayout layout = (RelativeLayout) findViewById(R.id.ciYuConstraintLayout);

                                Snackbar.make(layout, "已收藏该词语", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CiYuActivity.this, CollectActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
                            } else {
                                Log.i("bmob", "收藏保存失败：" + e.getMessage());
                            }
                        }
                    });


                }
            }


        });

        setAd();

    }
public void setAd(){
    final ViewGroup container = (ViewGroup) findViewById(R.id.containerCi);
    // 创建Banner广告AdView对象
    // appId : 在 http://e.qq.com/dev/ 能看到的app唯一字符串
    // posId : 在 http://e.qq.com/dev/ 生成的数字串，并非 appid 或者 appkey
    final BannerView banner = new BannerView(this, ADSize.BANNER, Constants.TENCENT_APPID, Constants.CIYU_POS_ID);
    container.addView(banner);
    //设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
    banner.setRefresh(30);
    banner.setADListener(new BannerADListener() {
        @Override
        public void onNoAD(com.qq.e.comm.util.AdError adError) {

        }

        @Override
        public void onADReceiv() {

        }

        @Override
        public void onADExposure() {

        }

        @Override
        public void onADClosed() {

        }

        @Override
        public void onADClicked() {

        }

        @Override
        public void onADLeftApplication() {

        }

        @Override
        public void onADOpenOverlay() {

        }

        @Override
        public void onADCloseOverlay() {

        }
    });
        /* 发起广告请求，收到广告数据后会展示数据   */
    banner.loadAD();
}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }
}
