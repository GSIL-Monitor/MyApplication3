package com.yuwen.activity;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.yuwen.bmobBean.Collect;
import com.yuwen.bmobBean.User;
import com.yuwen.entity.Zi;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;

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
    private final static String APP_POSITION_ID = "35e0adcb1e64ae7d3d2f964f71ff8b2f";
    private int[] mAdPositionList = {1, 3, 5};
    private ArrayList<NativeAdInfoIndex> mStuffList;
    private StandardNewsFeedAd mStandardNewsFeedAd;
  //  private Map<Integer,Boolean> pyMap,dyMap,bsMap,bhMap,jjMap,xjMap;

    private List<Object> ziList;



    private String queryText;  // 要查询的内容
    private ListView listView;
    private ZidianAdapter adapter;
    private static final int MSG_LOAD_SUCCESS=100;
    private static final int MSG_ERROR=0;
    private String error="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidian);
        MyApplication.getInstance().addActivity(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

        mStuffList = new ArrayList<NativeAdInfoIndex>();
        mStandardNewsFeedAd = new StandardNewsFeedAd(this);
        listView=(ListView)findViewById(R.id.zidianLv);

        Intent intent=this.getIntent();
        queryText=intent.getStringExtra("queryText");
        ziList=new ArrayList<Object>();


        //Util.showProgressDialog(this,"请稍候","正在加载");
        Thread thread=new Thread(zidianRunnable);
        thread.start();






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
           }else{
               error=jsonObject.getInt("error_code")+":"+jsonObject.getString("reason");
               mUIHandler.sendEmptyMessage(MSG_ERROR);

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

                   // Util.dismissDialog();
                    //更新ListView显示
                    adapter = new ZidianAdapter(ZidianActivity.this);
                    listView.setAdapter(adapter);
                    loadAd();
                    break;
                case MSG_ERROR:

                Util.showResultDialog(ZidianActivity.this,error,"警告");
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

    @Override
    protected void onResume() {
        super.onResume();
        //注意要在这里重设一遍adpater，以免事件处理出现问题
      listView.setAdapter(adapter);
    }


    class ZidianAdapter extends BaseAdapter {

        private LayoutInflater mInflater;



        public ZidianAdapter(Context context) {



            mInflater = LayoutInflater.from(context);


        }


        @Override
        public int getCount() {
            return ziList.size();
        }

        @Override
        public Object getItem(int position) {
            return ziList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        private boolean isAdPosition(int index) {
            for (int i = 0; i < mAdPositionList.length; i++) {
                if (mAdPositionList[i] == index) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

              Log.i("Listview",position+"---");
            if (isAdPosition(position) && ziList.get(position) instanceof ViewGroup) {
                return (View) ziList.get(position);
            }
            else {
                ViewHolder holder = null;
                ConstraintLayout constraintLayout;
                //判断是否缓存
                if (convertView != null && convertView instanceof ConstraintLayout) {

                    //通过tag找到缓存的布局
                    holder = (ViewHolder) convertView.getTag();
                    constraintLayout=(ConstraintLayout)convertView;


                } else {

                    holder = new ViewHolder();
                    //通过LayoutInflater实例化布局
                    constraintLayout = (ConstraintLayout )mInflater.inflate(R.layout.zidian_item, null);

                    holder.hanzi = (TextView) constraintLayout.findViewById(R.id.hanzi);   //下次不用再findViewById()
                    holder.pinyin = (TextView) constraintLayout.findViewById(R.id.pinyin);
                    holder.duyin = (TextView) constraintLayout.findViewById(R.id.duyin);
                    holder.bushou = (TextView) constraintLayout.findViewById(R.id.bushou);
                    holder.bihua = (TextView) constraintLayout.findViewById(R.id.bihua);
                    holder.jianjie = (TextView) constraintLayout.findViewById(R.id.jianjie);
                    holder.xiangjie = (TextView) constraintLayout.findViewById(R.id.xiangjie);
                    holder.constraintLayout = (ConstraintLayout) constraintLayout.findViewById(R.id.containerLayout);
                    holder.xjTextView = (TextView) constraintLayout.findViewById(R.id.xj);
                    holder.jjTextView = (TextView) constraintLayout.findViewById(R.id.jj);
                    holder.pyTextView = (TextView) constraintLayout.findViewById(R.id.py);
                    holder.dyTextView = (TextView) constraintLayout.findViewById(R.id.dy);
                    holder.bsTextView = (TextView) constraintLayout.findViewById(R.id.bs);
                    holder.bhTextView = (TextView) constraintLayout.findViewById(R.id.bh);
                    holder.fab = (FloatingActionButton) constraintLayout.findViewById(R.id.zidianFb);


                    constraintLayout.setTag(holder);
                }

                if (ziList.get(position) instanceof Zi){
                    holder.hanzi.setTextIsSelectable(true);
                    holder.pinyin.setTextIsSelectable(true);
                    holder.duyin.setTextIsSelectable(true);
                    holder.bushou.setTextIsSelectable(true);
                    holder.bihua.setTextIsSelectable(true);
                    holder.jianjie.setTextIsSelectable(true);
                    holder.xiangjie.setTextIsSelectable(true);

                    Zi zi = (Zi) ziList.get(position);
                    holder.hanzi.setText(zi.getHanzi());
                    holder.pinyin.setText(zi.getPinyin());
                    holder.duyin.setText(zi.getDuyin());
                    holder.bushou.setText(zi.getBushou());
                    holder.bihua.setText(zi.getBihua());
                    holder.jianjie.setText(zi.getJianjie());
                    holder.xiangjie.setText(zi.getXiangjie());
                    holder.fab.setOnClickListener(new ViewClicklistener(holder.hanzi.getText().toString(), holder.constraintLayout));






                    holder.xjTextView.setOnClickListener(new ViewClicklistener(holder.xiangjie));
                    holder.jjTextView.setOnClickListener(new ViewClicklistener(holder.jianjie));
                    holder.pyTextView.setOnClickListener(new ViewClicklistener(holder.pinyin));
                    holder.dyTextView.setOnClickListener(new ViewClicklistener(holder.duyin));
                    holder.bsTextView.setOnClickListener(new ViewClicklistener(holder.bushou));
                    holder.bhTextView.setOnClickListener(new ViewClicklistener(holder.bihua));

                }



                return constraintLayout;
            }
        }

        public synchronized void loadAdView(View view, int index) {
           // ziList.remove(mAdPositionList[index]);   //index
           // this.notifyDataSetChanged();
            ziList.add(mAdPositionList[index], view);
            this.notifyDataSetChanged();
        }



    }


    public final class ViewHolder{

        TextView hanzi,pinyin,duyin,bushou,bihua,jianjie,xiangjie,pyTextView,dyTextView,bsTextView,bhTextView,xjTextView,jjTextView;
        ConstraintLayout constraintLayout;
        FloatingActionButton fab;

    }

    public final class ViewClicklistener implements View.OnClickListener {
        View visableView;
        String hanzi;

        public ViewClicklistener(View view) {
            visableView = view;

        }

        public ViewClicklistener(String hanzi, View view) {
            this.hanzi = hanzi;
            this.visableView = view;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.zidianFb) {
                if (v.getId() == R.id.zidianFb) {   //添加收藏到数据库
                    User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
                    if (user == null) {   //未登录
                        Util.showConfirmCancelDialog(ZidianActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(ZidianActivity.this, LoginActivity.class);
                                startActivity(intent1);
                            }
                        });
                    } else {

                        //添加收藏action
                        //  User user= BmobUser.getCurrentUser(User.class);
                        Collect collect = new Collect();
                        collect.setName(hanzi);
                        collect.setUser(user);
                        collect.setType(Collect.ZI);


                        collect.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Log.i("bmob", "收藏保存成功");
                                    Snackbar.make(visableView, "已收藏该生字", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(ZidianActivity.this, CollectActivity.class);
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
            } else {
                if (visableView.getVisibility() == View.GONE) {
                    // visableView.startAnimation(mShowAction);
                    visableView.setVisibility(View.VISIBLE);
                } else if (visableView.getVisibility() == View.VISIBLE) {
                    //   visableView.startAnimation(mHiddenAction);
                    visableView.setVisibility(View.GONE);
                }
            }
        }




    }//


    public void loadAd(){
        try {
            mStuffList.clear();
            mStandardNewsFeedAd.requestAd(APP_POSITION_ID, mAdPositionList.length, new NativeAdListener() {
                @Override
                public void onNativeInfoFail(AdError adError) {
                    Log.e(TAG, "onNativeInfoFail e : " + adError);
                }

                @Override
                public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                    Log.e(TAG, "onNativeInfoSuccess is " + list);
                    mStuffList.addAll(list);
                    int size = (mStuffList.size() <= mAdPositionList.length) ? mStuffList.size() : mAdPositionList.length;
                    for (int i = 0; i < size; i++) {
                        final int index = i;
                        final NativeAdInfoIndex adInfoResponse = mStuffList.get(index);
                        mStandardNewsFeedAd.buildViewAsync(adInfoResponse, listView.getWidth(), new AdListener() {
                            @Override
                            public void onAdError(AdError adError) {
                                Log.e(TAG, "onAdError : " + adError + " at index : " + index);
                            }

                            @Override
                            public void onAdEvent(AdEvent adEvent) {
                                if (adEvent.mType == AdEvent.TYPE_CLICK) {
                                    Log.d(TAG, "ad has been clicked at position: " + index);
                                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
//                                            Log.d(TAG, "x button has been clicked at position : " + index);
                                } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                                    Log.d(TAG, "ad has been showed at position: " + index);
                                }
                            }

                            @Override
                            public void onAdLoaded() {

                            }

                            @Override
                            public void onViewCreated(View view) {
                                adapter.loadAdView(view, index);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }//
    }

}
