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
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MagazineContentActivity extends BasicActivity {

    private String httpUrl="";
    private WebSettings mWebSettings;
    private static final String AD_ID = "338443b6af5f0f43a7f7e998f80289ed";   //广告id
    @BindView(R.id.wv_content)  WebView mWebview;
    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.rb_collect)  CheckBox rbCollect;
    @BindView(R.id.containerAd) ViewGroup adContainer;
    private User user;

    private static ProgressDialog mProgressDialog;
    private String title="",articleId="";
    private StringBuilder content=null;
    private String testImage="<img alt=\"\" src=\"http://cimg.fx361.com/images/2018/01/17/tzzb201803tzzb20180314-1-l.jpg\" style=\"\"/>";
    private String htmlStr="<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:130%;margin:9px;text-indent:2em} img{width:100%;}  .sj{font-size:16px;color:#a6a5a5;}"
            + "</style></head><body>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_content);
        ButterKnife.bind(this);
       // getSupportActionBar().setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        httpUrl=getIntent().getStringExtra("url");
        articleId=httpUrl.split("/")[4].split(".")[0];
      //  setWebView();
        content=new StringBuilder(htmlStr);
        mProgressDialog=ProgressDialog.show(this, null, null);
        Thread getHtml=new GetHtml();
        getHtml.start();

        //设置广告
        setAd();
        user= BmobUser.getCurrentUser(User.class);
        //rbCollect.setChecked(true);

        rbCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //Utils.toastMessage(MagazineContentActivity.this,"选中");
                    //收藏
                    CollectBean collectBean=new CollectBean();
                    collectBean.setUser(user);
                    collectBean.setArticleUrl(httpUrl);
                    collectBean.setArticleTitle(title);
                    collectBean.setArtivleId(articleId);
                    collectBean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                             if (e==null){
                                 Utils.toastMessage(MagazineContentActivity.this,"收藏文章成功");
                             }else{
                                 Utils.toastMessage(MagazineContentActivity.this,"收藏文章失败:"+e.getMessage());
                             }
                        }
                    });
                }else{
                    Utils.toastMessage(MagazineContentActivity.this,"取消");
                }
            }
        });


    }





    public void setAd(){
        final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    standardNewsFeedAd.requestAd(AD_ID, 1, new NativeAdListener() {
                        @Override
                        public void onNativeInfoFail(AdError adError) {
                            Log.e(LOG_TAG, "onNativeInfoFail e : " + adError);
                        }

                        @Override
                        public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                            NativeAdInfoIndex response = list.get(0);

                            standardNewsFeedAd.buildViewAsync(response, adContainer.getWidth(), new AdListener() {
                                @Override
                                public void onAdError(AdError adError) {
                                    Log.e(LOG_TAG, "error : remove all views");
                                    adContainer.removeAllViews();
                                }

                                @Override
                                public void onAdEvent(AdEvent adEvent) {
                                    //目前考虑了３种情况，用户点击信息流广告，用户点击x按钮，以及信息流展示的３种回调，范例如下
                                    if (adEvent.mType == AdEvent.TYPE_CLICK) {
                                        Log.d(LOG_TAG, "ad has been clicked!");
                                    } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                                        Log.d(LOG_TAG, "x button has been clicked!");
                                    } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                                        Log.d(LOG_TAG, "ad has been showed!");
                                    }
                                }

                                @Override
                                public void onAdLoaded() {

                                }

                                @Override
                                public void onViewCreated(View view) {
                                    Log.e(LOG_TAG, "onViewCreated");
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

/*

        mWebview.setWebViewClient(new WebViewClient() {

            //设置不用系统浏览器打开,直接显示在当前Webview
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });
*/


    }

    class GetHtml extends Thread{
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element mainDiv=docHtml.getElementsByClass("main").first();
                title=mainDiv.getElementsByClass("bt").first().text();  //文章标题   h3
              //  time=mainDiv.getElementsByClass("sj").first().html();   //发布时间   p
              //  String contentStr=mainDiv.getElementsByClass("wz").html();
                mainDiv.getElementsByTag("h3").get(1).remove();
                mainDiv.getElementsByTag("ul").first().remove();
                content.append(mainDiv.html());
                content.append("</body></html>");




                uiHandler.sendEmptyMessage(100);
            } catch (IOException e) {
                e.printStackTrace();
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
