package com.cxy.magazine.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.entity.RankEntity;
import com.cxy.magazine.entity.RankListEntity;
import com.cxy.magazine.entity.UpdateMagazine;
import com.cxy.magazine.fragment.MagzineHistoryFragment;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;

import com.cxy.magazine.R;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.internal.Util;


public class FirstFragment extends BaseFragment {


   /* @BindView(R.id.tab_first)
    TabLayout mTablayout;
    @BindView(R.id.vp_first)
    ViewPager mContentViewPager;*/

    @BindView(R.id.tabSegment)
    QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager)
    ViewPager mContentViewPager;

    public FirstFragment() {
        // Required empty public constructor
    }

    private Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_first, null);
        unbinder=ButterKnife.bind(this, rootView);

        setmTabSegment();
        return rootView;
    }

    private void setmTabSegment(){
        //设置 mTabSegment
        //  mTabSegment.setHasIndicator(true);
        // mTabSegment.setIndicatorPosition(false);
        // mTabSegment.setIndicatorWidthAdjustContent(false);
        mTabSegment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.qmui_config_color_50_white));
        mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.qmui_config_color_white));
        List<String> dataList=new ArrayList<>();
        dataList.add("热门推荐");
        dataList.add("文章排行");
        dataList.add("最近更新");
        ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getActivity().getSupportFragmentManager(),dataList);
        mContentViewPager.setAdapter(viewpagerAdapter);
        mTabSegment.setupWithViewPager(mContentViewPager);
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class ViewpagerAdapter extends FragmentPagerAdapter {
        private List<String> datalist;
        public ViewpagerAdapter(FragmentManager fm, List<String> list) {
            super(fm);
            this.datalist=list;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            if (position==0){
                fragment= new RecommFragment();
            }
            if (position==1){
                fragment= RankFragment.newInstance();
            }
            if (position==2){
                fragment=UpdateFragment.newInstance();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return datalist.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return datalist.get(position);
        }

        //重写destroyItem方法，禁止重新加载fragment
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        //   super.destroyItem(container, position, object);
        }
    }




}
