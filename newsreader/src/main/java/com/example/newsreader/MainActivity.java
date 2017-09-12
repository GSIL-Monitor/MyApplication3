package com.example.newsreader;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RadioGroup navGroup;
    private String tabs[]={"国内","国际","军事","财经"};


    private String title;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title=(String) getTitle();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.setDrawerListener(toggle);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navGroup=(RadioGroup) findViewById(R.id.navgroup);

        navGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:
                        switchFragmentSupport(R.id.content,tabs[0]);
                        break;
                    case R.id.radioButton2:
                        switchFragmentSupport(R.id.content,tabs[1]);
                        break;
                    case R.id.radioButton3:
                        switchFragmentSupport(R.id.content,tabs[2]);
                        break;
                    case R.id.radioButton4:
                        switchFragmentSupport(R.id.content,tabs[3]);
                        break;
                }
            }
        });

        //默认选中最左边的RadioButton
        RadioButton btn=(RadioButton) navGroup.getChildAt(0);
        btn.toggle();
    }


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
            if (tag.equals(tabs[0])) destFragment=new Fragment1();
            if (tag.equals(tabs[1])) destFragment=new Fragment2();
            if (tag.equals(tabs[2])) destFragment=new Fragment3();
            if (tag.equals(tabs[3])) destFragment=new Fragment4();
        }
        FragmentTransaction ft=manager.beginTransaction();

        ft.replace(containerId,destFragment,tag);

        //设置Fragment切换效果,可根据需要使用
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        //将状态保存到回退栈，这样按下Back键将返回到前一个Fragment界面
        //ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.setting) {
            // Handle the setting action
            // Fragment contentFragment=new Fragment1();
            //  FragmentManager fm=getSupportFragmentManager();
            //   fm.beginTransaction().replace(R.layout.content_main,contentFragment).commit();
            Toast.makeText(MainActivity.this, "你点击了设置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.black_mode) {
            Toast.makeText(MainActivity.this, "你点击了夜间模式", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.collect) {
            Toast.makeText(MainActivity.this, "你点击了收藏", Toast.LENGTH_SHORT).show();
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
