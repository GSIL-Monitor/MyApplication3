package com.cxy.childstory.activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxy.childstory.R;
import com.cxy.childstory.base.BaseActivity;
import com.cxy.childstory.model.PageBean;
import com.cxy.childstory.model.ReturnBody;
import com.cxy.childstory.model.Story;
import com.cxy.childstory.model.StoryType;
import com.cxy.childstory.utils.Constants;
import com.cxy.childstory.utils.HttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryTypeDetailActivity extends BaseActivity {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
   // private static final int PAGE_SIZE = 10;
    private String typeName="";
    private int currentPage=1;
    private int totalPage=1;
    private StoryAdapter storyAdapter=null;
    private ArrayList<Story> storyList=new ArrayList<>();
    private static final String LOG_TAG="STORY_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_type_detail);
        ButterKnife.bind(this);

        typeName=getIntent().getStringExtra("typeName");
        initTopBar();
        initRecyclerView();
        initRefreshLayout();
        mSwipeRefreshLayout.setRefreshing(true);
        refresh();

    }

    private void initTopBar() {
        mTopBar.setTitle(typeName);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTopBar.addRightImageButton(R.drawable.ic_search_white,R.id.topbar_right_search_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    private void initRecyclerView() {
        storyAdapter = new StoryAdapter(R.layout.recycler_story_list_item);
        storyAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
               //   Toast.makeText(StoryTypeDetailActivity.this,"开始请求数据",Toast.LENGTH_LONG).show();
                    ObtainData obtainData=new ObtainData();
                    obtainData.execute();

            }
        }, mRecyclerView);
        storyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Story story=(Story) adapter.getData().get(position);
                Intent intent=new Intent(StoryTypeDetailActivity.this,StoryDetailActivity.class);
                intent.putExtra("story",story);
                startActivity(intent);
            }
        });
        storyAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(storyAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }
    private void refresh(){
        currentPage=1;
        //这里的作用是防止下拉刷新的时候还可以上拉加载
        storyAdapter.setEnableLoadMore(false);
        ObtainData obtainData=new ObtainData();
        obtainData.execute();
    }


    private void setData(boolean isRefresh, List data) {
        if (isRefresh) {
            storyAdapter.setNewData(data);
        } else {
            if (data.size() > 0) {
                storyAdapter.addData(data);
            }
        }
        if (currentPage<totalPage) {
            //本次数据加载结束并且还有下页数据
            storyAdapter.loadMoreComplete();
        } else {
            //加载结束,没有数据了
            storyAdapter.loadMoreEnd();
           // Toast.makeText(this, "no more data", Toast.LENGTH_SHORT).show();
        }
        currentPage++;
    }
    private  class ObtainData extends AsyncTask<Void, Integer, String>{
        private  String obtainUrl= Constants.DOMAIN+"/story/selectbytype";
        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("type",typeName);
            params.put("page",currentPage);

            try {
                String result= HttpUtil.get(obtainUrl,params);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(LOG_TAG,s);
            //不再刷新
            mSwipeRefreshLayout.setRefreshing(false);
            //开启加载更多
            storyAdapter.setEnableLoadMore(true);
            if (s!=null){
                Type type = new TypeReference<ReturnBody<PageBean<Story>>>() {}.getType();
                ReturnBody<PageBean<Story>> returnBody= JSON.parseObject(s,type);
                if (returnBody.getErrorCode().equals("0000")){
                    PageBean<Story> pageBean=returnBody.getData();
                    totalPage=pageBean.getTotalPages();
                    List<Story> list=pageBean.getContent();
                    boolean isRefresh =currentPage ==1;
                    setData(isRefresh,list);
                }else {
                   storyAdapter.loadMoreFail();
                }
            }
        }
    }

    private class StoryAdapter extends BaseQuickAdapter<Story,BaseViewHolder>{

        public StoryAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, Story item) {
             helper.setText(R.id.tv_story_title,item.getTitle());
             String summaray="";
             if (!TextUtils.isEmpty(item.getSummary())){
                 String dataSummary=item.getSummary();
                 if (dataSummary.length()>40){
                     summaray=dataSummary.substring(0,40)+"...";
                 }else{
                     summaray=dataSummary;
                 }
             }
             helper.setText(R.id.tv_story_summary,summaray);
             String imageUrl=item.getImagePath();
             ImageView imageView=helper.getView(R.id.iv_story);
             if (TextUtils.isEmpty(imageUrl)){
                 Glide.with(StoryTypeDetailActivity.this).load(R.mipmap.default_story).into(imageView);
                 imageView.setVisibility(View.GONE);
             }else{
                 imageView.setVisibility(View.VISIBLE);
                 Glide.with(StoryTypeDetailActivity.this).load(imageUrl).placeholder(R.mipmap.default_story).into(imageView);
             }
             if (item.getAudioPaths()==null || item.getAudioPaths().size()<=0){
                 ImageView audioImage=helper.getView(R.id.iv_audio);
                 audioImage.setVisibility(View.GONE);
             }
        }
    }
}
