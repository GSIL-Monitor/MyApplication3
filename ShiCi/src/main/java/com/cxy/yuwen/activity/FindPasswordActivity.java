package com.cxy.yuwen.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Util;
import com.cxy.yuwen.tool.CommonUtil;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etPhoneNumber,etPhoneCode;
    Button btnFindPassword;
    TextView tvSendCode;
    String  phoneNumber=null,phoneVertifyCode=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("重置密码");

        MyApplication.getInstance().addActivity(this);

        etPhoneNumber=(EditText) findViewById(R.id.et_userPhone);
        etPhoneCode=(EditText)findViewById(R.id.et_vertifyCode);
        tvSendCode=(TextView)findViewById(R.id.tv_sendCode);
        btnFindPassword=(Button)findViewById(R.id.btn_resetPassword);

        tvSendCode.setOnClickListener(this);
        btnFindPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tv_sendCode){  //发送验证码
            phoneNumber=etPhoneNumber.getText().toString();
            if (CommonUtil.isMobile(phoneNumber)){

                BmobSMS.requestSMSCode(phoneNumber,"注册模板", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功
                            Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                        }else{
                            Util.showResultDialog(FindPasswordActivity.this, ex.getErrorCode()+","+ex.getMessage(),"验证码发送失败");
                        }
                    }
                });
            }else{
                Util.showResultDialog(FindPasswordActivity.this,"请输入有效的手机号！",null);
            }
        }

        if (v.getId()==R.id.btn_resetPassword){

            phoneVertifyCode=etPhoneCode.getText().toString();
             if (!CommonUtil.isEmpty(phoneNumber)){

                  User.resetPasswordBySMSCode(phoneVertifyCode,CommonUtil.encryptBySHA("1234567"), new UpdateListener() {

                     @Override
                     public void done(BmobException ex) {
                         if(ex==null){
                             Log.i("smile", "密码重置成功");
                             Util.showResultDialog(FindPasswordActivity.this,"密码已被重置为：1234567！请及时修改你的密码",null);
                         }else{
                             Util.showResultDialog(FindPasswordActivity.this,"密码重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage(),null);
                             Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                         }
                     }
                 });

             }else{
                 Util.showResultDialog(FindPasswordActivity.this,"请输入有效的验证码！",null);
             }


            }

        }

}
