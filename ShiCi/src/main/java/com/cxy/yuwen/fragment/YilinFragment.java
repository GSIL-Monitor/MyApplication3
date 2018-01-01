package com.cxy.yuwen.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.ParcelableMap;
import com.cxy.yuwen.tool.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YilinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YilinFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static  final String  TAG="yilin";
    private static  final int LOAD_FINSHED=100;
    private ACache mCache;

    private String[] mTitles={"意林","意林作文素材","意林少年版","意林原创版","意林12+"};

    private List<ArrayList> tagList;

    public static YilinFragment newInstance( ) {
        YilinFragment fragment = new YilinFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View layoutView=inflater.inflate(R.layout.fragment_yilin, container, false);

        initView(layoutView);
        return layoutView;
    }

    private  void initView(View layoutView){
        tabLayout=(TabLayout) layoutView.findViewById(R.id.yilin_tab);
        tagList=new ArrayList<ArrayList>();

        tabLayout.addTab(tabLayout.newTab().setText(mTitles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(mTitles[1]));
        tabLayout.addTab(tabLayout.newTab().setText(mTitles[2]));
        tabLayout.addTab(tabLayout.newTab().setText(mTitles[3]));
        tabLayout.addTab(tabLayout.newTab().setText(mTitles[4]));




        viewPager=(ViewPager)layoutView.findViewById(R.id.yilin_viewpager);


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCache = ACache.get(this.getActivity());

        Thread getHtml=new Thread(new GetHtml());
        getHtml.start();


    }

    /**
     * 爬虫获取Html意林内容
     */
    class GetHtml implements  Runnable{

        @Override
        public void run() {


            try {
                 String html=mCache.getAsString("Indexhtml");
                 if (html==null){
                     Document docHtml = Jsoup.connect("http://www.92yilin.com/").get();
                     mCache.put("Indexhtml",docHtml.toString(),20* ACache.TIME_DAY);
                     html=docHtml.toString();
                   }

                Document doc = Jsoup.parse(html);

                Elements contentTag = doc.getElementsByClass("tagContent");
                for (int i=0;i<contentTag.size();i++){
                    ArrayList list=new ArrayList<ParcelableMap>();

                    Elements tds =contentTag.get(i).getElementsByTag("td");  //获取到所有td元素
                    for (int j=0;j<tds.size();j++){  //循环td
                          Elements strongs=tds.get(j).getElementsByTag("strong");
                          if (strongs.size()>0){   //是Strong
                              HashMap<String,String> strongMap=new HashMap<String,String>();
                              strongMap.put("type","1");   //年份
                              strongMap.put("text",strongs.get(0).text());
                              ParcelableMap parcelableMap1=new ParcelableMap(strongMap);
                              list.add(parcelableMap1);
                          }else{   //不是strong元素
                              Elements links = tds.get(j).getElementsByTag("a");
                              HashMap<String,String> linkMap=new HashMap<String,String>();
                              linkMap.put("type","2");  //年份第几期
                              linkMap.put("text",links.get(0).text());
                              linkMap.put("href",links.get(0).attr("href"));

                              ParcelableMap parcelableMap2=new ParcelableMap(linkMap);
                              list.add(parcelableMap2);

                          }


                    }
                    Log.i(TAG,list.toString());

                    tagList.add(list);

                 }
                handler.sendEmptyMessage(LOAD_FINSHED);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
                Util.toastMessage(getActivity(),e.getMessage());
            }

        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_FINSHED:
                     viewPager.setAdapter(new ViewpagerAdapter(getChildFragmentManager(),mTitles));
                    //绑定
                     tabLayout.setupWithViewPager(viewPager);

                    break;
            }
        }
    };

    private class ViewpagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;

        public ViewpagerAdapter(FragmentManager fm, String[] mTitles) {
            super(fm);
            this.mTitles = mTitles;
        }

        @Override
        public Fragment getItem(int position) {
                 Log.i(TAG,"list长度："+tagList.size());
            switch (position){
                case 0:
                    if (tagList.size()>0){
                        return  new YilinItem1Fragment().newInstance(tagList.get(0));
                    }

                case 1:
                    if (tagList.size()>1){
                        return  new YilinItem1Fragment().newInstance(tagList.get(1));
                    }
                case 2:
                    if (tagList.size()>2){
                        return  new YilinItem1Fragment().newInstance(tagList.get(2));
                    }
                case 3:
                    if (tagList.size()>3){
                        return  new YilinItem1Fragment().newInstance(tagList.get(3));
                    }
                case 4:
                    if (tagList.size()>4){
                        return  new YilinItem1Fragment().newInstance(tagList.get(4));
                    }

                default:
                   return  new YilinItem1Fragment().newInstance(tagList.get(0));
            }
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

}
