package com.cxy.magazine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.util.BaseUIListener;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class LoginActivity extends BasicActivity implements View.OnClickListener{
    EditText etUserName,etPassword;
    TextView tvRegister,tvQQLogin,tvForgetPassword;
    Button btnLogin;
    public static Tencent mTencent;
    private  String token;
    private UserInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // MyApplication.getInstance().addActivity(this);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("登录账号");
        bar.setDisplayHomeAsUpEnabled(true);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Utils.TencentAppId, this);
        }

        etUserName=(EditText)findViewById(R.id.et_account) ;
        etPassword=(EditText)findViewById(R.id.et_pwd) ;
        btnLogin=(Button) findViewById(R.id.btn_login);
        tvRegister=(TextView)findViewById(R.id.tv_register);
        tvQQLogin=(TextView)findViewById(R.id.tv_qq);
        tvForgetPassword=(TextView)findViewById(R.id.tv_forgetPasword);

        tvRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvQQLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tv_register) {  //注册
            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        if (view.getId()==R.id.btn_login){  //登录

             Utils.showTipDialog(LoginActivity.this,"登录中...",QMUITipDialog.Builder.ICON_TYPE_LOADING);

            final String userName=etUserName.getText().toString();
            final String password=etPassword.getText().toString();

           /* User user=new User();
            user.setUsername(userName);
            user.setPassword(Utils.encryptBySHA(password));*/
            BmobUser.loginByAccount(userName, Utils.encryptBySHA(password), new LogInListener<User>() {

                @Override
                public void done(User user, BmobException e) {
                    if(user!=null){
                        Log.i("smile","用户登陆成功");
                        Utils.dismissDialog();
                        finish();
                    }else{

                        BmobUser.loginByAccount(userName, password, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                               if (user!=null){
                                   Utils.dismissDialog();
                                   finish();
                               }else{
                                   Utils.dismissDialog();
                                   Utils.showResultDialog(LoginActivity.this,"用户名或密码错误，请重新输入！",null);
                               }
                            }
                        });

                    }
                }
            });
           /* user.login(new SaveListener<BmobUser>() {

                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if(e==null){  //login success
                         Utils.dismissDialog();


                        finish();
                    }else{
                        Utils.dismissDialog();
                        Utils.showResultDialog(LoginActivity.this,"用户名或密码错误,请重新输入！",null);
                        Log.i(LOG_TAG,e.toString());
                    }
                }
            });*/



        }
        if (view.getId()==R.id.tv_qq) { //qq登录
            onClickLogin();

        }

        if (view.getId()==R.id.tv_forgetPasword){
            Intent intent=new Intent(LoginActivity.this,FindPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void onClickLogin() {

        mTencent.login(this, "all", loginListener);
     //   Utils.showProgressDialog(LoginActivity.this, null, "请稍后");

     //   loginDialog.show();
        Utils.showTipDialog(LoginActivity.this,"登录中...",QMUITipDialog.Builder.ICON_TYPE_LOADING);
        Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());

    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject jsonObject) {
            Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(jsonObject);

        }
    };


    public   void initOpenidAndToken(JSONObject jsonObject) {
        try {
            token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);

                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ,token, expires,openId);
                loginWithAuth(authInfo);  //插入数据库


            }
        } catch(Exception e) {
        }



    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Utils.dismissDialog();
                Utils.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Utils.dismissDialog();
                Utils.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }

            Log.d("SDKQQAgentPref", "登录成功" );

            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject jsonObject) {

        }

        @Override
        public void onError(UiError e) {
            Utils.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
            Utils.dismissDialog();
        }

        @Override
        public void onCancel() {
            Utils.toastMessage(LoginActivity.this, "onCancel: ");
            Utils.dismissDialog();

        }
    }


    /**
     * @method loginWithAuth
     * @param authInfo
     * @return void
     * @exception
     */
    public  void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo){
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

            @Override
            public void done(JSONObject userAuth,BmobException e) {
                Log.i("SDKQQAgentPref",authInfo.getSnsType()+"登陆成功返回:"+userAuth);
                Utils.dismissDialog();
            //    getUserInfo();  //获取QQ用户的信息
                if (e==null){
                    finish();
                }else{
                  Log.e(LOG_TAG,e.getMessage());
                  Utils.showResultDialog(LoginActivity.this,"登录失败，请稍后重试","提示");
                }


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SDKQQAgentPref", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.dismissDialog();
        finish();
    }*/
}
