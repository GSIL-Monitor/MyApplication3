package com.cxy.yuwen.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Utils;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends BasicActivity implements View.OnClickListener{

    private EditText etName,etPassword,etPassword2,etPhoneNumber,etVertifyCode,etPlace,etPerson;
    private Button register;
    String userName,password,password2,place,person;
    TextView tvSendVertifyCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
     //   MyApplication.getInstance().addActivity(this);

        ActionBar bar= getSupportActionBar();
        bar.setTitle("注册账号");

        etName=(EditText)findViewById(R.id.et_userName);
        etPassword=(EditText)findViewById(R.id.et_password);
        etPassword2=(EditText)findViewById(R.id.et_password2);
        etPhoneNumber=(EditText)findViewById(R.id.et_phoneNumber);
        etVertifyCode=(EditText)findViewById(R.id.et_verifyCode);
        tvSendVertifyCode=(TextView)findViewById(R.id.tv_sendVertifyCode);
     //   etPlace=(EditText)findViewById(R.id.et_place);
     //   etPerson = (EditText) findViewById(R.id.et_person);

        register=(Button)findViewById(R.id.btn_register);
        register.setOnClickListener(this);
        tvSendVertifyCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {


        if (view.getId()==R.id.tv_sendVertifyCode){  //发送验证码
            String phoneNumber=etPhoneNumber.getText().toString();
            if (!Utils.isMobile(phoneNumber)){
                Utils.showResultDialog(RegisterActivity.this,"请输入有效的手机号码!",null);
            }else{
                BmobSMS.requestSMSCode(phoneNumber,"注册模板", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功

                            Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                            Utils.toastMessage(RegisterActivity.this,"验证码发送成功");
                        }
                        else{
                            Log.i("smile", ex.getErrorCode()+":"+ex.getMessage());//用于查询本次短信发送详情
                            Utils.toastMessage(RegisterActivity.this,ex.getMessage());
                        }
                    }
                });
            }





        }
        if (view.getId()==R.id.btn_register) {  //注册
            userName=etName.getText().toString();
            password=etPassword.getText().toString();
            password2=etPassword2.getText().toString();
            String phoneNumber=etPhoneNumber.getText().toString();
            String vertifyCode=etVertifyCode.getText().toString();

            if (Utils.isEmpty(userName)|| Utils.isEmpty(password)|| Utils.isEmpty(password2)|| Utils.isEmpty(phoneNumber)|| Utils.isEmpty(vertifyCode)) {
                  //内容不能为空
                Utils.showResultDialog(RegisterActivity.this,"内容不能为空！",null);

            } else if (!password2.equals(password)) {
                Utils.showResultDialog(RegisterActivity.this,"两次输入的密码必须一致！",null);

            }else if (!Utils.isPassword(password)){

                Utils.showResultDialog(RegisterActivity.this,"密码至少为6位，可以包含数字和字母！","提示");



            }
            else{ //注册
             //   Util.showProgressDialog(RegisterActivity.this,null,"正在注册中，请稍候！");
                User user = new User();
                user.setUsername(userName);
                user.setPassword(Utils.encryptBySHA(password));
                user.setMobilePhoneNumber(phoneNumber);  //手机号
                user.setUserType(User.REGISTER_USER);

                user.signOrLogin(vertifyCode, new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e==null){
                            new AlertDialog.Builder(RegisterActivity.this).setMessage("注册成功！请返回登录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();
                        }else{
                           Utils.showResultDialog(RegisterActivity.this,"注册失败："+e.getErrorCode()+","+e.getMessage(),null);
                        }

                    }
                });


            }

        }
    }
}
