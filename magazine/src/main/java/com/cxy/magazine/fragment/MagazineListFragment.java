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
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MagazineListFragment extends BaseFragment {

    private static final String HTTP_URL = "param1";
    private String htmlUrl;
  //  public static final String MAGAZIENE_URL="http://www.fx361.com";

    /**服务器端一共多少条数据*/
    private static  int TOTAL_COUNTER = 0;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;
    private MagazineListAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

  //  private List<HashMap> dataList;   //服务器端总数据

    private JSONArray dataArray=new JSONArray();     //服务器端总数据

//    private List<HashMap> dataDisplayList;  //显示在界面上的数据
    private JSONArray dataDisplayArray=new JSONArray();
  //  private PreviewHandler mHandler ;
   // private Bitmap defaultImage=null;

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
            cacheKey=tempArray[1]+tempArray[2].split(".html")[0];

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
      //  dataList=new ArrayList<HashMap>();
      //  dataDisplayList=new ArrayList<HashMap>();

        //设置RecycleView
        //设置RecycleView布局为网格布局 2列
        manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        recycleViewAdapter=new MagazineListAdapter(getContext(),dataDisplayArray,manager);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
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
                    mRecyclerView.refreshComplete(REQUEST_COUNT);// REQUEST_COUNT为每页加载数量
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });

       setOnItemClick();


        JSONArray cacheArray=mAcache.getAsJSONArray(cacheKey);
        if (cacheArray!=null&&cacheArray.length()>0){
            dataArray=cacheArray;
            TOTAL_COUNTER=dataArray.length();
            addItems();
            mRecyclerView.refreshComplete(REQUEST_COUNT);
            mLRecyclerViewAdapter.notifyDataSetChanged();
        }else{
            mRecyclerView.refresh();
        }

    }

    public void  setOnItemClick(){
        //设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Intent intent=new Intent(getActivity(), MagazineDetailActivity.class);
                    JSONObject jsonObject=dataDisplayArray.getJSONObject(position);
                    //   HashMap hashMap=dataDisplayList.get(position);
                    String href=jsonObject.getString("href");    //hashMap.get("href").toString();
                    intent.putExtra("href",href);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }



   private void addItems(){
       int currentSize = recycleViewAdapter.getItemCount();
       for (int i = currentSize; i < currentSize+REQUEST_COUNT; i++){
           try {
               if (i<dataArray.length() && dataArray.getJSONObject(i)!=null){
                   dataDisplayArray.put(dataArray.getJSONObject(i));
                   mCurrentCounter += 1;
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
   }

    private void reSetItems(){

        for (int i = 0; i < REQUEST_COUNT; i++){
            try {
                if (i<dataArray.length() && dataArray.getJSONObject(i)!=null){
                    dataDisplayArray.put(dataArray.getJSONObject(i));
                    mCurrentCounter += 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:    //重新获取数据
                     reSetItems();
                     mRecyclerView.refreshComplete(REQUEST_COUNT);
                  //   mLRecyclerViewAdapter.notifyDataSetChanged();
                    recycleViewAdapter=new MagazineListAdapter(getContext(),dataDisplayArray,manager);

                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
                    setOnItemClick();

                    mRecyclerView.setAdapter(mLRecyclerViewAdapter);

                    break;
                case 101:   //发生错误
                    Utils.toastMessage(getActivity(),"出错了，请稍后再试");
            }
        }
    };

    /**
     * 模拟请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();
                mCurrentCounter = 0;
                dataArray=new JSONArray();
                dataDisplayArray=new JSONArray();
                try {
                    Document docHtml = Jsoup.connect(htmlUrl).get();
                    Elements uls=docHtml.getElementsByClass("rowWrap mb20");
                    for (Element ul : uls){
                        Elements lis=ul.getElementsByTag("li");
                        for (Element li : lis){
                          //  HashMap map=new HashMap<String,String>();
                            JSONObject jsonObject=new JSONObject();
                            Element a=li.select("p.pel_m_pic").get(0).getElementsByTag("a").get(0);

                            jsonObject.put("href",ClassFragment.MAGAZIENE_URL+a.attr("href"));
                            jsonObject.put("imageSrc",a.getElementsByTag("img").get(0).attr("src"));

                            Element p2=li.select("p.pel_name").get(0);


                            jsonObject.put("name",p2.text());

                            Element p3=li.select("p.pel_time").get(0);
                        //    map.put("time",p3.text());
                            jsonObject.put("time",p3.text());
                          //  dataList.add(map);
                            dataArray.put(jsonObject);


                        }
                    }

                 //   TOTAL_COUNTER=dataList.size();
                 //   dataDisplayArray=tempDisplayArray;
                    //缓存数据
                    mAcache.put(cacheKey,dataArray);
                    TOTAL_COUNTER=dataArray.length();
                    handler.sendEmptyMessage(100);

             //       throw  new  Exception("测试");


                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(101);
                    return;

                }




            }
        }.start();
    }








}
