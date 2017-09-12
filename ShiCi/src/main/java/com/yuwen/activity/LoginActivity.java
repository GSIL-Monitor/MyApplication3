package com.yuwen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yuwen.BmobBean.User;
import com.yuwen.myapplication.R;
import com.yuwen.tool.BaseUIListener;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etUserName,etPassword;
    TextView tvRegister,tvQQLogin;
    Button btnLogin;
    public static Tencent mTencent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Utils.TencentAppId, this);
        }

        etUserName=(EditText)findViewById(R.id.et_account) ;
        etPassword=(EditText)findViewById(R.id.et_pwd) ;
        btnLogin=(Button) findViewById(R.id.btn_login);
        tvRegister=(TextView)findViewById(R.id.tv_register);
        tvQQLogin=(TextView)findViewById(R.id.tv_qq);
        tvRegister.setOnClickListener(this);
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
             String userName=etUserName.getText().toString();
             String password=etPassword.getText().toString();

             User user=new User();
             user.setUsername(userName);
             user.setPassword(password);

             user.login(new SaveListener<BmobUser>() {

                 @Override
                 public void done(BmobUser bmobUser, BmobException e) {
                     if(e==null){  //login success
                         Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                         startActivity(intent);
                     }else{
                         Log.i(AdApplication.TAG,e.toString());
                     }
                 }
             });



         }
        if (view.getId()==R.id.tv_qq) { //qq登录
            onClickLogin();
        }
    }

    /*class NewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();

            switch (v.getId()) {
                case R.id.tv_qq:
                    onClickLogin();

                    return;

            }

        }
    }*/

    private void onClickLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", loginListener);

            Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        }
    }
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
          //  updateUserInfo();
          //  updateLoginButton();
        }
    };
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            Util.showResultDialog(LoginActivity.this, response.toString(), "登录成功");
            Log.d("SDKQQAgentPref", "登录成功" );
            // 有奖分享处理
           // handlePrizeShare();
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(LoginActivity.this, "onCancel: ");
            Util.dismissDialog();

        }
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


}
