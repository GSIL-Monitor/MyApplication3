package com.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yuwen.adapter.ZidianAdapter;
import com.yuwen.bmobBean.Collect;
import com.yuwen.bmobBean.User;
import com.yuwen.entity.Zi;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Adapter;
import com.yuwen.tool.NetworkConnection;
import com.yuwen.tool.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ZidianActivity extends BasicActivity {
    public static final String TAG = "AD-StandardNewsFeed";
    //for app
    private final static String APP_POSITION_ID = "35e0adcb1e64ae7d3d2f964f71ff8b2f";

    private List<Zi> ziList;
    private TextView tv1,tv2;
    private ConstraintLayout layout;
    private Zi zi=null;
    private String queryText;  // 要查询的内容
    private ListView listView;
    private BaseAdapter adapter;
    private static final int MSG_LOAD_SUCCESS=100;

    TranslateAnimation mShowAction;
    TranslateAnimation mHiddenAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidian);
        MyApplication.getInstance().addActivity(this);
        //检查权限
        checkPermmion(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true


       /* layout=(ConstraintLayout) findViewById(R.id.zidianConlayout);*/
        listView=(ListView)findViewById(R.id.zidianLv);
       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ziFb);

     //   final ViewGroup container = (ViewGroup) findViewById(R.id.container1);

       /* tv1=(TextView)findViewById(R.id.title);
        tv2=(TextView)findViewById(R.id.ziidan);*/


        Intent intent=this.getIntent();
        queryText=intent.getStringExtra("queryText");
        ziList=new ArrayList<Zi>();
        adapter = new ZidianAdapter(this, ziList);
        listView.setAdapter(adapter);
        Thread thread=new Thread(zidianRunnable);
        thread.start();

        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHiddenAction.setDuration(500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvXj=(TextView)view.findViewById(R.id.xiangjie);
                if (tvXj.getVisibility()==View.VISIBLE){
                    tvXj.setVisibility(View.GONE);
                }else{
                    tvXj.setVisibility(View.VISIBLE);
                }
            }
        });

/*
        fab.setOnClickListener(new View.OnClickListener() {
          //  DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {

                User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
                if (user==null){   //未登录
                    Util.showConfirmCancelDialog(ZidianActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                  Intent intent1=new Intent(ZidianActivity.this,LoginActivity.class);
                                  startActivity(intent1);
                        }
                    });
                }
                else{

                    //添加收藏action
                  //  User user= BmobUser.getCurrentUser(User.class);
                    Collect  collect=new Collect();
                    collect.setName(zi.getName());
                    collect.setUser(user);
                    collect.setType(Collect.ZI);
                    Gson gson = new Gson();
                    String json=gson.toJson(zi);
                    collect.setContent(json);

                    collect.save(new SaveListener<String>(){
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("bmob","收藏保存成功");
                                Snackbar.make(layout, "已收藏该生字", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(ZidianActivity.this,CollectActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();

                            }else{
                                Log.i("bmob","收藏保存失败："+e.getMessage());
                            }
                        }
                    });

                }




               *//*保存到本地
                DBHelper dbHelper=new DBHelper(ZidianActivity.this);
                dBOperate=new DBOperate(dbHelper);
                Gson gson = new Gson();
                String json=gson.toJson(zi);



                dBOperate.insert("1",zi.getName(),json);    //插入到数据库

                Snackbar.make(layout, "已收藏该生字", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ZidianActivity.this,CollectActivity.class);
                        startActivity(intent);
                    }
                }).show();*//*

            }
        });*/

       /* final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);

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


    }

    Runnable zidianRunnable=new Runnable() {
        @Override
        public void run() {
            Map map=new HashMap<String,String>();
            map.put("key", NetworkConnection.APPKEY_ZI);
            map.put("content",queryText);


            try {
                String dataZidian= NetworkConnection.net(NetworkConnection.URL_ZI,map,"GET");
                parseJsonData(dataZidian);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 解析数据字符串
     * @param str
     */
   public  void parseJsonData(String str){
       try {

           JSONObject jsonObject = new JSONObject(str);
           if(jsonObject.getInt("error_code")==0){
               JSONArray dataArray=jsonObject.getJSONArray("result");
               for (int i=0;i<dataArray.length();i++){
                   JSONObject ziData=dataArray.getJSONObject(i);
                   Zi zi=new Zi();
                  // zi.setId(ziData.getString("id"));
                   zi.setHanzi(ziData.getString("hanzi"));
                   zi.setPinyin(ziData.getString("pinyin"));
                   zi.setDuyin(ziData.getString("duyin"));
                   zi.setBushou(ziData.getString("bushou"));
                   zi.setBihua(ziData.getString("bihua"));
                   zi.setJianjie(ziData.getString("jianjie"));
                   zi.setXiangjie(ziData.getString("xiangjie"));

                   ziList.add(zi);

               }

               mUIHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
           }

       } catch (JSONException e) {
           e.printStackTrace();
       }

   }

    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_SUCCESS:
                   // footer.setVisibility(View.GONE);
                    //更新ListView显示
                    adapter.notifyDataSetChanged();


                    break;


            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }
}
