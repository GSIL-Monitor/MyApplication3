package com.cxy.yuwen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.fragment.MagazineFragment;
import com.cxy.yuwen.tool.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineActivity extends BasicActivity {

    @BindView(R.id.allList) RecyclerView dataRecycleView;
    private String htmlUrl="";
    private List<HashMap> dataList;
    private RecycleViewAdapter recycleViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine);
        ButterKnife.bind(this);

        Intent intent=this.getIntent();
        htmlUrl=intent.getStringExtra("url");
        dataList=new ArrayList<HashMap>();

        //设置RecycleView
        //设置RecycleView布局为网格布局 3列
        dataRecycleView.setLayoutManager(new GridLayoutManager(this,2));
        recycleViewAdapter=new RecycleViewAdapter();
        dataRecycleView.setAdapter(recycleViewAdapter);

        Thread thread=new getHtml();
        thread.start();

    }


    class getHtml extends  Thread {
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(htmlUrl).get();
                Elements uls=docHtml.getElementsByClass("rowWrap mb20");
                for (Element ul : uls){
                    Elements lis=ul.getElementsByTag("li");
                    for (Element li : lis){
                        HashMap map=new HashMap<String,String>();
                        Element a=li.select("p.pel_m_pic").get(0).getElementsByTag("a").get(0);
                        map.put("href",a.attr("href"));
                        map.put("imageSrc",a.getElementsByTag("img").get(0).attr("src"));

                        Element p2=li.select("p.pel_name").get(0);
                        map.put("name",p2.text());

                        Element p3=li.select("p.pel_time").get(0);
                        map.put("time",p3.text());

                        dataList.add(map);

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
            switch (msg.what) {
                case 100:
                    recycleViewAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };


    class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder>{
        Bitmap bitmap=null;
        Handler adapterHandler=null;
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(MagazineActivity.this).inflate(R.layout.magazine_cover_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final HashMap hashMap=dataList.get(position);
            holder.tvCoverName.setText(hashMap.get("name").toString());
            holder.tvCoverOrder.setText(hashMap.get("time").toString());

            adapterHandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 101:
                            holder.imCover.setImageBitmap(bitmap);
                            break;
                    }

                }
            };
            //根据url获取图片
            new Thread(){
                @Override
                public void run() {
                  bitmap=Util.getbitmap(hashMap.get("imageSrc").toString());
                    adapterHandler.sendEmptyMessage(101);
                }
            }.start();


        }



        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            @BindView(R.id.coverName)  TextView tvCoverName;
            @BindView(R.id.coverOrder) TextView tvCoverOrder;
            @BindView(R.id.coverImage) ImageView imCover;



            public MyViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);

            }
        }
    }
}
