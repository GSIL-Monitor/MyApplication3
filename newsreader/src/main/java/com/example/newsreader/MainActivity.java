package com.example.newsreader;



import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup navGroup;
    private String tabs[]={"首页","新闻","组图","更多"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
