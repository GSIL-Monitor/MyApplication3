package com.cxy.magazine.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class SettingInfomationActivity extends BasicActivity implements View.OnClickListener {
    private TextView btnLogout,tvBindPhone,tvModifyPsssword;
    private User user=null;
    private View modifyView;
    private Dialog modifyDialog;
    private EditText etOldpassword,etNewPassword,etPassword2;
    private Button btnCancel,btnModify;
    private TextView editUserName;
    private ImageView headImage;
    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private Uri mDestinationUri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_infomation);
     //   MyApplication.getInstance().addActivity(this);
        getSupportActionBar().setTitle("修改个人信息");
        initView();
        mDestinationUri = Uri.fromFile(new File(this.getCacheDir(), "cropImage.jpeg"));
        btnLogout.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
        tvModifyPsssword.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        editUserName.setOnClickListener(this);
        headImage.setOnClickListener(this);

      //  setInfo();

    }

    public void initView(){
        btnLogout=(TextView)findViewById(R.id.btn_logout);
        tvBindPhone=(TextView)findViewById(R.id.bindPhone);
        tvModifyPsssword=(TextView)findViewById(R.id.modifyPassword);
        editUserName=(TextView)findViewById(R.id.tv_userName);
        headImage=(ImageView)findViewById(R.id.userImage);
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
            editUserName.setText(user.getUsername());
            if (!TextUtils.isEmpty(user.getHeadImageUrl())){
                Glide.with(SettingInfomationActivity.this)
                        .load(user.getHeadImageUrl())
                        .error(R.drawable.head_image)
                        .into(headImage);
            }
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

                    Utils.showResultDialog(SettingInfomationActivity.this,"你是用第三方账号进行登录，无需修改密码",null);
                }


                break;
            case R.id.btn_modify:
                modifyPassword();
                break;
            case R.id.btn_cancel:
                modifyDialog.dismiss();
                break;
            case R.id.tv_userName: //修改用户名

                showEditTextDialog();
                break;
            case R.id.userImage:    //修改用户头像
                //TODO：修改用户头像
                selectImage();
                break;


        }
    }

    private void selectImage(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
     //   pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==SettingInfomationActivity.RESULT_OK){
            if (requestCode==GALLERY_REQUEST_CODE){
                UCrop.Options options = new UCrop.Options();
                UCrop.of(data.getData(), mDestinationUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(200, 200)
                        .start(SettingInfomationActivity.this);
            }
            if (requestCode==UCrop.REQUEST_CROP){
                final Uri resultUri = UCrop.getOutput(data);
                handleCropImage(resultUri);
            }
            if (requestCode==UCrop.RESULT_ERROR){
                final Throwable cropError = UCrop.getError(data);
                Utils.toastMessage(SettingInfomationActivity.this,cropError.getMessage());
            }

        }

      //  super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCropImage(Uri resultUri){
        try {
            if (resultUri!=null){
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                File file = new File(new URI(resultUri.toString()));
                //上传数据库并更新
                final BmobFile bmobFile=new BmobFile(file);
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                        //  Utils.toastMessage(SettingInfomationActivity.this,"上传文件成功:" + bmobFile.getFileUrl());
                          User newUser=new User();
                          newUser.setHeadImageUrl(bmobFile.getFileUrl());
                          newUser.update(user.getObjectId(), new UpdateListener() {
                              @Override
                              public void done(BmobException e) {
                                if (e==null){
                                    Utils.toastMessage(SettingInfomationActivity.this,"更新头像成功");
                                    headImage.setImageBitmap(bitmap);
                                   // mCache.put("headImageBitmap",bitmap);
                                }
                                else{
                                    Utils.toastMessage(SettingInfomationActivity.this,e.getMessage());
                                }
                              }
                          });
                        }else{
                            Utils.toastMessage(SettingInfomationActivity.this,"上传文件失败");
                        }
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(SettingInfomationActivity.this);
        builder.setTitle("标题")
                .setPlaceholder("在此输入新的用户名")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(final QMUIDialog dialog, int index) {
                        final String userName = builder.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(userName)) {
                            //修改用户名
                            User newUser=new User();
                            newUser.setUsername(userName);
                            newUser.update(user.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    dialog.dismiss();
                                    if (e==null){
                                        Utils.toastMessage(SettingInfomationActivity.this,"修改用户名成功");
                                        editUserName.setText(userName);
                                    }else{
                                        Utils.toastMessage(SettingInfomationActivity.this,"修改用户名失败:"+e.getMessage());
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(SettingInfomationActivity.this, "请填入用户名", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create().show();
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
        if (Utils.isEmpty(oldPassword)|| Utils.isEmpty(newPassword)|| Utils.isEmpty(newPassword2)){
            Utils.toastMessage(SettingInfomationActivity.this,"内容不能为空");
        }else if(!newPassword.equals(newPassword2)){
            Utils.toastMessage(SettingInfomationActivity.this,"两次输入的密码必须一致");
        }else if(!Utils.isPassword(newPassword)){
            Utils.toastMessage(SettingInfomationActivity.this,"密码至少为6位，可以包含数字和字母");
        }
        else{  //重置密码
           // user=BmobUser.getCurrentUser(User.class);
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("username", user.getUsername());
            query.addWhereEqualTo("password", Utils.encryptBySHA(oldPassword));
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> object,BmobException e) {
                    if(e==null){
                        if (object.size()==1){
                           // Util.toastMessage(SettingInfomationActivity.this,"查询用户成功:"+object.size());
                            User newUsr=new User();
                            newUsr.setPassword(Utils.encryptBySHA(newPassword));
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
                                        Utils.toastMessage(SettingInfomationActivity.this,"密码修改失败:" + e.toString());
                                    }
                                }
                            });

                        }
                        else{
                            Utils.toastMessage(SettingInfomationActivity.this,"旧密码错误");
                        }

                    }else{
                        Utils.toastMessage(SettingInfomationActivity.this,"查询用户失败:" + e.getMessage());
                    }
                }
            });


        }
    }
}
