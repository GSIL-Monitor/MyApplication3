package com.cxy.childstory;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cxy.childstory.base.BaseActivity;
import com.cxy.childstory.fragment.home.StoryMineFragment;
import com.cxy.childstory.fragment.home.StoryTypeFragment;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;

    private static ArrayList<Fragment> mPages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTabs();
        initPagers();
    }



    private void initTabs() {
        int normalColor = QMUIResHelper.getAttrColor(this, R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(this, R.attr.qmui_config_color_blue);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);

        QMUITabSegment.Tab component = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(this, R.drawable.ic_tabbar_type),
                ContextCompat.getDrawable(this, R.drawable.ic_tabbar_type_selected),
                "分类", false
        );

        QMUITabSegment.Tab util = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(this, R.drawable.ic_tabbar_mine),
                ContextCompat.getDrawable(this, R.drawable.ic_tabbar_mine_selected),
                "我的", false
        );
      /*  QMUITabSegment.Tab lab = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_lab),
                ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_lab_selected),
                "Lab", false
        );*/
        mTabSegment.addTab(component)
                .addTab(util);

        //创建通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "audio";
            String channelName = "音频播放";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void initPagers() {
        mPages=new ArrayList<>();
        StoryTypeFragment typeFragment=new StoryTypeFragment();
        mPages.add(typeFragment);

        StoryMineFragment mineFragment=new StoryMineFragment();
        mPages.add(mineFragment);
        FragmentAdapter mPagerAdapter=new FragmentAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager, false);
    }

    private static class FragmentAdapter extends FragmentPagerAdapter{

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPages.get(position);
        }

        @Override
        public int getCount() {
            return mPages.size();
        }
    }
}
