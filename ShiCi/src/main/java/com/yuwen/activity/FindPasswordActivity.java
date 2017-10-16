package com.yuwen.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.yuwen.bmobBean.User;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etName,etPlace,etStar;
    Button btnFindPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        etName=(EditText) findViewById(R.id.et_userName);
        etPlace=(EditText)findViewById(R.id.et_place);
        etStar=(EditText)findViewById(R.id.et_star);
        btnFindPassword=(Button)findViewById(R.id.btn_resetPassword);
        btnFindPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.btn_resetPassword){
            String name=etName.getText().toString();
            String place=etPlace.getText().toString();
            String star=etStar.getText().toString();

            if (Utils.isEmpty(name)||Utils.isEmpty(place)||Utils.isEmpty(star)){
                Util.showResultDialog(FindPasswordActivity.this,"内容不能为空",null);
            }else{  //重置密码
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("username",name);
                query.addWhereEqualTo("place",place);
                query.addWhereEqualTo("person",star);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                           if (e==null){
                               if (list.size()==0){
                                   Util.showResultDialog(FindPasswordActivity.this,"输入信息有误，请重新输入！",null);
                               }else if (list.size()==1){ //输入信息正确，更新密码
                                   User user=new User();
                                   user.setPassword(Utils.encryptBySHA("123456"));
                                 //  user.set
                                   user.update(list.get(0).getObjectId(),new UpdateListener() {
                                       @Override
                                       public void done(BmobException e) {
                                           if (e==null){
                                                    Util.showResultDialog(FindPasswordActivity.this,"你的密码已被重置为12346！",null);
                                           }
                                           else{
                                               Util.toastMessage(FindPasswordActivity.this,"出错了，请稍候再试"+e.getErrorCode()+":"+e.getMessage());
                                           }
                                       }
                                   });
                               }

                           }else{
                               Util.toastMessage(FindPasswordActivity.this,"出错了，请稍候再试"+e.getErrorCode()+":"+e.getMessage());
                           }
                    }
                });


            }

        }
    }
}
