package com.cxy.yuwen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.yuwen.Adapter.ImageTextAdapter;
import com.cxy.yuwen.R;
import com.cxy.yuwen.activity.MagazineDirectoryActivity;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MagzineHistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private List<HashMap> dataDisplayList;

    private String httpUrl;
    private Unbinder unbinder;

    @BindView(R.id.rv_magazine_history)
    LRecyclerView mRecyclerView;

    private ImageTextAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

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
        Thread thread=new getHtml();
        thread.start();
        return view;
    }
    public void setmRecyclerView(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        recycleViewAdapter=new ImageTextAdapter(getContext(),dataDisplayList);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), MagazineDirectoryActivity.class);
                intent.putExtra("href",MagazineFragment.MAGAZIENE_URL+dataDisplayList.get(position).get("href").toString());
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
                Document docHtml = Jsoup.connect(httpUrl).get();
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if (msg.what==100){
               mLRecyclerViewAdapter.notifyDataSetChanged();
           }
        }
    };




}
