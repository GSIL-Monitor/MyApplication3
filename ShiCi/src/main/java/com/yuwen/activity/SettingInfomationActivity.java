package com.yuwen.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yuwen.MyApplication;
import com.yuwen.bmobBean.User;
import com.yuwen.myapplication.R;
import com.yuwen.tool.CommonUtil;
import com.yuwen.tool.Util;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class SettingInfomationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnLogout,tvBindPhone,tvModifyPsssword;
    private User user=null;
    private View modifyView;
    private Dialog modifyDialog;
    private EditText etOldpassword,etNewPassword,etPassword2;
    private Button btnCancel,btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_infomation);
        MyApplication.getInstance().addActivity(this);

        initView();
        btnLogout.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
        tvModifyPsssword.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnModify.setOnClickListener(this);

        setInfo();

    }

    public void initView(){
        btnLogout=(TextView)findViewById(R.id.btn_logout);
        tvBindPhone=(TextView)findViewById(R.id.bindPhone);
        tvModifyPsssword=(TextView)findViewById(R.id.modifyPassword);
        modifyView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_password, null);
        modifyDialog=new Dialog(this,R.style.BottomDialog);
        modifyDialog.setContentView(modifyView);


        etOldpassword=(EditText)modifyView.findViewById(R.id.oldPassword);
        etNewPassword=(EditText)modifyView.findViewById(R.id.newPassword);
        etPassword2=(EditText)modifyView.findViewById(R.id.newPassword2);
        btnCancel=(Button)modifyView.findViewById(R.id.btn_cancel);
        btnModify=(Button)modifyView.findViewById(R.id.btn_modify);

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

                finish();

                break;

            case R.id.bindPhone:
                Intent intent=new Intent(SettingInfomationActivity.this,BindPhoneActivity.class);
                startActivity(intent);

                break;
            case R.id.modifyPassword:  //修改密码

                Integer userType=user.getUserType();
                if (userType==User.REGISTER_USER){
                      showDialog();
                }else {

                    Util.showResultDialog(SettingInfomationActivity.this,"你是用第三方账号进行登录，无需修改密码",null);
                }


                break;
            case R.id.btn_modify:
                modifyPassword();
                break;
            case R.id.btn_cancel:
                modifyDialog.dismiss();
                break;



        }
    }


    /**
     * 修改密码对话框
     */
    private void showDialog(){


       /* ViewGroup.LayoutParams layoutParams = modifyView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.width = 300;
        modifyView.setLayoutParams(layoutParams);*/

        modifyDialog.getWindow().setGravity(Gravity.CENTER);
        modifyDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        modifyDialog.show();
      //  modifyDialog.setCanceledOnTouchOutside(true);
    }

    private void modifyPassword(){
        String oldPassword=etOldpassword.getText().toString();
        String newPassword=etNewPassword.getText().toString();
        String newPassword2=etPassword2.getText().toString();
        if (CommonUtil.isEmpty(oldPassword)||CommonUtil.isEmpty(newPassword)||CommonUtil.isEmpty(newPassword2)){
            Util.toastMessage(SettingInfomationActivity.this,"内容不能为空");
        }else if(!newPassword.equals(newPassword2)){
            Util.toastMessage(SettingInfomationActivity.this,"两次输入的密码必须一致");
        }else{  //重置密码
            User.updateCurrentUserPassword("旧密码", "新密码", new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        modifyDialog.dismiss();
                        Util.toastMessage(SettingInfomationActivity.this,"密码修改成功，可以用新密码进行登录啦");
                    }else{
                        Util.toastMessage(SettingInfomationActivity.this,"密码修改失败:" + e.getMessage());
                    }
                }

            });

        }
    }
}
