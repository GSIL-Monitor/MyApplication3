package com.example.newsreader;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.newsreader.bean.NewsBean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.LogRecord;


/**
 * Created by cxy on 2016/4/20.
 */
public class Fragment1 extends Fragment {

    private static final int NODE_CHANEEL=0;
    private static final int NODE_ITEM=1;
    private static final int NODE_NONE=-1;
    private static final int MSG_NEWS_LOADED=100;   //指示Rss新闻数据已获取
    private View layoutView;
    private ProgressDialog pd;
    private List<NewsBean> newsList=new ArrayList<NewsBean>();  //新闻条目数组
    private ListView listView1;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView=inflater.inflate(R.layout.fragment1,null);
        listView1=(ListView)layoutView.findViewById(R.id.listView);

        //创建并显示一个进度条，设定可以被用户打断
        pd=ProgressDialog.show(getActivity(),"请稍候...","正在加载数据",true,true);

        adapter=new NewsAdapter(newsList);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Fragment1.this.getActivity(),NewsActivity.class);
                NewsBean news=newsList.get(position);
                intent.putExtra("news",news);
                startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {


                //通过HttpGet获取Rss数据
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://news.163.com/special/00011K6L/rss_newstop.xml");

                try {
                    HttpResponse response = client.execute(get);
                    //检查服务器返回的响应码，200表示成功
                    if (response.getStatusLine().getStatusCode() == 200) {
                        //获取网络连接的输入流。然后解析收到的RSS数据
                        InputStream stream = response.getEntity().getContent();
                        List<Map<String, String>> items = getRssItems(stream);

                        //先清空数组列表
                        newsList.clear();

                        //将解析后的Rss数据转换成Bean对象保存
                        for (Map<String, String> item : items) {
                            NewsBean news = new NewsBean();
                            news.title = item.get("title");
                            news.description = item.get("description");
                            news.link = item.get("link");
                            news.pubDate = item.get("pubDate");
                            news.guid = item.get("guid");
                            newsList.add(news);
                        }
                        //数据加载完毕，通知ListView显示
                      //  adapter.notifyDataSetChanged();
                        Message msg=mUIHandler.obtainMessage(MSG_NEWS_LOADED);
                        //向主线程发送消息时，还可以携带数据
                        //msg.obj=newsList;
                        mUIHandler.sendMessage(msg);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //销毁进度条
                pd.dismiss();

            }


        }).start();


        return layoutView;
    }

    public List<Map<String,String>> getRssItems(InputStream xml) throws Exception{
        //itemList表示新闻的列表
        List<Map<String,String>> itemList=new ArrayList<Map<String, String>>();
        Map<String,String> item=new HashMap<String, String>();
        String name,value;
        int currNode=NODE_NONE;
        XmlPullParser pullParser= Xml.newPullParser();
        pullParser.setInput(xml,"UTF-8");
        int event=pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                //节点元素开始，比如<title>
                case XmlPullParser.START_TAG:
                    name = pullParser.getName();
                    //确定当前是channel还是item节点
                    if ("channel".equalsIgnoreCase(name)) {
                        currNode = NODE_CHANEEL;
                        break;
                    } else if ("item".equalsIgnoreCase(name)) {
                        currNode = NODE_ITEM;
                        break;
                    }

                    //如果当前是在item节点中，则提取item节点的子元素，
                    if (currNode == NODE_ITEM) {
                        value = pullParser.nextText();
                        item.put(name, value);
                    }
                    break;

                //节点元素结束，比如</title>
                case XmlPullParser.END_TAG:
                    name = pullParser.getName();
                    if ("item".equals(name)) {
                        itemList.add(item);
                        item = new HashMap<String, String>();
                    }
                    break;

            }  //of switch
            //继续处理下一节点
            event = pullParser.next();
        }
        return itemList;
    }

    private Handler mUIHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_NEWS_LOADED:
                    //更新ListView显示
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    class NewsAdapter extends BaseAdapter{
        //待显示的新闻列表
        private  List<NewsBean> newsItems;

        public NewsAdapter(List<NewsBean> newsItems){
            this.newsItems=newsItems;

        }

        @Override
        public int getCount() {
            return newsItems.size();
        }

        @Override
        public Object getItem(int position) {
            return newsItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //ListView显示每条数据时，都要调用getView()方法
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.news_item,null);
            }

            //初始化行布局视图中的各个子控件
            TextView newsTitle=(TextView) convertView.findViewById(R.id.news_title);
            TextView newsDescr=(TextView) convertView.findViewById(R.id.news_description);
            TextView newsPubdate=(TextView) convertView.findViewById(R.id.news_pubDate);
            ImageView newsIcon=(ImageView)convertView.findViewById(R.id.news_icon);
            //获取第position行的数据
            NewsBean item=newsItems.get(position);
            //将第position行的数据显示到布局界面中
            newsTitle.setText(item.title);
            newsDescr.setText(item.description);
            newsPubdate.setText(item.pubDate);

            //将行布局返回给ListView组件显示
            return convertView;
        }
    }


}
