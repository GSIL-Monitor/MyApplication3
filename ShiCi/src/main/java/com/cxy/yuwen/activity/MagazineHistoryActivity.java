package com.cxy.yuwen.activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


import com.cxy.yuwen.R;
import com.cxy.yuwen.fragment.MagzineHistoryFragment;
import com.cxy.yuwen.tool.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.NewsAddress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineHistoryActivity extends BasicActivity {
    private static final String DOMAIN_MAGAZINE="http://www.fx361.com";
    private String httpUrl="";
    private List<HashMap> dataList;

    @BindView(R.id.tab_magazine)
    TabLayout tabLayout;

    @BindView(R.id.vp_magazine)
    ViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_history);
        ButterKnife.bind(this);
        //设置Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(getIntent().getStringExtra("title"));
        dataList=new ArrayList<HashMap>();
        httpUrl=DOMAIN_MAGAZINE+getIntent().getStringExtra("historyUrl");
        Thread thread=new GetHtml();
        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    class GetHtml extends Thread{
        @Override
        public void run() {
            try {
                Document document=Jsoup.connect(httpUrl).get();
                Elements aList=document.getElementsByClass("selBox").first().getElementsByTag("a");
                for (Element a : aList){
                   HashMap map=new HashMap();
                   map.put("text",a.text());
                   map.put("href",DOMAIN_MAGAZINE+a.attr("href"));
                   dataList.add(map);

                }
                uiHandler.sendEmptyMessage(100);
            } catch (IOException e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);

            }
        }
    }

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==100){

               /* for (int i=0;i<dataList.size();i++){
                    tabLayout.addTab( tabLayout.newTab().setText(dataList.get(i).get("text").toString()));
                }*/

                viewPager.setAdapter(new ViewpagerAdapter(getSupportFragmentManager(),dataList));
                //绑定
                tabLayout.setupWithViewPager(viewPager);
            }else if(msg.what==101){
                Util.toastMessage(MagazineHistoryActivity.this,"出错了,该杂志内容暂无法查看，换本杂志看看吧！");
            }
        }
    };

    private class ViewpagerAdapter extends FragmentPagerAdapter {
        private List<HashMap> datalist;
        public ViewpagerAdapter(FragmentManager fm,List<HashMap> list) {
            super(fm);
            this.datalist=list;
        }

        @Override
        public Fragment getItem(int position) {
            return MagzineHistoryFragment.newInstance(datalist.get(position).get("href").toString());
        }

        @Override
        public int getCount() {
            return datalist.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return datalist.get(position).get("text").toString();
        }
    }
}
