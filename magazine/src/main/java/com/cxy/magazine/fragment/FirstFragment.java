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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.NewsAddress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


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
        //设置 mTabSegment
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
        mTabSegment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.qmui_config_color_50_white));
        mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.qmui_config_color_white));
      //  mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.tabSegment_item_1_title)));
      //  mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.tabSegment_item_2_title)));
        parseHtml();
        return rootView;
    }

    private List<String> dataList=new ArrayList<>();
    //排行榜数据
    private RankListEntity rankListEntity=new RankListEntity();
    //更新榜数据
    private ArrayList<UpdateMagazine> updateData=new ArrayList<>();

    public void  parseHtml(){
        final String httpUrl="http://www.fx361.com/";
        //首先添加热门推荐Fragment
        dataList.add("热门推荐");

        //解析排行榜数据和更新数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String html= OkHttpUtil.get(httpUrl);
                    Document document = Jsoup.parse(html);

                    //排行榜DIV
                    Element rankEle=document.getElementsByClass("wzph mt20").first();
                    //获取每天、每周、每月的排行榜
                    Elements rankElements=rankEle.getElementsByClass("tabItem");
                    ArrayList<RankEntity> rankData=new ArrayList<>();
                    for (Element element : rankElements){
                      //  Map<String,Object> rankItem=new HashMap<>();
                        RankEntity rankEntity=new RankEntity();
                        ArrayList<HashMap<String,String>> rankList=new ArrayList<>();
                        //获取表头
                        String tableHead=element.getElementsByClass("tabTit").first().text();
                       // rankItem.put("title",tableHead);   //一月、一周、一天
                        rankEntity.setTitle(tableHead);
                        Elements articleEles=element.getElementsByTag("tbody").first().getElementsByTag("tr");
                        for (Element articleEle : articleEles){
                            HashMap<String,String> articleMap=new HashMap<>();
                            String title=articleEle.getElementsByTag("td").get(1).text();
                            String time=articleEle.getElementsByTag("td").get(2).text();
                            String href=articleEle.getElementsByTag("td").get(1).getElementsByTag("a").first().attr("href");
                            articleMap.put("title",title);
                            articleMap.put("time",time);
                            articleMap.put("href",href);

                            rankList.add(articleMap);
                        }
                        //rankItem.put("data",rankList);
                        rankEntity.setData(rankList);

                        rankData.add(rankEntity);

                    }
                    //设置所有榜单数据
                    rankListEntity.setRankEntityList(rankData);
                    //获取更新榜单
                    //获取右侧边栏
                    Element siderBar=document.getElementsByClass("sidebarR").first();
                    Elements lis=siderBar.getElementsByClass("list_01").first().getElementsByTag("li");
                    for (Element li : lis){
                        UpdateMagazine updateMagazine=new UpdateMagazine();
                        String tiltle=li.getElementsByTag("a").first().text();
                        String href=li.getElementsByTag("a").first().attr("href");
                        updateMagazine.setTitle(tiltle);
                        updateMagazine.setHref(href);

                        updateData.add(updateMagazine);
                    }

                    dataList.add("文章排行");
                    dataList.add("最近更新");
                    uiHandler.sendEmptyMessage(100);
                } catch (IOException e) {
                    e.printStackTrace();
                    //Todo:解析排行榜和更新失败，只显示热门推荐
                    uiHandler.sendEmptyMessage(101);
                }


            }
        }).start();
    }


    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==100){
                ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getActivity().getSupportFragmentManager(),dataList);
                mContentViewPager.setAdapter(viewpagerAdapter);
                mTabSegment.setupWithViewPager(mContentViewPager);
            }
            if(msg.what==101){
                ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getActivity().getSupportFragmentManager(),dataList);
                mContentViewPager.setAdapter(viewpagerAdapter);
                mTabSegment.setupWithViewPager(mContentViewPager);

            }
        }
    };
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
           // return MagzineHistoryFragment.newInstance(maplist.get(position).get("href").toString());
            Fragment fragment=null;
            if (position==0){
                fragment= new RecommFragment();
            }
            if (position==1){
                fragment= RankFragment.newInstance(rankListEntity);
            }
            if (position==2){
                fragment=UpdateFragment.newInstance(updateData);
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
    }
}
