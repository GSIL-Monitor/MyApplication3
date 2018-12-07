package com.cxy.magazine.activity;

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

import com.cxy.magazine.fragment.MagzineHistoryFragment;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;

import com.cxy.magazine.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private UiHandler uiHandler=null;

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
        uiHandler=new UiHandler(this);
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
                String html= OkHttpUtil.get(httpUrl);
                Document document = Jsoup.parse(html);
                Elements selBoxes=document.getElementsByClass("selBox");
                if (selBoxes.size()>0){
                    Elements aList=selBoxes.first().getElementsByTag("a");
                    for (Element a : aList){
                        HashMap map=new HashMap();
                        map.put("text",a.text());
                        map.put("href",DOMAIN_MAGAZINE+a.attr("href"));
                        dataList.add(map);

                    }
                }

                uiHandler.sendEmptyMessage(100);
            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);

            }
        }
    }

    private static class UiHandler extends Handler{
        private final WeakReference<MagazineHistoryActivity> weakReference;
        private UiHandler(MagazineHistoryActivity historyActivity){
            weakReference=new WeakReference<>(historyActivity);
        }
        @Override
        public void handleMessage(Message msg) {

            final MagazineHistoryActivity magazineHistoryActivity=weakReference.get();
            if (magazineHistoryActivity!=null){
                if (msg.what==100){
                    magazineHistoryActivity.viewPager.setAdapter(new ViewpagerAdapter(magazineHistoryActivity.getSupportFragmentManager(),magazineHistoryActivity.dataList));
                    //绑定
                    magazineHistoryActivity.tabLayout.setupWithViewPager(magazineHistoryActivity.viewPager);
                }else if(msg.what==101){
                    Utils.toastMessage(magazineHistoryActivity,"出错了,该杂志内容暂无法查看，换本杂志看看吧！");
                }
            }

        }
    };

    private static class ViewpagerAdapter extends FragmentPagerAdapter {
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
