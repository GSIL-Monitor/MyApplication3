package com.example.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsreader.bean.NewsBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        setTitle(R.string.app_name);

        //接收intent传递过来的数据
        Intent intent=this.getIntent();
        final NewsBean news=(NewsBean)intent.getSerializableExtra("news");

        TextView titleView=(TextView)findViewById(R.id.news_title);
        TextView pubDateView=(TextView)findViewById(R.id.news_pubDate);
        final WebView webView=(WebView)findViewById(R.id.newsDetail);

        titleView.setText(news.title);

        //显示新闻发布时间
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d=sdf.parse(news.pubDate);

            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
            String s=sdf2.format(d);
            pubDateView.setText("(发布日期："+s+")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView参数设置(是否支持多窗口，是否支持缩放)
        WebSettings settings=webView.getSettings();
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(false);
        //加载显示新闻描述内容
        webView.loadDataWithBaseURL(null,news.description,null,"utf-8",null);

        //返回动作，单击返回则结束当前NewsActivity
        ImageView back=(ImageView)findViewById(R.id.imageViewBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //单击浏览新闻URL对应的详细界面
        ImageView browser=(ImageView)findViewById(R.id.imageViewBrowser);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(news.link);
            }
        });



    }

}
