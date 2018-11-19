package com.cxy.magazine.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class FindPasswordActivity extends BasicActivity implements View.OnClickListener{

    EditText etPhoneNumber,etPhoneCode,etEmail;
    Button btnFindPassword;
    TextView tvSendCode;
    String  phoneNumber=null,phoneVertifyCode=null;
    LinearLayout phoneLayout,emailLayout;
    RadioButton  phoneRadio,emailRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("重置密码");

      //  MyApplication.getInstance().addActivity(this);

        etPhoneNumber=(EditText) findViewById(R.id.et_userPhone);
        etPhoneCode=(EditText)findViewById(R.id.et_vertifyCode);
        etEmail=(EditText)findViewById(R.id.resetEmail);
        tvSendCode=(TextView)findViewById(R.id.tv_sendCode);
        btnFindPassword=(Button)findViewById(R.id.btn_resetPassword);
        phoneLayout=(LinearLayout)findViewById(R.id.phone_layout);
        emailLayout=(LinearLayout)findViewById(R.id.email_layout);
        phoneRadio=(RadioButton)findViewById(R.id.radio_phone);
        emailRadio=(RadioButton)findViewById(R.id.radio_email);

        tvSendCode.setOnClickListener(this);
        btnFindPassword.setOnClickListener(this);
        phoneRadio.setOnClickListener(this);
        emailRadio.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.radio_email){
            boolean checked = ((RadioButton) v).isChecked();
            if (checked){
                emailLayout.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.GONE);
                etPhoneNumber.setText(null);
                etPhoneCode.setText(null);
                etEmail.setText(null);
            }
        }
        if (v.getId()==R.id.radio_phone){
            emailLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.VISIBLE);
            etPhoneNumber.setText(null);
            etPhoneCode.setText(null);
            etEmail.setText(null);
        }
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
                Utils.toastMessage(FindPasswordActivity.this,"请输入有效的手机号");
            }
        }

        if (v.getId()==R.id.btn_resetPassword){
            phoneNumber=etPhoneNumber.getText().toString();
            phoneVertifyCode=etPhoneCode.getText().toString();
            final String email=etEmail.getText().toString();

             if (!Utils.isEmpty(phoneNumber)){

                 if (Utils.isMobile(phoneNumber)){
                     if (!Utils.isEmpty(phoneVertifyCode)){
                         //手机号重置密码
                         User.resetPasswordBySMSCode(phoneVertifyCode, Utils.encryptBySHA("1234567"), new UpdateListener() {
                             @Override
                             public void done(BmobException ex) {
                                 if(ex==null){
                                     Log.i("smile", "密码重置成功");
                                     Utils.showResultDialog(FindPasswordActivity.this, "密码已被重置为：1234567！请及时修改你的密码", null, new QMUIDialogAction.ActionListener() {
                                         @Override
                                         public void onClick(QMUIDialog dialog, int index) {
                                             finish();
                                         }
                                     });
                                 }else{
                                     Utils.showResultDialog(FindPasswordActivity.this,"密码重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage(),null);
                                     Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                                 }
                             }
                         });

                     }else {
                         Utils.toastMessage(FindPasswordActivity.this,"请输入验证码");
                     }
                 }else{
                     Utils.toastMessage(FindPasswordActivity.this,"请输入有效的手机号");
                 }


             }
            else if (!Utils.isEmpty(email)){
                 if (!Utils.isEmail(email)){
                     Utils.toastMessage(FindPasswordActivity.this,"请输入有效的邮箱");
                 }else{
                     //邮箱重置密码
                     BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                         @Override
                         public void done(BmobException e) {
                             if(e==null){
                                 Utils.showResultDialog(FindPasswordActivity.this, "重置密码请求成功，请到" + email + "邮箱进行密码重置操作", null, new QMUIDialogAction.ActionListener() {
                                     @Override
                                     public void onClick(QMUIDialog dialog, int index) {
                                         finish();
                                     }
                                 });
                             }else{
                                 Utils.toastMessage(FindPasswordActivity.this,"重置失败"+e.getMessage());
                             }
                         }
                     });
                 }


             }else{
                 Utils.showResultDialog(FindPasswordActivity.this,"请输入手机或者邮箱，用以重置密码",null);
             }


            }

        }

}
