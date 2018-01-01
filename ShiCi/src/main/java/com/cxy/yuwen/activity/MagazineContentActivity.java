package com.cxy.yuwen.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cxy.yuwen.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineContentActivity extends BasicActivity {

    private String httpUrl="";
    private WebSettings mWebSettings;
    private String  htmlData="";
    @BindView(R.id.wv_content)  WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_content);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        httpUrl=getIntent().getStringExtra("url").replace("page","news").replace("shtml","html");
        setWebView();
      /*  Thread thread=new GetHtml();
        thread.start();*/

    }

    public void setWebView(){
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


    }

    class GetHtml extends Thread{
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element mainDiv=docHtml.getElementsByClass("main").first();
                Elements images=mainDiv.getElementsByTag("img");
                for (Element image : images) {
                    image.attr("width","90%").attr("height","auto");
                }
                htmlData=mainDiv.toString();
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
               mWebview.loadDataWithBaseURL(null,htmlData,"text/html","utf-8",null);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }


}
