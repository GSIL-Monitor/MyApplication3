package com.cxy.magazine.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class BindPhoneActivity extends BasicActivity implements View.OnClickListener{

    private EditText etPhone,etVertifyCode;
    private TextView tvSendCode;
    private Button  btnBindPhone;
    String phoneNumber=null,phoneVertifyCode=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        ActionBar bar= getSupportActionBar();
        bar.setTitle("修改手机号码");

    //    MyApplication.getInstance().addActivity(this);


        etPhone=(EditText)findViewById(R.id.et_bindPhone);
        etVertifyCode=(EditText)findViewById(R.id.et_vertifyCode);
        tvSendCode=(TextView)findViewById(R.id.tv_sendCode);
        btnBindPhone=(Button)findViewById(R.id.btn_bindPhone);

        tvSendCode.setOnClickListener(this);
        btnBindPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sendCode:
                phoneNumber=etPhone.getText().toString();
                if (Utils.isMobile(phoneNumber)){
                    BmobSMS.requestSMSCode(phoneNumber, "注册模板",new QueryListener<Integer>() {

                        @Override
                        public void done(Integer smsId,BmobException ex) {
                            if(ex==null){//验证码发送成功
                                Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                                Utils.toastMessage(BindPhoneActivity.this,"验证码发送成功");
                            }
                            else{
                                Utils.showResultDialog(BindPhoneActivity.this,ex.toString(),"验证码发送失败");
                            }
                        }
                    });
                }else {
                    Utils.showResultDialog(BindPhoneActivity.this,"请输入有效的手机号码",null);
                }

                break;
            case R.id.btn_bindPhone:
                phoneVertifyCode=etVertifyCode.getText().toString();
                if (!Utils.isEmpty(phoneVertifyCode)){
                    BmobSMS.verifySmsCode(phoneNumber, phoneVertifyCode, new UpdateListener() {

                        @Override
                        public void done(BmobException ex) {
                            if(ex==null){//短信验证码已验证成功
                                //  Log.i("smile", "验证通过");
                                User user =new User();
                                user.setMobilePhoneNumber(phoneNumber);
                                user.setMobilePhoneNumberVerified(true);
                                User cur = BmobUser.getCurrentUser(User.class);
                                user.update(cur.getObjectId(),new UpdateListener() {

                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                           // Util.toastMessage(BindPhoneActivity.this,"手机号码绑定成功");
                                            AlertDialog dlg = new AlertDialog.Builder(BindPhoneActivity.this).setMessage("手机号绑定成功")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    }).create();
                                            dlg.setCanceledOnTouchOutside(false);
                                            dlg.show();
                                        }else{
                                            Utils.showResultDialog(BindPhoneActivity.this,e.toString(),"绑定失败");
                                        }
                                    }
                                });
                            }else{
                                Log.i("smile", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                            }
                        }
                    });

                }else{
                    Utils.showResultDialog(BindPhoneActivity.this,"请输入验证码",null);
                }

                break;
        }
    }
}
