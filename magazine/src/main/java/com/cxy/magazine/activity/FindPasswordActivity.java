package com.cxy.magazine.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.util.Utils;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class FindPasswordActivity extends BasicActivity implements View.OnClickListener{

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

      //  MyApplication.getInstance().addActivity(this);

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
            if (Utils.isMobile(phoneNumber)){

                BmobSMS.requestSMSCode(phoneNumber,"杂志天下", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功
                            Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                            Utils.toastMessage(FindPasswordActivity.this,"验证码发送成功");
                        }else{
                            Utils.showResultDialog(FindPasswordActivity.this, ex.getErrorCode()+","+ex.getMessage(),"验证码发送失败");
                        }
                    }
                });
            }else{
                Utils.showResultDialog(FindPasswordActivity.this,"请输入有效的手机号！",null);
            }
        }

        if (v.getId()==R.id.btn_resetPassword){

            phoneVertifyCode=etPhoneCode.getText().toString();
             if (!Utils.isEmpty(phoneNumber)){

                  User.resetPasswordBySMSCode(phoneVertifyCode, Utils.encryptBySHA("1234567"), new UpdateListener() {

                     @Override
                     public void done(BmobException ex) {
                         if(ex==null){
                             Log.i("smile", "密码重置成功");
                             Utils.showResultDialog(FindPasswordActivity.this,"密码已被重置为：1234567！请及时修改你的密码",null);
                         }else{
                             Utils.showResultDialog(FindPasswordActivity.this,"密码重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage(),null);
                             Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                         }
                     }
                 });

             }else{
                 Utils.showResultDialog(FindPasswordActivity.this,"请输入有效的验证码！",null);
             }


            }

        }

}
