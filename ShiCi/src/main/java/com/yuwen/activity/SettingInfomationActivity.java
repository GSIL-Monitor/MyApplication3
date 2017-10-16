package com.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;

import cn.bmob.v3.BmobUser;


public class SettingInfomationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_infomation);
        MyApplication.getInstance().addActivity(this);
        btnLogout=(Button)findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:    //logout
                BmobUser.logOut();   //清除缓存用户对象

                /*Intent intent=new Intent(SettingInfomationActivity.this,MainActivity.class);
                intent.putExtra("param","我的");
                startActivity(intent);*/
                finish();

                break;



        }
    }
}
