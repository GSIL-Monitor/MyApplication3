package com.cxy.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
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
import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.Composition;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.OkHttpUtil;
import com.cxy.yuwen.tool.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class CompositionDetailActivity extends BasicActivity {

    TextView tvName,tvSchool,tvWriter,tvTime,tvContent,tvComment,tvTeacher;
    FloatingActionButton fab;
    Composition composition=null;
    ViewGroup adContainer;
    private static final String AD_ID = "73de7ce1acfd8777553c367d5a4aab06";   //广告id
   // private InterstitialAd mInterstitialAd;
    private static final String APPKEY_COMPOSITION ="cf7b0e43bbe17e82cc632163361ccfcf";  //作文key
    private static String urlContent="http://zuowen.api.juhe.cn/zuowen/content";
    private String error="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition_detail);
        initView();

        Intent intent=this.getIntent();
        Composition selectComposition=(Composition)intent.getSerializableExtra("selectComposition");
        if (selectComposition!=null){
            composition=selectComposition;
            queryComposition();
        }

       Composition collectComposition=(Composition)intent.getSerializableExtra("composition");  //composition
        if (collectComposition!=null){
            tvName.setText(collectComposition.getName());
            tvSchool.setText(collectComposition.getSchool());
            tvWriter.setText(collectComposition.getWriter());
            tvTime.setText(collectComposition.getTime());
            tvContent.setText(collectComposition.getContent());
            tvComment.setText(collectComposition.getComment());

            composition=collectComposition;
        }

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


      //  mInterstitialAd = new InterstitialAd(getApplicationContext(),getWindow().getDecorView());

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

    //查询作文
    public void queryComposition(){
        final Map paramMap=new HashMap();
        paramMap.put("key",APPKEY_COMPOSITION);
        paramMap.put("id",composition.getId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String compositionDetail= OkHttpUtil.get(urlContent,paramMap);
                    JSONObject compositionJson=new JSONObject(compositionDetail);
                    if (compositionJson.getInt("error_code")==0) {   //请求成功
                      //  composition=new Composition();

                        JSONObject resultObject=compositionJson.getJSONObject("result");
                        String contentStr=resultObject.getString("content");
                        String content=contentStr.replace("<p>","").replace("</p>","\n\n");

                        composition.setContent(content);
                        composition.setComment(resultObject.getString("comment"));
                        composition.setSchool(resultObject.getString("school"));
                        composition.setTeacher(resultObject.getString("teacher"));
                        handler.sendEmptyMessage(100);


                    }
                    else{
                        //  Log.i(TAG,compositionJson.get("error_code")+":"+compositionJson.get("reason"));
                        error=compositionJson.get("error_code")+":"+compositionJson.get("reason");
                        handler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error="出现错误，该篇作文暂时无法查看";
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();//

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Util.showResultDialog(CompositionDetailActivity.this,error,"出错了");
                    break;
                case 100:
                    tvName.setText(composition.getName());
                    tvSchool.setText(composition.getSchool());
                    tvWriter.setText(composition.getWriter());
                    tvTime.setText(composition.getTime());
                    tvContent.setText(composition.getContent());
                    tvComment.setText(composition.getComment());
                    break;
            }
        }
    };

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
