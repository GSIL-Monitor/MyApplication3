package com.cxy.yuwen.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.Divider;
import com.cxy.yuwen.tool.ParcelableMap;
import com.cxy.yuwen.tool.Util;
import com.cxy.yuwen.tool.YilinAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import cn.bmob.v3.http.bean.Init;

public class YilinDirectoryActivity extends BasicActivity {
    private static final String YILIN_URL="http://www.92yilin.com/";
    private String url="",directoryUrl="";
    private ArrayList list=new ArrayList<ParcelableMap>();
    private TextView tvTitle;
    private RecyclerView recyclerView;
    private YilinAdapter yilinAdapter;
    private static  final int LOAD_FINISHED=100;
    private String title="";
    private ACache mCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yilin_directory);
        MyApplication.getInstance().addActivity(this);
        mCache = ACache.get(this);
        Intent intent=getIntent();
        url=YILIN_URL+intent.getStringExtra("url");  //爬虫目录完全地址
        directoryUrl=intent.getStringExtra("url").split("/")[0];      //2017_05_zw 用作缓存的key
        initView();
        Thread pachong=new Thread(runnable);
        pachong.start();
    }
   public void   initView(){
       tvTitle=(TextView) findViewById(R.id.directory_title);
       recyclerView=(RecyclerView)findViewById(R.id.directory_rv);
       ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

       //设置RecycleView
       //设置固定大小
       recyclerView.setHasFixedSize(true);
       //创建线性布局
       LinearLayoutManager layoutManager = new LinearLayoutManager(YilinDirectoryActivity.this);
       //垂直方向
       layoutManager.setOrientation(OrientationHelper.VERTICAL);
       //给RecyclerView设置布局管理器
       recyclerView.setLayoutManager(layoutManager);
       //如果确定每个item的高度是固定的，设置这个选项可以提高性能
       recyclerView.setHasFixedSize(true);
       //添加间隔线
       Divider divider = new Divider(new ColorDrawable(0xffcccccc), OrientationHelper.VERTICAL);
       //单位:px
       divider.setMargin(8, 8, 8, 0);
       divider.setHeight(2);
       recyclerView.addItemDecoration(divider);
       yilinAdapter=new YilinAdapter(this,list,YilinAdapter.ARTICLE_FLAG,directoryUrl);
       recyclerView.setAdapter(yilinAdapter);


   }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {

                String html=mCache.getAsString(directoryUrl);
                if (html==null){
                    Document docHtml = Jsoup.connect(url).get();
                    mCache.put(directoryUrl,docHtml.toString(),20* ACache.TIME_DAY);
                    html=docHtml.toString();
                }
                Document doc = Jsoup.parse(html);
                //获取标题
                title= doc.getElementsByTag("h1").get(0).text();
                Elements spans=doc.getElementsByTag("span");  //获取所有<span>元素
                for (int i=0;i<spans.size();i++){

                    Elements contents=spans.get(i).getElementsByTag("a");
                    if (contents.size()>0){  //详细目录
                        HashMap<String,String> smallMap=new HashMap<String,String>();//boldMap
                        smallMap.put("type","2");   //详细目录
                        smallMap.put("text",contents.get(0).text());
                        smallMap.put("href",contents.get(0).attr("href"));

                        ParcelableMap parcelableMap1=new ParcelableMap(smallMap);
                        list.add(parcelableMap1);
                    }else{ //大目录
                        HashMap<String,String> boldMap=new HashMap<String,String>();
                        boldMap.put("type","1");   //大目录
                        boldMap.put("text",spans.get(i).text());
                        ParcelableMap parcelableMap1=new ParcelableMap(boldMap);
                        list.add(parcelableMap1);
                    }
                }
                handler.sendEmptyMessage(LOAD_FINISHED);
            } catch (IOException e) {
                e.printStackTrace();
                Util.toastMessage(YilinDirectoryActivity.this,e.getMessage());
            }
        }
    };

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_FINISHED:
                    tvTitle.setText(title);
                    yilinAdapter.notifyDataSetChanged();
                    break;
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


