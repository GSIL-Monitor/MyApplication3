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
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxy.yuwen.tool.Constants;
import com.google.gson.Gson;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.Chengyu;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.DBOperate;
import com.cxy.yuwen.tool.Utils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
public class ChengyuActivity extends BasicActivity {
   // public static final String TAG = "AD-StandardNewsFeed";
    public static final String TAG2 = "AD-StandardFeed";
    TextView nametv,pinyintv,jiehsitv,fromtv,exampletv,yufatv,yinzhengtv,tongyitv,fanyitv,yinzhengjs;
    ScrollView scrollView;
    private FloatingActionButton fb;
    private NativeExpressAD nativeExpressAD2;
    private NativeExpressADView nativeExpressADView2;
    private ViewGroup container2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengyu);
     //   MyApplication.getInstance().addActivity(this);
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


        fb=(FloatingActionButton)findViewById(R.id.chengYuFb);
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
        //设置广告
        setXiaoMiAd();

        fb.setOnClickListener(new View.OnClickListener() {
            DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {
                //添加收藏action
                User user= BmobUser.getCurrentUser(User.class);
                if (user==null){
                    Utils.showConfirmCancelDialog(ChengyuActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
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


    public void setXiaoMiAd(){
        //设置信息流大图广告
        container2 = (ViewGroup) findViewById(R.id.adBanner);
        nativeExpressAD2 = new NativeExpressAD(this, new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), Constants.TENCENT_APPID, Constants.CHENGYU_POS_ID, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {

            }

            @Override
            public void onADLoaded(List<NativeExpressADView> adList) {
                if (nativeExpressADView2 != null) {
                    nativeExpressADView2.destroy();
                }
                if (container2.getVisibility() != View.VISIBLE) {
                    container2.setVisibility(View.VISIBLE);
                }
                if (container2.getChildCount() > 0) {
                    container2.removeAllViews();
                }

                  nativeExpressADView2 = adList.get(0);
                  container2.addView(nativeExpressADView2);

                  nativeExpressADView2.render();
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
                if (container2 != null && container2.getChildCount() > 0) {
                    container2.removeAllViews();
                    container2.setVisibility(View.GONE);
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
        });// 传入Activity
        nativeExpressAD2.loadAD(1);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }


}
