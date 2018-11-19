package com.cxy.magazine.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineDetailActivity;
import com.cxy.magazine.adapter.ImageTextAdapter;
import com.cxy.magazine.adapter.MagazineListAdapter;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.JsonArray;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MagazineListFragment extends BaseFragment {

    private static final String HTTP_URL = "param1";
    private String htmlUrl;
  //  public static final String MAGAZIENE_URL="http://www.fx361.com";

    /**服务器端一共多少条数据*/
    private  int TOTAL_COUNTER = 0;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**当前指针*/
    private int mCurrentCounter = 0;
    private MagazineListAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;



    private ArrayList<HashMap<String,String>> dataArray=new ArrayList();     //总数据

    private  ArrayList<HashMap<String,String>> dataDisplayArray=new ArrayList();


    private Context context;
    private Unbinder unbinder;
    private String cacheKey="";

   @BindView(R.id.allList)
   LRecyclerView mRecyclerView;
   private   GridLayoutManager manager=null;


    public MagazineListFragment() {

    }


    public static MagazineListFragment newInstance(String httpUrl) {
        MagazineListFragment fragment = new MagazineListFragment();
        Bundle args = new Bundle();
        args.putString(HTTP_URL, httpUrl);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment 生命周期  onCreate()在OnCreateView()之前
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            htmlUrl = getArguments().getString(HTTP_URL);
            String[] tempArray=htmlUrl.split("//")[1].split("/");
            cacheKey=tempArray[1]+tempArray[2].split(".html")[0]+"List";

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View layoutView=inflater.inflate(R.layout.fragment_magazine_list, container, false);
         unbinder=ButterKnife.bind(this,layoutView);
         context=this.getContext();


      //   mHandler = new PreviewHandler(this);
         setRecyclerView();
         return layoutView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private  void setRecyclerView(){
        //设置RecycleView布局为网格布局 2列
        manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, android.R.color.darker_gray,android.R.color.white);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, android.R.color.darker_gray ,android.R.color.white);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
        mRecyclerView.setHasFixedSize(true);

        //mLRecyclerViewAdapter.addHeaderView(new SampleHeader(this));

        //下拉刷新
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                requestData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more

                    addItems();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });


     setAdapter();



    }
   public void setAdapter(){

       recycleViewAdapter=new MagazineListAdapter(getContext(),dataDisplayArray,manager);
       mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
       mRecyclerView.setAdapter(mLRecyclerViewAdapter);
       //设置点击事件
       mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {

                   //跳转目录页
                   Intent intent=new Intent(getActivity(), MagazineDetailActivity.class);
                   HashMap<String,String> dataMap=dataDisplayArray.get(position);
                   //   HashMap hashMap=dataDisplayList.get(position);
                   String href=dataMap.get("href");
                   String time=dataMap.get("time");  // 2018年42期
                   String timeTemp=time.replace("年","").replace("期","");
                   String directoryUrl=href.replace("index",timeTemp);
                   intent.putExtra("href",directoryUrl);
                   startActivity(intent);


           }
       });

       Object cacheArray= mAcache.getAsObject(cacheKey);

       if (cacheArray!=null){
           dataArray=(ArrayList<HashMap<String,String>>) cacheArray;
           TOTAL_COUNTER=dataArray.size();
           addItems();
        /*   mRecyclerView.refreshComplete(REQUEST_COUNT);
           mLRecyclerViewAdapter.notifyDataSetChanged();*/
       }else{
           //缓存为空，获取数据
           mRecyclerView.refresh();
       }
   }



   private void addItems(){
       int currentSize = mCurrentCounter;
       for (int i = currentSize; i < currentSize+REQUEST_COUNT; i++){

               if (i<dataArray.size() && dataArray.get(i)!=null){
                   dataDisplayArray.add(dataArray.get(i));
                   mCurrentCounter += 1;
               }

       }
       if (mRecyclerView!=null){
           mRecyclerView.post(new Runnable() {
               public void run() {
                   mLRecyclerViewAdapter.notifyDataSetChanged();
                   mRecyclerView.refreshComplete(REQUEST_COUNT);  //刷新完成
               }
           });
       }


   }





    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    //重新获取数据
                    mCurrentCounter=0;
                    //清空数据
                    dataDisplayArray.clear();
                    addItems();


                    break;
                case 101:   //发生错误
                   // Utils.toastMessage(getActivity(),"出错了，请稍后再试");
                    Utils.showTipDialog(context,"加载数据失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);
                    mRecyclerView.refreshComplete(0);// REQUEST_COUNT为每页加载数量
                    mLRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    };


    /**
     * 请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {

                dataArray.clear();

                try {
                    if (htmlUrl.equals("http://www.fx361.com/bk/hqsb/")){  //环球时报，特殊处理
                      //  JSONObject jsonObject=new JSONObject();
                        HashMap<String,String> objectMap=new HashMap<>();
                        String html= OkHttpUtil.get(htmlUrl);
                        Document docHtml = Jsoup.parse(html);
                        Element introDiv = docHtml.getElementsByClass("magBox1").first();
                        String magazineTime = introDiv.getElementsByTag("p").first().text();
                        String coverImageUrl = introDiv.getElementsByTag("a").first().attr("href");
                        String magazineTitle = docHtml.getElementsByTag("h3").first().text();
                       //String href = docHtml.getElementsByClass("btn_history act_history").first().attr("href");   //没有前缀
                        objectMap.put("name",magazineTitle);
                        objectMap.put("href","http://www.fx361.com/bk/hqsb/index.html");
                        objectMap.put("imageSrc",coverImageUrl);
                        objectMap.put("time",magazineTime);
                        dataArray.add(objectMap);


                    }else{
                        String html= OkHttpUtil.get(htmlUrl);
                        Document docHtml = Jsoup.parse(html);
                        Elements uls=docHtml.getElementsByClass("rowWrap mb20");
                        for (Element ul : uls){
                            Elements lis=ul.getElementsByTag("li");
                            for (Element li : lis){
                                HashMap<String,String> jsonMap=new HashMap<>();
                                Element a=li.select("p.pel_m_pic").get(0).getElementsByTag("a").get(0);

                                jsonMap.put("href",ClassFragment.MAGAZIENE_URL+a.attr("href"));
                                jsonMap.put("imageSrc",a.getElementsByTag("img").get(0).attr("src"));

                                Element p2=li.select("p.pel_name").get(0);
                                jsonMap.put("name",p2.text());

                                Element p3=li.select("p.pel_time").get(0);
                                jsonMap.put("time",p3.text());
                                dataArray.add(jsonMap);


                            }
                        }
                    }

                    //缓存数据
                    mAcache.put(cacheKey,dataArray);

                    TOTAL_COUNTER=dataArray.size();
                    handler.sendEmptyMessage(100);


                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(101);
                    return;

                }




            }
        }.start();
    }








}
