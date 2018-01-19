package com.example.questionsurvey;


import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Util.ACache;
import Util.OkHttpUtil;
import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends BasicActivity {




    private UserLoginTask mAuthTask = null;

    // UI references.
    @BindView(R.id.email) EditText mNameView;
    @BindView(R.id.password) EditText mPasswordView;

    @BindView(R.id.cb_mima) CheckBox cbRemberPassword;
    @BindView(R.id.cb_auto) CheckBox cbAutoLogin;
    private String userId="";
  //  private ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mCache=ACache.get(this);

        mNameView.setText(mCache.getAsString("userName"));
        mPasswordView.setText(mCache.getAsString("userPassword"));

        String autoLogin=mCache.getAsString("AutoLogin");
        if (autoLogin!=null&&"1".equals(autoLogin)){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);   //实现自动登录
            startActivity(intent);
        }


        //回车键事件
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });



    }






    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }


        mNameView.setError(null);
        mPasswordView.setError(null);


        String userName = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(userName)) {
            mNameView.setError(getString(R.string.error_invalid_email));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {


            mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    //验证用户名的有效性
    private boolean isNameValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >=3;
    }





    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;

        UserLoginTask(String name, String password) {
            mName = name;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            //用户登录
            Map hashMap=new HashMap<String,String>();
            hashMap.put("userName",mName);
            hashMap.put("password",mPassword);
            try {
                String response=OkHttpUtil.get(Util.SERVER_URL+"login",hashMap);
                JSONObject responseObject=new JSONObject(response);
                String responseCode=responseObject.getString("responseCode");
                if ("00000".equals(responseCode)){
                    JSONObject  userObject=responseObject.getJSONObject("responseBody");
                    userId=userObject.getString("id");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
           /* if ("admin".equals(mName)&&"admin".equals(mPassword)){
                return true;
            }*/


            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            if (success) {
               if (cbRemberPassword.isChecked()){  //记住密码
                   mCache.put("userName",mName);
                   mCache.put("userPassword",mPassword);
                   mCache.put("userId",userId);
               }
               if (cbAutoLogin.isChecked()){    //自动登录
                   mCache.put("AutoLogin","1",10*24*60*60);    //10天之内免登录

               }
               Intent intent=new Intent(LoginActivity.this,MainActivity.class);
               startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }


    }


}

