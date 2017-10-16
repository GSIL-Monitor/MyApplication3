package com.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yuwen.MyApplication;
import com.yuwen.bmobBean.Collect;
import com.yuwen.bmobBean.User;
import com.yuwen.entity.Composition;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Util;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class CompositionDetailActivity extends BasicActivity {

    TextView tvName,tvSchool,tvWriter,tvTime,tvContent,tvComment,tvTeacher;
    FloatingActionButton fab;
    Composition composition;
    ViewGroup adContainer;
    private static final String AD_ID = "73de7ce1acfd8777553c367d5a4aab06";   //广告id
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition_detail);
        initView();

        Intent intent=this.getIntent();
        composition=(Composition)intent.getSerializableExtra("composition");  //composition
        tvName.setText(composition.getName());
        tvSchool.setText(composition.getSchool());
        tvWriter.setText(composition.getWriter());
        tvTime.setText(composition.getTime());
        tvContent.setText(composition.getContent());
        tvComment.setText(composition.getComment());
     //   tvTeacher.setText(composition.getType());

        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
                if (user == null) {   //未登录
                    Util.showConfirmCancelDialog(CompositionDetailActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent(CompositionDetailActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                } else {
                    //添加收藏action
                    // User user= BmobUser.getCurrentUser(User.class);
                    Collect collect = new Collect();
                    collect.setName(composition.getName());
                    collect.setUser(user);
                    collect.setType(Collect.COMPOSITION);
                    Gson gson = new Gson();
                    String json = gson.toJson(composition);
                    collect.setContent(json);

                    collect.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "收藏保存成功");

                                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.compositionDetailLayout);

                                Snackbar.make(layout, "已收藏该作文", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CompositionDetailActivity.this, CollectActivity.class);
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


        mInterstitialAd = new InterstitialAd(getApplicationContext(),getWindow().getDecorView());
        final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    standardNewsFeedAd.requestAd(AD_ID, 1, new NativeAdListener() {
                        @Override
                        public void onNativeInfoFail(AdError adError) {
                            Log.e(TAG, "onNativeInfoFail e : " + adError);
                        }

                        @Override
                        public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                            NativeAdInfoIndex response = list.get(0);
                            standardNewsFeedAd.buildViewAsync(response, adContainer.getWidth(), new AdListener() {
                                @Override
                                public void onAdError(AdError adError) {
                                    Log.e(TAG, "error : remove all views");
                                    adContainer.removeAllViews();
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
                                    adContainer.removeAllViews();
                                    adContainer.addView(view);
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

    public void initView(){
        MyApplication.getInstance().addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

        tvName=(TextView)findViewById(R.id.compositionTitle);
        tvSchool=(TextView)findViewById(R.id.compositionSchool);
        tvWriter=(TextView)findViewById(R.id.compositionWriter);
        tvTime=(TextView)findViewById(R.id.compositionTime);
        tvContent=(TextView)findViewById(R.id.compositionContent);
        tvComment=(TextView)findViewById(R.id.compositionComment);
     //   tvTeacher=(TextView)findViewById(R.id.compositionTeacher);
        fab=(FloatingActionButton)findViewById(R.id.compositionFb);
        adContainer = (ViewGroup) findViewById(R.id.compositionAdcontainer);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }
}
