package com.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yuwen.BmobBean.User;
import com.yuwen.myapplication.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etUserName,etPassword;
    TextView tvRegister;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUserName=(EditText)findViewById(R.id.et_account) ;
        etPassword=(EditText)findViewById(R.id.et_pwd) ;
        btnLogin=(Button) findViewById(R.id.btn_login);
        tvRegister=(TextView)findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tv_register) {  //注册
            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
         if (view.getId()==R.id.btn_login){  //登录
             String userName=etUserName.getText().toString();
             String password=etPassword.getText().toString();

             User user=new User();
             user.setUsername(userName);
             user.setPassword(password);

             user.login(new SaveListener<BmobUser>() {

                 @Override
                 public void done(BmobUser bmobUser, BmobException e) {
                     if(e==null){  //login success
                         Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                         startActivity(intent);
                     }else{
                         Log.i(AdApplication.TAG,e.toString());
                     }
                 }
             });



         }
    }
}
