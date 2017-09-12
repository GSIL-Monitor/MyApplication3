package com.example.newsreader;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.newsreader.bean.HttpJson;
import com.newsreader.bean.NewsBean;
import com.newsreader.bean.News_adapter;

import java.util.List;


/**
 * Created by cxy on 2016/4/20.
 */
public class Fragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final int MSG_NEWS_LOADED = 100;   //指示Rss新闻数据已获取
    private View layoutView;
    private ProgressDialog pd;
    private List<NewsBean> newsList;  //新闻条目数组
    private ListView listView1;
    private SwipeRefreshLayout refresh_layout = null;//刷新控件
    private News_adapter adapter;
    private Handler handler;

    String httpUrl = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news";
    String httpArg = "channelId=5572a109b3cdc86cf39001db&needHtml=1";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment1, null);

       Log.i("life","onCreateView被执行");
        listView1 = (ListView) layoutView.findViewById(R.id.listView);
        refresh_layout = (SwipeRefreshLayout) layoutView.findViewById(R.id.refresh_layout);
        // refresh_layout.setProgressBackgroundColorSchemeColor(Color.GRAY);
        refresh_layout.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_red_light);//设置跑动的颜色值

        //创建并显示一个进度条，设定可以被用户打断
        pd = ProgressDialog.show(getActivity(), "请稍候...", "正在加载数据", true, true);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
               // System.out.println("接收到了消息");
                pd.dismiss();
                refresh_layout.setRefreshing(false);
                adapter = new News_adapter(getActivity(),newsList);
                listView1.setAdapter(adapter);
            }
        };

        connect();


        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Fragment1.this.getActivity(), NewsActivity.class);

                NewsBean news = newsList.get(position);
                intent.putExtra("news", news);
                //  intent.putExtra("title","国内焦点");
                startActivity(intent);
            }
        });
        refresh_layout.setOnRefreshListener(this);//设置下拉的监听



        return layoutView;
    }

    public void connect(){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                newsList= HttpJson.httpRequest(httpUrl+"?"+httpArg);
                handler.sendEmptyMessage(1);
            }
        }).start();



    }


//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i("life","onPause被执行");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i("life","onDestroy被执行");
//    }

    @Override
    public void onRefresh() {
//        String url = httpUrl + "?" + httpArg;
//        final HttpJson httpJson=new HttpJson(listView1,  adapter,  url, handler,pd,refresh_layout,getActivity());
//        httpJson.start();

          connect();
    }

}
