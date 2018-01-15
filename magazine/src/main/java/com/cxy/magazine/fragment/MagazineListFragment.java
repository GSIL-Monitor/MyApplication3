package com.cxy.magazine.fragment;

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
import com.cxy.magazine.util.Util;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

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


public class MagazineListFragment extends Fragment {

    private static final String HTTP_URL = "param1";
    private String htmlUrl;
  //  public static final String MAGAZIENE_URL="http://www.fx361.com";

    /**服务器端一共多少条数据*/
    private static  int TOTAL_COUNTER = 0;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;
    private ImageTextAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private List<HashMap> dataList;   //服务器端总数据
    private List<HashMap> dataDisplayList;  //显示在界面上的数据
    private PreviewHandler mHandler = new PreviewHandler(this);
    private Bitmap defaultImage=null;

    private Context context;
    private Unbinder unbinder;

   @BindView(R.id.allList)  LRecyclerView mRecyclerView;


    public MagazineListFragment() {

    }


    public static MagazineListFragment newInstance(String httpUrl) {
        MagazineListFragment fragment = new MagazineListFragment();
        Bundle args = new Bundle();
        args.putString(HTTP_URL, httpUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            htmlUrl = getArguments().getString(HTTP_URL);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View layoutView=inflater.inflate(R.layout.fragment_magazine_list, container, false);
         unbinder=ButterKnife.bind(this,layoutView);
         context=this.getContext();
         setRecyclerView();
         return layoutView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private  void setRecyclerView(){
        dataList=new ArrayList<HashMap>();
        dataDisplayList=new ArrayList<HashMap>();
        //设置RecycleView
        //设置RecycleView布局为网格布局 2列
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);
        recycleViewAdapter=new ImageTextAdapter(getContext(),dataDisplayList);
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
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentCounter = 0;
                // recycleViewAdapter.clear();
                dataList.clear();
                dataDisplayList.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();
                requestData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    updateDisplayList();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });

        mRecyclerView.refresh();

        //设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), MagazineDetailActivity.class);
                HashMap hashMap=dataDisplayList.get(position);
                String href=hashMap.get("href").toString();
                intent.putExtra("href",href);
                startActivity(intent);

            }
        });

    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<HashMap> list) {
        dataDisplayList.addAll(list);
        mCurrentCounter += list.size();
    }

    public void updateDisplayList(){
        int currentSize = recycleViewAdapter.getItemCount();

        //模拟组装10个数据
        ArrayList<HashMap> newList = new ArrayList<HashMap>();
        for (int i = currentSize; i < currentSize+REQUEST_COUNT; i++) {
            if (newList.size() + currentSize >= TOTAL_COUNTER) {
                break;
            }


            newList.add(dataList.get(i));
        }


        addItems(newList);

        mRecyclerView.refreshComplete(REQUEST_COUNT);
    }

    private class PreviewHandler extends Handler {

        private WeakReference<MagazineListFragment> ref;

        PreviewHandler(MagazineListFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final MagazineListFragment fragment = ref.get();
            if (fragment == null || fragment.getActivity().isFinishing()) {
                return;
            }

            switch (msg.what) {
                case -1:

                    updateDisplayList();

                    break;
                case -2:
                    fragment.notifyDataSetChanged();
                    break;
                case -3:
                    fragment.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    fragment.notifyDataSetChanged();
                    fragment.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
                            dataList.clear();
                            dataDisplayList.clear();
                            requestData();

                        }
                    });
                    break;
                case 101:
                    Util.toastMessage(getActivity(),"亲，出错了，请稍后重试！");

            }
        }
    }

    /**
     * 模拟请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Document docHtml = Jsoup.connect(htmlUrl).get();
                    Elements uls=docHtml.getElementsByClass("rowWrap mb20");
                    for (Element ul : uls){
                        Elements lis=ul.getElementsByTag("li");
                        for (Element li : lis){
                            HashMap map=new HashMap<String,String>();
                            Element a=li.select("p.pel_m_pic").get(0).getElementsByTag("a").get(0);
                            map.put("href",ClassFragment.MAGAZIENE_URL+a.attr("href"));
                            map.put("imageSrc",a.getElementsByTag("img").get(0).attr("src"));

                            Element p2=li.select("p.pel_name").get(0);
                            map.put("name",p2.text());

                            Element p3=li.select("p.pel_time").get(0);
                            map.put("time",p3.text());

                            dataList.add(map);

                        }
                    }

                    TOTAL_COUNTER=dataList.size();


                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(101);
                }

                //模拟一下网络请求失败的情况
                if(Util.checkNetworkState(getActivity())) {    //网络可用
                    mHandler.sendEmptyMessage(-1);
                } else {                                 //网络不可用
                    mHandler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }








}
