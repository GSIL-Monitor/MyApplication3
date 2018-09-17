package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.CollectActivity;
import com.cxy.magazine.activity.MagazineContentActivity;
import com.cxy.magazine.adapter.RecommAdapter;
import com.cxy.magazine.bmobBean.ArticleRecommBean;
import com.cxy.magazine.bmobBean.RecommBean;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.GDTLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommFragment extends BaseFragment{

    private Unbinder unbinder;
    //public static final int MAX_ITEMS = 50;
    public static final int AD_COUNT = 5;    // 加载广告的条数，取值范围为[1, 10]
    public int  initAdPostion = 7; // 第一条广告的位置
    public static int ITEMS_PER_AD = 8;     // 每间隔8个条目插入一条广告
    public static int pageSize=50;
    private int skip=0;
    private static final String TAG="tencentAd";
    @BindView(R.id.article_recomm_lr)
    LRecyclerView mLRecycleView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private RecommAdapter recommAdapter=null;
    private List<Object> mData=null;
    private TextView tvFoot=null;
    public RecommFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView=inflater.inflate(R.layout.fragment_recomm, container, false);
        unbinder= ButterKnife.bind(this,layoutView);
        setlRecyclerView();
        return layoutView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void setlRecyclerView(){

        mData=new ArrayList<>();
        recommAdapter=new RecommAdapter(getContext(),mData);
        mLRecyclerViewAdapter=new LRecyclerViewAdapter(recommAdapter);
        mLRecycleView.setAdapter(mLRecyclerViewAdapter);
        mLRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(getActivity())
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mLRecycleView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mLRecycleView.setHasFixedSize(true);

        //添加foot
        SampleFooter footerView = new SampleFooter(getActivity());
        tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);

        mLRecyclerViewAdapter.addFooterView(footerView);
        //下拉刷新
        mLRecycleView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        mLRecycleView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                tvFoot.setText("正在加载更多数据...");
                //获取推荐数据
                BmobQuery<ArticleRecommBean> bmobQuery=new BmobQuery<>();
                //每次加载50条数据
                bmobQuery.order("-recommCount").setSkip(skip).addWhereGreaterThan("recommCount",0).setLimit(pageSize).findObjects(new FindListener<ArticleRecommBean>() {
                    @Override
                    public void done(List<ArticleRecommBean> list, BmobException e) {
                        if (e==null){
                            if (list.size()>0){
                                mData.addAll(list);
                                mLRecycleView.refreshComplete(50);  //刷新完成
                                mLRecyclerViewAdapter.notifyDataSetChanged();
                                skip+=list.size();
                                //TODO:加载广告
                                addAds();

                            }else{
                                tvFoot.setText("没有更多数据了");
                                mLRecycleView.setNoMore(true);
                            }

                        }
                    }
                });
            }
        });

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(),MagazineContentActivity.class);
                intent.putExtra("url",((ArticleRecommBean)mData.get(position)).getArticleUrl());
                startActivity(intent);
            }
        });
        mLRecycleView.refresh();

    }
    //刷新数据
    public  void refreshData(){
        skip=0;
        //获取推荐数据
        BmobQuery<ArticleRecommBean> bmobQuery=new BmobQuery<>();
        bmobQuery.setLimit(pageSize).addWhereGreaterThan("recommCount",0).order("-recommCount").findObjects(new FindListener<ArticleRecommBean>() {
            @Override
            public void done(List<ArticleRecommBean> list, BmobException e) {
                 if (e==null){
                     mData.clear();
                     mData.addAll(list);
                     mLRecycleView.refreshComplete(50);  //刷新完成
                     mLRecyclerViewAdapter.notifyDataSetChanged();
                     skip+=list.size();
                     //重新设置广告初始位置
                     initAdPostion = 7;
                     addAds();
                 }
            }
        });
    }
   public void  addAds(){
        for (int i=initAdPostion;i<mData.size();i+=ITEMS_PER_AD){
            recommAdapter.addADViewToPosition(i,"adview");
            initAdPostion = initAdPostion + ITEMS_PER_AD ;
        }

        mLRecyclerViewAdapter.notifyDataSetChanged();
   }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源。
      /*  if (mAdViewList != null) {
            for (NativeExpressADView view : mAdViewList) {
                view.destroy();
            }
        }*/
     /* for (int i=0;i<mData.size();i++){
         if ((mData.get(i) instanceof String) && ((String)mData.get(i)).equals("adview")) {

          }
      }*/
    }


}
