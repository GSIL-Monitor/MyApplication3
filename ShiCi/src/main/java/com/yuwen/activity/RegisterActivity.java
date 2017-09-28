package com.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yuwen.bmobBean.User;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etName,etPassword,etPassword2,etPlace,etPerson;
    private Button register;
    String userName,password,password2,place,person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MyApplication.getInstance().addActivity(this);

        etName=(EditText)findViewById(R.id.et_userName);
        etPassword=(EditText)findViewById(R.id.et_password);
        etPassword2=(EditText)findViewById(R.id.et_password2);
        etPlace=(EditText)findViewById(R.id.et_place);
        etPerson = (EditText) findViewById(R.id.et_person);

        register=(Button)findViewById(R.id.btn_register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //密码正则表达式 6-20 位，字母、数字、字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]){6,20}$";
        if (view.getId()==R.id.btn_register) {  //注册
            userName=etName.getText().toString();
            password=etPassword.getText().toString();
            password2=etPassword2.getText().toString();
            person=etPerson.getText().toString();
            place=etPlace.getText().toString();
            if (Utils.isEmpty(userName)||Utils.isEmpty(password)||Utils.isEmpty(password2)||Utils.isEmpty(person)||Utils.isEmpty(place)) {
                  //内容不能为空
                new AlertDialog.Builder(RegisterActivity.this).setMessage("内容不能为空！").setPositiveButton("确定", null).create().show();
            } else if (!password2.equals(password)) {
                new AlertDialog.Builder(RegisterActivity.this).setMessage("两次输入的密码必须一致！").setPositiveButton("确定", null).create().show();
            }else if (!password.matches(regStr)){

                Util.showResultDialog(RegisterActivity.this,"密码至少为6位，可以包含数字、字母和字符！","提示");



            }
            else{ //注册
                Util.showProgressDialog(RegisterActivity.this,null,"正在注册中，请稍候！");
                User user = new User();
                user.setUsername(userName);
                user.setPassword(Utils.encryptBySHA(password));
                user.setPerson(person);
                user.setPlace(place);

                //注意：不能用save方法进行注册
                user.signUp(new SaveListener<User>() { //每当你应用的用户注册成功或是第一次登录成功，都会在本地磁盘中有一个缓存的用户对象，这样，你可以通过获取这个缓存的用户对象来进行登录
                    @Override
                    public void done(User s, BmobException e) {
                        Util.dismissDialog();
                        if(e==null){
                            new AlertDialog.Builder(RegisterActivity.this).setMessage("注册成功！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                            }).create().show();
                        }else{
                            Log.i(MyApplication.TAG,e.toString());
                            new AlertDialog.Builder(RegisterActivity.this).setMessage("用户名已存在，请重新输入！").setPositiveButton("确定", null).create().show();
                        }
                    }
                });

            }

        }
    }
}
