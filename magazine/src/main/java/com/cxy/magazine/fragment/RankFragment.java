package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineContentActivity;
import com.cxy.magazine.adapter.RankAdapter;
import com.cxy.magazine.entity.RankEntity;
import com.cxy.magazine.entity.RankListEntity;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.HashMap;

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

    @BindView(R.id.rankTabSegment)
    TabLayout mTabSegment;

    @BindView(R.id.rank_lr)
    LRecyclerView lRecyclerView;
    private RankAdapter rankAdapter=null;
    LRecyclerViewAdapter lRecyclerViewAdapter=null;

    private RankListEntity rankListEntity;
    private ArrayList<HashMap<String,String>> rankEntityList=new ArrayList<>();


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
        initTab();
        return  rootView;
    }
    private void initTab(){
       /* mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(true);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.qmui_config_color_60_pure_black));
        mTabSegment.addTab(new QMUITabSegment.Tab("一天"));
        mTabSegment.addTab(new QMUITabSegment.Tab("一周"));
        mTabSegment.addTab(new QMUITabSegment.Tab("一月"));*/


       for (RankEntity rankEntity : rankList){
          mTabSegment.addTab(mTabSegment.newTab().setText(rankEntity.getTitle()));
      }
      mTabSegment.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
                int position=tab.getPosition();
                rankEntityList.clear();

                rankEntityList.addAll(rankList.get(position).getData());
                lRecyclerViewAdapter.notifyDataSetChanged();
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {

          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {

          }
      });

      //默认选中第一个
      //  mTabSegment.getTabAt(0).select();

    }

    public void setlRecyclerView(){

        rankEntityList.addAll(rankList.get(0).getData());
        rankAdapter=new RankAdapter(rankEntityList,context);
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
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        lRecyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        lRecyclerView.setHasFixedSize(true);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String href=rankEntityList.get(position).get("href");
                Intent intent=new Intent(activity, MagazineContentActivity.class);
                intent.putExtra("url",href);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }




}
