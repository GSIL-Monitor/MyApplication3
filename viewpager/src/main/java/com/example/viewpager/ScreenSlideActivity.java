package com.example.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlideActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<Fragment> fragmentList=new ArrayList<Fragment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        Fragment f1=new ScreenSlidePageFragment();
        Fragment f2=new Page2Fragment();
        Fragment f3=new Page3Fragment();
        fragmentList.add(f1);
        fragmentList.add(f2);
        fragmentList.add(f3);

        mPager=(ViewPager)findViewById(R.id.pager);
        mPagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{
        List<Fragment> listFragment=new ArrayList<Fragment>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            listFragment=fragmentList;

        }

        @Override

        public Fragment getItem(int position) {

            return listFragment.get(position);
        }

        @Override
        public int getCount() {

            return listFragment.size();
        }
    }
}
