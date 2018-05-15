package com.cxy.yuwen.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Constants;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.cxy.yuwen.jsInterface.JavascriptInterface;
import com.cxy.yuwen.tool.Util;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.xiaomi.market.sdk.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineContentActivity extends BasicActivity implements  NativeExpressAD.NativeExpressADListener{

    private String httpUrl="";
    private WebSettings mWebSettings;
    private String title="";
    private static final String AD_ID = "73de7ce1acfd8777553c367d5a4aab06";   //广告id
    private String htmlStr="<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "body{margin-left:15px;margin-right:12px;}h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:200%;margin-top:30px;} img{width:100%;}  .sj{font-size:15px;color:#a6a5a5;}"
            + "</style></head><body>";
    private static final String MAGAZINE_URL="http://m.fx361.com";
    private String intentUrl="";
    private StringBuilder content=null;
    private static ProgressDialog mProgressDialog;
    @BindView(R.id.wv_content)
    WebView mWebview;
    @BindView(R.id.containerAd)
    ViewGroup adContainer;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_content);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setWebView();
        intentUrl=getIntent().getStringExtra("url");
        httpUrl=(MAGAZINE_URL + intentUrl).replace("page","news").replace("shtml","html");    //(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");

        content=new StringBuilder(htmlStr);
        mProgressDialog=ProgressDialog.show(this,   null, "请稍后");
        Thread getHtml=new GetHtml();
        getHtml.start();




    }

    public void setAd(){
        nativeExpressAD = new NativeExpressAD(this,new ADSize(ADSize.FULL_WIDTH,ADSize.AUTO_HEIGHT), Constants.TENCENT_APPID, Constants.ZAZHI_POS_ID, this);// 传入Activity
        nativeExpressAD.loadAD(1);
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TENCENT_LOG, String.format("onADError, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        // 释放前一个展示的NativeExpressADView的资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (adContainer.getVisibility() != View.VISIBLE) {
            adContainer.setVisibility(View.VISIBLE);
        }

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        // 广告可见才会产生曝光，否则将无法产生收益。
        adContainer.addView(nativeExpressADView);
        nativeExpressADView.render();
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {
       Log.i(TENCENT_LOG,"onRenderFail");
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
        if (adContainer != null && adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
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


    public void setWebView(){

        mWebSettings = mWebview.getSettings();
        mWebSettings.setTextSize(WebSettings.TextSize.NORMAL);
        // 设置与Js交互的权限
        mWebSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //防止中文乱码
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        // mWebView.loadUrl("file:///android_asset/image.html");
        //mWebView.loadUrl("http://www.toutiao.com/a6401738581286682881/#p=1");
        //载入js
        // mWebview.addJavascriptInterface(new JavascriptInterface(this,htmlStr), "imagelistner");


        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //这段js函数的功能就是注册监听，遍历所有的img标签，并添加onClick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
                mWebview.loadUrl("javascript:(function(){"
                        + "var objs = document.getElementsByTagName(\"img\"); "
                        + "for(var i=0;i<objs.length;i++)  " + "{"
                        + "    objs[i].onclick=function()  " + "    {  "
                        + "        window.imagelistner.openImage(this.src);  "
                        + "    }  " + "}" + "})()");
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
            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);
            }
        }
    }

    Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==100){
                mProgressDialog.dismiss();
                String[] imageUrls= Util.returnImageUrlsFromHtml(content.toString());
                mWebview.addJavascriptInterface(new JavascriptInterface(MagazineContentActivity.this,imageUrls), "imagelistner");
                mWebview.loadData(content.toString(), "text/html; charset=UTF-8", null);
                //设置广告
                setAd();
            }
            if (msg.what==101){
                mProgressDialog.dismiss();
                String error="<h3>抱歉，该篇文章暂时无法阅读！<h3>";
                mWebview.loadData(error, "text/html; charset=UTF-8", null);
                //设置广告
                setAd();
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWebview.canGoBack()){
                mWebview.goBack();
            }
            else {
                finish();
            }

        }
        return true;
    }


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
