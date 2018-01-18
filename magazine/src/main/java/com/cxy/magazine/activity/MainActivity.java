package com.cxy.magazine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.cxy.magazine.R;
import com.cxy.magazine.fragment.ClassFragment;
import com.cxy.magazine.fragment.MyFragment;
import com.cxy.magazine.fragment.ShelfFragment;
import com.cxy.magazine.util.PermissionHelper;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import cn.bmob.v3.Bmob;

public class MainActivity extends BasicActivity {

    private String tabs[]={"首页","书架","我的"};
    private static final String BmobApplicationId ="be69c91d46af21288d5b855ee9fe158e";
    protected PermissionHelper mPermissionHelper;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragmentSupport(R.id.content,tabs[0]);
                    return true;
                case R.id.navigation_shelf:
                    switchFragmentSupport(R.id.content,tabs[1]);
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchFragmentSupport(R.id.content,tabs[0]);

        //初始化Bmob
        Bmob.initialize(this,BmobApplicationId);
        //小米更新
        XiaomiUpdateAgent.update(this);//这种情况下, 若本地版本是debug版本则使用沙盒环境，否则使用线上环境
        //检查权限
        checkPermmion(this);


    }

    public void checkPermmion(Activity activity){
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(activity);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(LOG_TAG, "All of requested permissions has been granted, so run app logic.");

            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(LOG_TAG, "The api level of system is lower than 23, so run app logic directly.");

        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(LOG_TAG, "All of requested permissions has been granted, so run app logic directly.");

            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(LOG_TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
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
            if (tag.equals(tabs[1])) destFragment= new ShelfFragment();
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
