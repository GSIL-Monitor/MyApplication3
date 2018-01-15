package com.cxy.magazine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.cxy.magazine.R;
import com.cxy.magazine.fragment.ClassFragment;
import com.cxy.magazine.fragment.MyFragment;

import cn.bmob.v3.Bmob;

public class MainActivity extends BasicActivity {

    private String tabs[]={"首页","书架","我的"};
    private static final String BmobApplicationId ="be69c91d46af21288d5b855ee9fe158e";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragmentSupport(R.id.content,tabs[0]);
                    return true;
                case R.id.navigation_shelf:

                    return true;
                case R.id.navigation_mine:
                    switchFragmentSupport(R.id.content,tabs[2]);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化Bmob
        Bmob.initialize(this,BmobApplicationId);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchFragmentSupport(R.id.content,tabs[0]);
    }

    /**
     *
     * @param containerId 待切换界面的Id
     * @param tag     目标Fragment的标签名称
     */
    public void switchFragmentSupport(int containerId,String tag){
        //获取FragmentManager管理器
        FragmentManager manager=getSupportFragmentManager();
        //根据tab标签名查找是否已存在对应的Fragment对象
        Fragment destFragment=manager.findFragmentByTag(tag);

        if (destFragment==null){
            if (tag.equals(tabs[0])) destFragment= new ClassFragment();
            if (tag.equals(tabs[2])) destFragment=new MyFragment();

        }
        FragmentTransaction ft=manager.beginTransaction();

        ft.replace(containerId,destFragment,tag);

        //设置Fragment切换效果,可根据需要使用
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        //将状态保存到回退栈，这样按下Back键将返回到前一个Fragment界面
        //ft.addToBackStack(null);
        ft.commit();
    }


}
