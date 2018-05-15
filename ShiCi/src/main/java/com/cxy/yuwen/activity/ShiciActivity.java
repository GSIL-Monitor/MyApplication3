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
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.entity.Article;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.DBOperate;
import com.cxy.yuwen.tool.PermissionHelper;
import com.cxy.yuwen.tool.Utils;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ShiciActivity extends BasicActivity implements   NativeExpressAD.NativeExpressADListener{


    Article article;
    TextView title,zuozhe,content,zhushi;
    FloatingActionButton fb;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;
    private ViewGroup containerAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shici);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true



        title=(TextView)findViewById(R.id.title);
        zuozhe=(TextView)findViewById(R.id.zuozhe);
        content=(TextView)findViewById(R.id.content) ;
        zhushi=(TextView)findViewById(R.id.zhushi);
        fb=(FloatingActionButton)findViewById(R.id.shiciFab);

        //接收intent传递过来的数据
        Intent intent = this.getIntent();
        article= (Article) intent.getSerializableExtra("article");

       // Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/kaiti.ttf");  //设置字体
       // content.setTypeface(typeFace);

        String contentStr=article.getContent().replace("\\r\\n","\r\n");

        String zhushiStr=article.getJieShao().replace("\\r\\n","\r\n");
      //  Log.i("info","转换得到的字符串为"+contentStr);

        title.setText(article.getTitle());
        zuozhe.setText(article.getZuoZhe());
        content.setText(contentStr);
        zhushi.setText(zhushiStr);

        setAd();

        //收藏
        fb.setOnClickListener(new View.OnClickListener() {
            DBOperate dBOperate=null;

            @Override
            public void onClick(View view) {
                //添加收藏action
                User user= BmobUser.getCurrentUser(User.class);
                if (user==null){
                    Utils.showConfirmCancelDialog(ShiciActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent(ShiciActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                }
                else{
                    Collect collect=new Collect();
                    collect.setName(article.getTitle());
                    collect.setUser(user);
                    collect.setType(Collect.SHICI);
                    Gson gson = new Gson();
                    String json=gson.toJson(article);
                    collect.setContent(json);

                    collect.save(new SaveListener<String>(){
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("bmob","收藏保存成功");

                                ConstraintLayout layout=(ConstraintLayout)findViewById(R.id.shiCiLayout);
                                Snackbar.make(layout, "已收藏该诗词", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(ShiciActivity.this,CollectActivity.class);
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
    public  void setAd(){
        //设置信息流大图广告
        containerAd = (ViewGroup) findViewById(R.id.containerAd);
        nativeExpressAD = new NativeExpressAD(this, new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), Constants.TENCENT_APPID,
                                                Constants.SHICI_POS_ID, this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return true;
    }


}
