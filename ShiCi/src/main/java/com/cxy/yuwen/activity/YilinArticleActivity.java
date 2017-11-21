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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.Util;
import com.cxy.yuwen.tool.YilinAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import ddd.eee.fff.nm.bn.BannerManager;
import ddd.eee.fff.nm.bn.BannerViewListener;

public class YilinArticleActivity extends BasicActivity {

    private TextView articleTitle,articleContent,articleWriter,aritcleSource;
    private String url="",title="",content="",writer="",source="",cacheKey="";
    private static final int LOAD_FINISHED=100;
    private ACache mCache;
    private static final String AD_TAG="youmi";

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

        mCache=ACache.get(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
        setAd();
    }

    /**
     * 设置广告条
     */
    public void setAd(){
        // 获取广告条
        View bannerView = BannerManager.getInstance(this).getBannerView(this, new BannerViewListener() {
            @Override
            public void onRequestSuccess() {
                Log.i(AD_TAG,"请求广告条成功");
            }

            @Override
            public void onSwitchBanner() {
                Log.i(AD_TAG,"广告条切换");
            }

            @Override
            public void onRequestFailed() {
                Log.e(AD_TAG,"请求广告条失败");
            }

        });

         // 获取要嵌入广告条的布局
        LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);

        // 将广告条加入到布局中
        bannerLayout.addView(bannerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(this).onDestroy();
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
