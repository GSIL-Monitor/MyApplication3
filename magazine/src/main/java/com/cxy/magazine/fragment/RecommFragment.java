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
public class RecommFragment extends BaseFragment implements NativeExpressAD.NativeExpressADListener{

    private Unbinder unbinder;
    //public static final int MAX_ITEMS = 50;
    public static final int AD_COUNT = 5;    // 加载广告的条数，取值范围为[1, 10]
    public int  initAdPostion = 7; // 第一条广告的位置
    public static int ITEMS_PER_AD = 8;     // 每间隔10个条目插入一条广告
    private static final String TAG="tencentAd";
    @BindView(R.id.article_recomm_lr)
    LRecyclerView mLRecycleView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private RecommAdapter recommAdapter=null;
    private List<ArticleRecommBean> recommBeanList=null;
    private TextView tvFoot=null;
    private int skip=0;
    private NativeExpressAD mADManager;
    private List<NativeExpressADView> mAdViewList;
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
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

        recommBeanList=new ArrayList<>();
        recommAdapter=new RecommAdapter(getContext(),recommBeanList,mAdViewPositionMap);
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
                bmobQuery.addWhereGreaterThan("recommCount",0).order("-updatedAt,-recommCount").setSkip(skip).setLimit(50).findObjects(new FindListener<ArticleRecommBean>() {
                    @Override
                    public void done(List<ArticleRecommBean> list, BmobException e) {
                        if (e==null){
                            if (list.size()>0){
                                recommBeanList.addAll(list);
                                mLRecycleView.refreshComplete(list.size());  //刷新完成
                                mLRecyclerViewAdapter.notifyDataSetChanged();
                                skip+=list.size();
                                //TODO:加载广告
                                initNativeExpressAD();

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
                intent.putExtra("url",recommBeanList.get(position).getArticleUrl());
                startActivity(intent);
            }
        });
        mLRecycleView.refresh();

    }
    //刷新数据
    public  void refreshData(){
        //获取推荐数据
        BmobQuery<ArticleRecommBean> bmobQuery=new BmobQuery<>();
        bmobQuery.addWhereGreaterThan("recommCount",0).order("-updatedAt,-recommCount").setLimit(50).findObjects(new FindListener<ArticleRecommBean>() {
            @Override
            public void done(List<ArticleRecommBean> list, BmobException e) {
                 if (e==null){
                     recommBeanList.clear();
                     recommBeanList.addAll(list);
                     mLRecycleView.refreshComplete(list.size());  //刷新完成
                     mLRecyclerViewAdapter.notifyDataSetChanged();
                     skip+=list.size();
                     //重新设置广告初始位置
                     initAdPostion = 7;
                     initNativeExpressAD();
                 }
            }
        });
    }

    private void initNativeExpressAD() {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        mADManager = new NativeExpressAD(getContext(), adSize, Constants.APPID, Constants.RECOMM_AD_ID, this);
        mADManager.loadAD(AD_COUNT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源。
        if (mAdViewList != null) {
            for (NativeExpressADView view : mAdViewList) {
                view.destroy();
            }
        }
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i( TAG, String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        Log.i(TAG, "onADLoaded: " + adList.size());
        mAdViewList = adList;
        for (int i = 0; i < mAdViewList.size(); i++) {

            if (initAdPostion < recommBeanList.size()) {
                NativeExpressADView view = mAdViewList.get(i);
               // GDTLogger.i("ad load[" + i + "]: " + getAdInfo(view));
                mAdViewPositionMap.put(view, initAdPostion); // 把每个广告在列表中位置记录下来
                recommAdapter.addADViewToPosition(initAdPostion, mAdViewList.get(i));
                initAdPostion = initAdPostion + ITEMS_PER_AD ;
            }
        }
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderFail: " + nativeExpressADView.toString());
    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderSuccess: " + nativeExpressADView.toString() );
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
        Log.i(TAG, "onADExposure: " + adView.toString());
    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        if (recommAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            recommAdapter.removeADView(removedPosition, adView);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }
}
