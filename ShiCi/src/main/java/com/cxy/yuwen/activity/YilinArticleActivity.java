package com.cxy.yuwen.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.Util;
import com.cxy.yuwen.tool.YilinAdapter;
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

import java.io.IOException;
import java.util.List;


public class YilinArticleActivity extends BasicActivity {

    private TextView articleTitle,articleContent,articleWriter,aritcleSource;
    private String url="",title="",content="",writer="",source="",cacheKey="";
    private static final int LOAD_FINISHED=100;
    //private ACache mCache;
    private static final String AD_TAG="youmi";
    private final static String APP_POSITION_ID ="b210a2197a21fc8ca36235cfebd403f9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yilin_article);
        initView();
        MyApplication.getInstance().addActivity(this);
        Intent intent=getIntent();
        url= intent.getStringExtra("url");

        String[] urlArray=url.split("/");
        cacheKey=urlArray[urlArray.length-1];

        Thread thread=new Thread(runnable);
        thread.start();
    }

    public void initView(){
        articleTitle=(TextView) findViewById(R.id.articleTitle);
        articleContent=(TextView)findViewById(R.id.articleContent);
        articleWriter=(TextView)findViewById(R.id.articleWriter);
        aritcleSource=(TextView)findViewById(R.id.articleSource);

      //  mCache=ACache.get(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
       // setAd();
        setXiaomiAd();
    }

    public void setXiaomiAd(){
        final ViewGroup adContainer = (ViewGroup) findViewById(R.id.adContainer);
        final StandardNewsFeedAd standardNewsFeedAd = new StandardNewsFeedAd(this);
        adContainer.post(new Runnable() {
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
                            standardNewsFeedAd.buildViewAsync(response, adContainer.getWidth(), new AdListener() {
                                @Override
                                public void onAdError(AdError adError) {
                                    Log.e(TAG, "error : remove all views");
                                    adContainer.removeAllViews();
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




    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                StringBuilder sb=new StringBuilder();

                String html=mCache.getAsString(cacheKey);
                if (html==null){
                    //爬取文章详情
                    Document doc = Jsoup.connect(url).get();
                    html=doc.toString();
                    mCache.put(cacheKey,html,20* ACache.TIME_DAY); //缓存20天
                }
                //爬取文章详情
                Document doc = Jsoup.parse(html);
                //爬取文章主文档
                Element mainDoc=doc.getElementsByClass("blkContainer").first();
                //爬取文章标题
                title=mainDoc.getElementsByTag("h1").get(0).text();
                //作者和文章来源
                writer=mainDoc.getElementById("pub_date").text();
                source=mainDoc.getElementById("media_name").text();
                //爬取文章内容
                Elements pList=mainDoc.getElementsByTag("p");
                for (int i=0;i<pList.size();i++){
                    sb.append(pList.get(i).text()).append("\n\n");
                }

                content=sb.toString();
                handler.sendEmptyMessage(LOAD_FINISHED);


            } catch (IOException e) {
                e.printStackTrace();
                Util.toastMessage(YilinArticleActivity.this,e.getMessage());
            }


        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==LOAD_FINISHED){
                articleTitle.setText(title);
                articleContent.setText(content);
                articleWriter.setText(writer);
                aritcleSource.setText(source);

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
