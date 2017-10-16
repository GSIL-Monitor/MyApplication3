package com.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yuwen.MyApplication;
import com.yuwen.bmobBean.User;
import com.yuwen.myapplication.R;
import com.yuwen.tool.CommonUtil;

import cn.bmob.v3.BmobUser;


public class SettingInfomationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnLogout,tvBindPhone;
    private User user=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_infomation);
        MyApplication.getInstance().addActivity(this);

        btnLogout=(TextView)findViewById(R.id.btn_logout);
        tvBindPhone=(TextView)findViewById(R.id.bindPhone);
        btnLogout.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);

        setInfo();

    }

    public void setInfo(){
        user=BmobUser.getCurrentUser(User.class);
        if (user!=null){

            if (user.getMobilePhoneNumberVerified()!=null&&user.getMobilePhoneNumberVerified()==true){
                String phoneNumber=user.getMobilePhoneNumber();
                tvBindPhone.setText(phoneNumber);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setInfo();
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

            case R.id.bindPhone:
                Intent intent=new Intent(SettingInfomationActivity.this,BindPhoneActivity.class);
                startActivity(intent);

                break;



        }
    }
}
