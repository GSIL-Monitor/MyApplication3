package com.cxy.yuwen.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.CommonUtil;
import com.cxy.yuwen.tool.Util;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class SettingInfomationActivity extends BasicActivity implements View.OnClickListener {
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
     //   MyApplication.getInstance().addActivity(this);

        initView();
        btnLogout.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
        tvModifyPsssword.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnModify.setOnClickListener(this);

      //  setInfo();

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


        /*ViewGroup.LayoutParams layoutParams = modifyView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels-160;
        modifyView.setLayoutParams(layoutParams);*/

        modifyDialog.getWindow().setGravity(Gravity.CENTER);
        modifyDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        modifyDialog.show();
      //  modifyDialog.setCanceledOnTouchOutside(true);
    }

    private void modifyPassword(){
        String oldPassword=etOldpassword.getText().toString();
        final String newPassword=etNewPassword.getText().toString();
        String newPassword2=etPassword2.getText().toString();
        if (CommonUtil.isEmpty(oldPassword)||CommonUtil.isEmpty(newPassword)||CommonUtil.isEmpty(newPassword2)){
            Util.toastMessage(SettingInfomationActivity.this,"内容不能为空");
        }else if(!newPassword.equals(newPassword2)){
            Util.toastMessage(SettingInfomationActivity.this,"两次输入的密码必须一致");
        }else if(!CommonUtil.isPassword(newPassword)){
            Util.toastMessage(SettingInfomationActivity.this,"密码至少为6位，可以包含数字和字母");
        }
        else{  //重置密码
           // user=BmobUser.getCurrentUser(User.class);
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("username", user.getUsername());
            query.addWhereEqualTo("password",CommonUtil.encryptBySHA(oldPassword));
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> object,BmobException e) {
                    if(e==null){
                        if (object.size()==1){
                           // Util.toastMessage(SettingInfomationActivity.this,"查询用户成功:"+object.size());
                            User newUsr=new User();
                            newUsr.setPassword(CommonUtil.encryptBySHA(newPassword));
                            newUsr.update(user.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        modifyDialog.dismiss();

                                        AlertDialog dlg = new AlertDialog.Builder(SettingInfomationActivity.this).setMessage("密码修改成功，可以用新密码进行登录啦")
                                                .setPositiveButton("返回登录", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BmobUser.logOut();
                                                        Intent intent=new Intent(SettingInfomationActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("取消", null).create();
                                        dlg.setCanceledOnTouchOutside(false);
                                        dlg.show();

                                    }else{
                                        Util.toastMessage(SettingInfomationActivity.this,"密码修改失败:" + e.toString());
                                    }
                                }
                            });

                        }
                        else{
                            Util.toastMessage(SettingInfomationActivity.this,"旧密码错误");
                        }

                    }else{
                        Util.toastMessage(SettingInfomationActivity.this,"查询用户失败:" + e.getMessage());
                    }
                }
            });


        }
    }
}
