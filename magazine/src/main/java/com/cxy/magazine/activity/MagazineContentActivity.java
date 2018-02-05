package com.cxy.magazine.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.AndroidException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.CollectBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Utils;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.xiaomi.ad.common.pojo.AdType;
/*
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
*/
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MagazineContentActivity extends BasicActivity {

    private String httpUrl="";
    private WebSettings mWebSettings;
    private static final String AD_ID = "338443b6af5f0f43a7f7e998f80289ed";   //广告id
    private IAdWorker mAdWorker;
    @BindView(R.id.wv_content)  WebView mWebview;
    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.rb_collect)  CheckBox rbCollect;
    @BindView(R.id.containerAd) ViewGroup adContainer;
    private User user;
    private String articleObjectId=null;

    private static ProgressDialog mProgressDialog;
    private String title="",articleId="";
    private StringBuilder content=null;
    private static final String MAGAZINE_URL="http://m.fx361.com";
   // private String testImage="<img alt=\"\" src=\"http://cimg.fx361.com/images/2018/01/17/tzzb201803tzzb20180314-1-l.jpg\" style=\"\"/>";
    private String htmlStr="<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "body{margin-left:15px;margin-right:12px;}h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:180%;margin-top:30px;} img{width:100%;}  .sj{font-size:15px;color:#a6a5a5;}"
            + "</style></head><body>";
    private boolean isFirst=false;
    private String intentUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_content);
        ButterKnife.bind(this);
       // getSupportActionBar().setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intentUrl=getIntent().getStringExtra("url");
        httpUrl=(MAGAZINE_URL + intentUrl).replace("page","news").replace("shtml","html");    //(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");
        articleId=intentUrl.split("/")[4].split(".shtml")[0];
      //  setWebView();
        content=new StringBuilder(htmlStr);
        mProgressDialog=ProgressDialog.show(this, null, "请稍后");
        Thread getHtml=new GetHtml();
        getHtml.start();

        //设置广告
        setAd();
        user= BmobUser.getCurrentUser(User.class);
        //rbCollect.setChecked(true);
        setCollect();
        selectCollect();




    }

public void selectCollect(){
    BmobQuery<CollectBean> collctQuery=new BmobQuery<CollectBean>();
    collctQuery.addWhereEqualTo("user",user);
    collctQuery.addWhereEqualTo("articleId",articleId);
    collctQuery.findObjects(new FindListener<CollectBean>() {
        @Override
        public void done(List<CollectBean> list, BmobException e) {
            if (e==null){
                isFirst=true;
                if (list.size()==1){
                    rbCollect.setChecked(true);
                    articleObjectId=list.get(0).getObjectId();
                }else{
                    rbCollect.setChecked(false);
                }
                isFirst=false;
            }else{
                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
            }
        }
    });
}
public void setCollect(){
    rbCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked&&!isFirst){
                //Utils.toastMessage(MagazineContentActivity.this,"选中");
                //收藏
                CollectBean collectBean=new CollectBean();
                collectBean.setUser(user);
                collectBean.setArticleUrl(intentUrl);
                collectBean.setArticleTitle(title);
                collectBean.setArticleId(articleId);
                collectBean.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e==null){
                            Utils.toastMessage(MagazineContentActivity.this,"收藏文章成功");
                            articleObjectId=objectId;
                        }else{
                            Utils.toastMessage(MagazineContentActivity.this,"收藏文章失败:"+e.getMessage());
                        }
                    }
                });
            }else if (!isFirst){
           //     Utils.toastMessage(MagazineContentActivity.this,"取消");
                //删除收藏
                CollectBean collectBean=new CollectBean();
                collectBean.setObjectId(articleObjectId);
                collectBean.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            Utils.toastMessage(MagazineContentActivity.this,"取消收藏成功");
                        }else{
                            Utils.toastMessage(MagazineContentActivity.this,"取消收藏失败:"+e.getMessage());
                        }
                    }
                });

            }
        }
    });
}



   public void setAd() {
       try {
           mAdWorker = AdWorkerFactory.getAdWorker(this, adContainer, new MimoAdListener() {
               @Override
               public void onAdPresent() {
                   Log.e(LOG_TAG, "onAdPresent");
               }

               @Override
               public void onAdClick() {
                   Log.e(LOG_TAG, "onAdClick");
               }

               @Override
               public void onAdDismissed() {
                   Log.e(LOG_TAG, "onAdDismissed");
               }

               @Override
               public void onAdFailed(String s) {
                   Log.e(LOG_TAG, "onAdFailed");
               }

               @Override
               public void onAdLoaded() {
               }
           }, AdType.AD_STANDARD_NEWSFEED);
           mAdWorker.loadAndShow(AD_ID);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    public void setWebView(){
        mWebSettings = mWebview.getSettings();
        mWebSettings.setDefaultFontSize(22);
        //支持缩放，默认为true。
        mWebSettings .setSupportZoom(false);
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
         // 设置支持缩放
          mWebSettings.setSupportZoom(true);
       // 设置缩放工具的显示
        mWebSettings.setBuiltInZoomControls(true);
        //设置默认编码
        mWebSettings .setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        mWebSettings .setLoadsImagesAutomatically(true);
        mWebview.loadUrl(httpUrl);

        mWebview.setWebViewClient(new WebViewClient() {

            //设置不用系统浏览器打开,直接显示在当前Webview
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });



    }

    class GetHtml extends Thread{
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element mainDiv=docHtml.getElementsByClass("main").first();
                title=mainDiv.getElementsByClass("bt").first().text();  //文章标题   h3
                mainDiv.getElementsByTag("h3").get(1).remove();
                mainDiv.getElementsByTag("ul").first().remove();
                content.append(mainDiv.html());
                content.append("</body></html>");



                uiHandler.sendEmptyMessage(100);
            } catch (IOException e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);
            }
        }
    }

    Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==100){
                // mWebview.loadDataWithBaseURL(null,htmlData,"text/html","utf-8",null);
                // tvTitle.setText(title);
                // tvTime.setText(time);
                // tvContent.setText(content);
                mProgressDialog.dismiss();
                mWebview.loadData(content.toString(), "text/html; charset=UTF-8", null);
            }
            if (msg.what==101){
                String error="<h3>抱歉，该篇文章暂时无法阅读！<h3>";
                mWebview.loadData(content.toString(), "text/html; charset=UTF-8", null);
            }
        }
    };

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_magazine_content, menu);

        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

                finish();


        }

        return true;
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/

    //销毁Webview
    @Override
    protected void onDestroy() {
        /*if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }*/
        super.onDestroy();
    }


}
