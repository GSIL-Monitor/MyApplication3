package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineDetailActivity;
import com.cxy.magazine.adapter.SearchAdapter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.rv_allData)   LRecyclerView mRecyclerView;
    private SearchAdapter dataAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private static final String  DATA_URL="http://www.fx361.com/common/all.html";
    private static final String YILIN_URL="http://www.fx361.com";
    private List<HashMap> datalist=null;
    private List<HashMap> dataShowList=null;
    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        datalist=new ArrayList<HashMap>();
        dataShowList=new ArrayList<HashMap>();
        setRecyclerView();
        setSearchView();

        Thread thread=new GetAllData();
        thread.start();
        return  view;
    }

    public void setRecyclerView(){
        dataAdapter=new SearchAdapter(dataShowList,getContext());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(dataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(getContext())
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mRecyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), MagazineDetailActivity.class);
                HashMap hashMap=dataShowList.get(position);
                String href=hashMap.get("href").toString();
                intent.putExtra("href",YILIN_URL+href);
                startActivity(intent);
            }
        });

    }
    @OnClick(R.id.iv_back)
    public void back(){
       getActivity().getSupportFragmentManager().popBackStack();//suport.v4包


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void setSearchView(){
        //SearchView 自动展开并且弹出输入法
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            //当搜索内容改变时触发
            @Override
            public boolean onQueryTextChange(String newText) {
                /*if (!CommonUtil.isEmpty(newText)){
                    setDataShowList(newText);
                }*/
                setDataShowList(newText);
                return true;
            }
        });
    }

    public void setDataShowList(String input){
        dataShowList.clear();
        String pattern = ".*"+input+".*";     // ".*abc.*"
        //正则匹配
       for (int i=0;i<datalist.size();i++){
           String content=datalist.get(i).get("text").toString();
           if (Pattern.matches(pattern,content)){
               dataShowList.add(datalist.get(i));
           }
       }

       mLRecyclerViewAdapter.notifyDataSetChanged();




    }


    class  GetAllData extends Thread{
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(DATA_URL).get();
                Elements aList=docHtml.getElementsByTag("a");
                for (Element a : aList){
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("text",a.text());
                    map.put("href",a.attr("href"));
                    map.put("type","item");
                    datalist.add(map);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
