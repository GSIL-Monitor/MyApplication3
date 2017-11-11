package com.cxy.yuwen.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.NetWorkUtils;
import com.cxy.yuwen.tool.Util;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.fragment.CompositionFragment;
import com.cxy.yuwen.fragment.MainFragment;
import com.cxy.yuwen.fragment.MyFragment;
import com.cxy.yuwen.tool.CommonUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.bmob.v3.Bmob;

public class MainActivity extends BasicActivity {

    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private String tabs[]={"查询","作文大全","我的"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermmion(this);
        MyApplication.getInstance().addActivity(this);
     //   Log.i("packageName",this.getPackageName());

        CommonUtil.checkNetworkState(this);

        XiaomiUpdateAgent.update(this);//这种情况下, 若本地版本是debug版本则使用沙盒环境，否则使用线上环境

        //初始化Bmob
        Bmob.initialize(this, CommonUtil.BmobApplicationId,"bmob");

        /*Intent intent=this.getIntent();
        String param=intent.getStringExtra("param");
        if (!Utils.isEmpty(param)){
            if (param.equals(tabs[0])){
                switchFragmentSupport(R.id.content,tabs[0]);
            }else if (param.equals(tabs[1])){
                switchFragmentSupport(R.id.content,tabs[1]);
            }else if (param.equals(tabs[2])){
                switchFragmentSupport(R.id.content,tabs[2]);
            }

        }*/
        switchFragmentSupport(R.id.content,tabs[0]);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //navigation.setse
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected( MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   // mTextMessage.setText(R.string.title_home);
                    switchFragmentSupport(R.id.content,tabs[0]);
                    return true;
                case R.id.navigation_dashboard:
                  //  mTextMessage.setText(R.string.title_dashboard);
                    switchFragmentSupport(R.id.content,tabs[1]);
                    return true;
                case R.id.navigation_notifications:
                  //  mTextMessage.setText(R.string.title_notifications);
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
        FragmentManager manager=getFragmentManager();
        //根据tab标签名查找是否已存在对应的Fragment对象
        Fragment destFragment=manager.findFragmentByTag(tag);

        if (destFragment==null){
            if (tag.equals(tabs[0])) destFragment=new MainFragment();
            if (tag.equals(tabs[1])) destFragment=new CompositionFragment();
            if (tag.equals(tabs[2])) destFragment=new MyFragment();
           // if (tag.equals(tabs[3])) destFragment=new Fragment4();
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
