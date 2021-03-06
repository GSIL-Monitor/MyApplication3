package com.cxy.yuwen.activity;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;


import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Utils;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.cxy.yuwen.fragment.CompositionFragment;
import com.cxy.yuwen.fragment.MainFragment;
import com.cxy.yuwen.fragment.MyFragment;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import cn.bmob.v3.Bmob;

public class MainActivity extends BasicActivity {

    public static List<String> logList = new CopyOnWriteArrayList<String>();
    private String tabs[]={"查询","作文大全","我的",};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        XiaomiUpdateAgent.update(this);//这种情况下, 若本地版本是debug版本则使用沙盒环境，否则使用线上环境

        //初始化Bmob
        Bmob.initialize(this, Utils.BmobApplicationId,"bmob");

        switchFragmentSupport(R.id.content,tabs[0]);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    @Override
    protected void onResume() {
        /*String tag=getIntent().getStringExtra("tag");
        if (!TextUtils.isEmpty(tag)){
            switchFragmentSupport(R.id.content,tag);
        }
*/
        super.onResume();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected( MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    switchFragmentSupport(R.id.content,tabs[0]);
                    return true;
               /* case R.id.navigation_composition:
                    switchFragmentSupport(R.id.content,tabs[1]);
                    return true;*/

                case R.id.navigation_mine:
                    switchFragmentSupport(R.id.content,tabs[2]);
                    return true;

            }
            return false;
        }

    };

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
            if (tag.equals(tabs[0])) destFragment=new MainFragment();
            if (tag.equals(tabs[1])) destFragment=new CompositionFragment();
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
