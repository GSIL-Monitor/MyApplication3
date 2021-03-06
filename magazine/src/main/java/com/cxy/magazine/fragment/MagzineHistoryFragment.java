package com.cxy.magazine.fragment;

import android.content.Intent;
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
import com.cxy.magazine.activity.MagazineDirectoryActivity;
import com.cxy.magazine.adapter.ImageTextAdapter;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
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
import android.support.v4.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MagzineHistoryFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private ArrayList<HashMap> dataDisplayList;

    private String httpUrl;
    private Unbinder unbinder;

    @BindView(R.id.rv_magazine_history)
    LRecyclerView mRecyclerView;

    private ImageTextAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private MyHandler uiHandler;

    public MagzineHistoryFragment() {
        // Required empty public constructor
    }


    public static MagzineHistoryFragment newInstance(String url) {
        MagzineHistoryFragment fragment = new MagzineHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            httpUrl = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_magzine_history, container, false);
        unbinder= ButterKnife.bind(this,view);
        dataDisplayList=new ArrayList<HashMap>();
        setmRecyclerView();
        uiHandler=new MyHandler(this);
        Thread thread=new getHtml();
        thread.start();
        return view;
    }
    public void setmRecyclerView(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        recycleViewAdapter=new ImageTextAdapter(getContext(),dataDisplayList,manager);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //直接跳转到目录界面
                Intent intent=new Intent(getActivity(), MagazineDirectoryActivity.class);
                intent.putExtra("href",ClassFragment.MAGAZIENE_URL+dataDisplayList.get(position).get("href").toString());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class getHtml extends Thread{
        @Override
        public void run() {
            try {
                String html= OkHttpUtil.get(httpUrl);
                Document docHtml = Jsoup.parse(html);
                Element ul=docHtml.getElementsByClass("results").first();
                Elements resultes=ul.getElementsByTag("a");
                for (Element a :resultes){
                  HashMap map=new HashMap();
                  map.put("name",a.getElementsByTag("span").first().text());
                  map.put("time",a.getElementsByTag("span").get(1).text());
                  map.put("href",a.attr("href"));
                  map.put("imageSrc",a.getElementsByTag("img").first().attr("src"));

                  dataDisplayList.add(map);
                  uiHandler.sendEmptyMessage(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);
            }

        }
    }

    private static class MyHandler extends Handler{
        private final WeakReference<MagzineHistoryFragment> fragmentWeakReference;
        private MyHandler(MagzineHistoryFragment historyFragment){
            fragmentWeakReference=new WeakReference<>(historyFragment);
        }
        @Override
        public void handleMessage(Message msg) {
            MagzineHistoryFragment historyFragment=fragmentWeakReference.get();
            if (historyFragment!=null){
                if (msg.what==100){
                    historyFragment.mLRecyclerViewAdapter.notifyDataSetChanged();
                }
                if(msg.what==101){
                    Utils.toastMessage(historyFragment.context,"出错了,该内容暂时无法查看！");
                }
            }

        }
    };




}
