package com.cxy.yuwen.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;

import com.cxy.yuwen.Adapter.DataAdapter;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.RecyclerAdapter;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineDirectoryActivity extends AppCompatActivity {
    private String httpUrl="";
    private String magazineTitle="",magazineIntro="",magazineTime="",magazineHistoryHref="";
    private List<HashMap> dataList;
    private DataAdapter dataAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    @BindView(R.id.rv_directory)  LRecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_directory);
        ButterKnife.bind(this);
        httpUrl=getIntent().getStringExtra("href");
        dataList=new ArrayList<HashMap>();
        setRecycleView();
        Thread thread=new GetData();
        thread.start();
    }

    public void setRecycleView(){
        //创建线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //垂直方向
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        dataAdapter=new DataAdapter(dataList,this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(dataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLoadMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);

    }

    class GetData extends Thread {
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element introDiv=docHtml.getElementsByClass("magBox1").first();
                magazineTime=introDiv.getElementsByTag("p").first().text();
                magazineIntro=introDiv.getElementsByClass("rec").first().getElementsByTag("p").first().text();
                magazineTitle=docHtml.getElementsByTag("h3").first().text();
                magazineHistoryHref=docHtml.getElementsByClass("btn_history act_history").first().attr("href");   //没有前缀

                Element dirDiv=docHtml.getElementById("dirList");  //目录div
                Elements dirElements=dirDiv.getElementsByClass("dirItem02");
                for (Element dirElement : dirElements){
                    String subTitle=dirElement.getElementsByTag("h5").first().text();
                    HashMap titleMap=new HashMap<String,String>();
                    titleMap.put("type","title");
                    titleMap.put("text",subTitle);
                    dataList.add(titleMap);

                    Elements lis=dirElement.getElementsByTag("ul").first().getElementsByTag("li");
                    for (Element li : lis){
                        String text=li.getElementsByTag("a").first().text();
                        String href=li.getElementsByTag("a").first().attr("href");
                        HashMap dirMap=new HashMap<String,String>();
                        dirMap.put("type","item");
                        dirMap.put("text",text);
                        dirMap.put("href",href);
                        dataList.add(dirMap);
                    }
                }

                handler.sendEmptyMessage(100);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what==100){
            mLRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
};


}
