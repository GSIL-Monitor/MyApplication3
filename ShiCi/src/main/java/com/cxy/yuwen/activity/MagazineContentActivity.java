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
    private String htmlStr="<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "body{margin-left:15px;margin-right:12px;}h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:180%;margin-top:30px;} img{width:100%;}  .sj{font-size:15px;color:#a6a5a5;}"
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

    /* public void setWebView(){
        mWebSettings = mWebview.getSettings();
        mWebSettings.setDefaultFontSize(22);
        //支持缩放，默认为true。
       // mWebSettings .setSupportZoom(false);
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

    }*/

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






}
