package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineContentActivity;
import com.cxy.magazine.adapter.RankAdapter;
import com.cxy.magazine.entity.RankEntity;
import com.cxy.magazine.entity.RankListEntity;
import com.cxy.magazine.util.Constants;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "rankdata";

    private static final  String AD_LOG="rankTencentAd";
    @BindView(R.id.rank_lr)
    LRecyclerView lRecyclerView;
    private RankAdapter rankAdapter=null;
    LRecyclerViewAdapter lRecyclerViewAdapter=null;

    private RankListEntity rankListEntity;
    private ArrayList<HashMap<String,String>> rankEntityList=new ArrayList<>();
    //保存广告的 map
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
    private List<NativeExpressADView> mAdViewList;
    public RankFragment() {
        // Required empty public constructor
    }



    public static RankFragment newInstance(RankListEntity rankListEntity) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM1,rankListEntity);
        fragment.setArguments(args);
        return fragment;
    }
     ArrayList<RankEntity> rankList=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rankListEntity =(RankListEntity)getArguments().getSerializable(ARG_PARAM1);
            rankList=rankListEntity.getRankEntityList();
            System.out.println("123");

        }
    }

    private Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_rank, container, false);
        unbinder=ButterKnife.bind(this,rootView);
        setlRecyclerView();
      //  initTab();
        return  rootView;
    }


    public void setlRecyclerView(){

       // rankEntityList.addAll(rankList.get(0).getData());
        for(RankEntity entity : rankList){
            HashMap<String,String> titleMap=new HashMap<>();
            titleMap.put("type","title");
            titleMap.put("title",entity.getTitle());
            rankEntityList.add(titleMap);

            rankEntityList.addAll(entity.getData());
        }

        //加载广告


        rankAdapter=new RankAdapter(rankEntityList,context,mAdViewPositionMap);
        lRecyclerViewAdapter=new LRecyclerViewAdapter(rankAdapter);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //  mLRecycleView.setPullRefreshEnabled(true);
        //禁用自动加载更多功能
        lRecyclerView.setLoadMoreEnabled(false);
        //禁止下拉刷新
        lRecyclerView.setPullRefreshEnabled(false);
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(context)
                .setHeight(R.dimen.thin_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        lRecyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
       // lRecyclerView.setHasFixedSize(true);

        //加载广告
        initAd();
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HashMap<String,String> itemMap=rankEntityList.get(position);
                if (itemMap.get("type").equals("item")){
                    String href=itemMap.get("href");
                    Intent intent=new Intent(activity, MagazineContentActivity.class);
                    intent.putExtra("url",href);
                    startActivity(intent);
                }

            }
        });


    }

    private  static  final int AD_COUNT=3;
    public void initAd(){
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        AdListener adListener=new AdListener();
        NativeExpressAD  mADManager = new NativeExpressAD(context, adSize, Constants.APPID, Constants.RANK_AD_ID, adListener);
        mADManager.loadAD(AD_COUNT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源。
        if (mAdViewList != null) {
            for (NativeExpressADView view : mAdViewList) {
                view.destroy();
            }
        }
    }




    //广告监听器
    class  AdListener implements NativeExpressAD.NativeExpressADListener{
        private int  initAdPostion=11;
        private static final int ITEMS_PER_AD=12;

        @Override
        public void onNoAD(AdError adError) {
            Log.i( AD_LOG, String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
        }

        @Override
        public void onADLoaded(List<NativeExpressADView> adList) {
            Log.i(AD_LOG, "onADLoaded: " + adList.size());
            mAdViewList = adList;
            for (int i = 0; i < mAdViewList.size(); i++) {

                if (initAdPostion < rankEntityList.size()+i) {
                    NativeExpressADView view = mAdViewList.get(i);
                    // GDTLogger.i("ad load[" + i + "]: " + getAdInfo(view));
                    mAdViewPositionMap.put(view, initAdPostion); // 把每个广告在列表中位置记录下来
                    rankAdapter.addADViewToPosition(initAdPostion, mAdViewList.get(i));
                    initAdPostion = initAdPostion + ITEMS_PER_AD ;
                }
            }
            lRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRenderFail(NativeExpressADView nativeExpressADView) {
            Log.i(AD_LOG, "onRenderFail: " + nativeExpressADView.toString());
        }

        @Override
        public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
            Log.i(AD_LOG, "onRenderSuccess: " + nativeExpressADView.toString() );
        }

        @Override
        public void onADExposure(NativeExpressADView nativeExpressADView) {
            Log.i(AD_LOG, "onADExposure: " + nativeExpressADView.toString());
        }

        @Override
        public void onADClicked(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADClosed(NativeExpressADView adView) {
            if (rankAdapter != null) {
                int removedPosition = mAdViewPositionMap.get(adView);
                rankAdapter.removeADView(removedPosition, adView);
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


}
