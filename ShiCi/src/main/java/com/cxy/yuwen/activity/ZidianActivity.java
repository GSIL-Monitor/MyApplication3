package com.cxy.yuwen.activity;

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
import android.widget.TextView;

import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.Zi;
import com.cxy.yuwen.R;

import com.cxy.yuwen.tool.Constants;
import com.cxy.yuwen.tool.NetworkConnection;
import com.cxy.yuwen.tool.Utils;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class ZidianActivity extends BasicActivity implements NativeExpressAD.NativeExpressADListener{


    private List<Object> ziList;
    private String queryText;  // 要查询的内容
   
  //  private ZidianAdapter adapter;
    private static final int MSG_LOAD_SUCCESS=100;
    private static final int MSG_ERROR=0;
    private String error="";

    @BindView(R.id.hanzi)
    TextView hanzi;
    @BindView(R.id.pinyin)
    TextView pinyin;
    @BindView(R.id.duyin )
    TextView duyin;
    @BindView(R.id.bushou)
    TextView bushou;
    @BindView(R.id.bihua)
    TextView bihua;
    @BindView(R.id.jianjie)
    TextView jianjie;
    @BindView(R.id.xiangjie)
    TextView xiangjie;

    @BindView(R.id.cl_zi)
    ConstraintLayout constraintLayout;
    @BindView(R.id.zidianFb)
    FloatingActionButton fab;

    ViewGroup containerAd;

    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;


    Zi zi=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidian);
        ButterKnife.bind(this);
        containerAd=(ViewGroup)findViewById(R.id.ziAd);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

      

        Intent intent=this.getIntent();
        queryText=intent.getStringExtra("queryText");
      //  ziList=new ArrayList<Object>();


        Thread thread=new Thread(zidianRunnable);
        thread.start();

        setAd();


    }

    public void setAd(){
        //设置信息流大图广告
        nativeExpressAD = new NativeExpressAD(this, new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), Constants.TENCENT_APPID,
                Constants.ZI_POS_ID, this);
        nativeExpressAD.loadAD(1);

    }

    @Override
    public void onNoAD(AdError adError) {

    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }
        if (containerAd.getVisibility() != View.VISIBLE) {
            containerAd.setVisibility(View.VISIBLE);
        }
        if (containerAd.getChildCount() > 0) {
            containerAd.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        containerAd.addView(nativeExpressADView);

        nativeExpressADView.render();
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {
        // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
        if (containerAd != null && containerAd.getChildCount() > 0) {
            containerAd.removeAllViews();
            containerAd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

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
               for (int i=0;i<dataArray.length();i++){      //length=1;
                   JSONObject ziData=dataArray.getJSONObject(i);
                   zi=new Zi();
                  // zi.setId(ziData.getString("id"));
                   zi.setHanzi(ziData.getString("hanzi"));
                   zi.setPinyin(ziData.getString("pinyin"));
                   zi.setDuyin(ziData.getString("duyin"));
                   zi.setBushou(ziData.getString("bushou"));
                   zi.setBihua(ziData.getString("bihua"));
                   zi.setJianjie(ziData.getString("jianjie"));
                   zi.setXiangjie(ziData.getString("xiangjie"));

                 //  ziList.add(zi);

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
                    hanzi.setText(zi.getHanzi());
                    pinyin.setText(zi.getPinyin());
                    duyin.setText(zi.getDuyin());
                    bushou.setText(zi.getBushou());
                    bihua.setText(zi.getBihua());
                    jianjie.setText(zi.getJianjie());
                    xiangjie.setText(zi.getXiangjie());
                 //   loadAd();
                    break;
                case MSG_ERROR:

                Utils.showResultDialog(ZidianActivity.this,error,"警告");
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

  @OnClick(R.id.py)
    public void  pyClick(){
      if (pinyin.getVisibility() == View.GONE) {
          pinyin.setVisibility(View.VISIBLE);
      } else if (pinyin.getVisibility() == View.VISIBLE) {

          pinyin.setVisibility(View.GONE);
      }
   }
    @OnClick(R.id.dy)
    public void  dyClick(){
        if (duyin.getVisibility() == View.GONE) {
            duyin.setVisibility(View.VISIBLE);
        } else if (duyin.getVisibility() == View.VISIBLE) {

            duyin.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.bs)
    public void  bsClick(){
        if (bushou.getVisibility() == View.GONE) {
            bushou.setVisibility(View.VISIBLE);
        } else if (bushou.getVisibility() == View.VISIBLE) {

            bushou.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.bh)
    public void  bhClick(){
        if (bihua.getVisibility() == View.GONE) {
            bihua.setVisibility(View.VISIBLE);
        } else if (bihua.getVisibility() == View.VISIBLE) {

            bihua.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.jj)
    public void  jjClick(){
        if (jianjie.getVisibility() == View.GONE) {
            jianjie.setVisibility(View.VISIBLE);
        } else if (jianjie.getVisibility() == View.VISIBLE) {

            jianjie.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.xj)
    public void  xjClick(){
        if (xiangjie.getVisibility() == View.GONE) {
            xiangjie.setVisibility(View.VISIBLE);
        } else if (xiangjie.getVisibility() == View.VISIBLE) {

            xiangjie.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.zidianFb)
    public  void  collect(){

        User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
        if (user == null) {   //未登录
            Utils.showConfirmCancelDialog(ZidianActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
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
            collect.setName(hanzi.getText().toString());
            collect.setUser(user);
            collect.setType(Collect.ZI);


            collect.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Log.i("bmob", "收藏保存成功");
                        Snackbar.make(constraintLayout, "已收藏该生字", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
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





}
