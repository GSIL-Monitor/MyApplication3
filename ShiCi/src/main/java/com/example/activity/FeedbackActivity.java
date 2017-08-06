package com.example.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;
import com.google.gson.JsonObject;
import com.myapp.tool.Email;


public class FeedbackActivity extends AppCompatActivity {
    EditText editFeedback,editQQ,editWechat,editEmail;
    Button buttonCommit;
    public static final String tag="INFO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("用户反馈");

        editFeedback=(EditText) findViewById(R.id.edit_feedback);
        editQQ=(EditText)findViewById(R.id.edit_qq);
        editEmail=(EditText)findViewById(R.id.edit_mail);
        editWechat=(EditText)findViewById(R.id.edit_wechat);
        buttonCommit=(Button)findViewById(R.id.buttonCommit);

        buttonCommit.setOnClickListener(new View.OnClickListener() {
            String qq="",weChat="",email="",feedback="";
            @Override
            public void onClick(View view) {
                feedback=editFeedback.getText().toString();
                qq=editQQ.getText().toString();
                weChat=editWechat.getText().toString();
                email=editEmail.getText().toString();
                if (feedback.length()>0){


                    Thread thread=new Thread(new Runnable() {
                       @Override
                       public void run() {
                           JsonObject jsonObject=new JsonObject();
                           jsonObject.addProperty("反馈内容",feedback);
                           jsonObject.addProperty("QQ",qq);
                           jsonObject.addProperty("微信",weChat);
                           jsonObject.addProperty("邮箱",email);
                           Log.i(tag,jsonObject.toString());
                           boolean isSuccessed= Email.postEmail(jsonObject.toString());
                           if (isSuccessed){
                               mUIHandler.sendEmptyMessage(1);

                           }else{
                               mUIHandler.sendEmptyMessage(0);

                           }
                       }
                   }) ;

                    thread.start();



                }else{
                    new AlertDialog.Builder(FeedbackActivity.this).setMessage("反馈内容不能为空哦！").setPositiveButton("确定", null).create().show();
                }
            }
        });
    }



    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    new AlertDialog.Builder(FeedbackActivity.this).setMessage("反馈成功，感谢你的反馈！").setPositiveButton("确定", null).create().show();
                    break;
                case 0:
                    new AlertDialog.Builder(FeedbackActivity.this).setMessage("提交失败，请稍候重试！").setPositiveButton("确定", null).create().show();
                    break;
            }
        }
    };
}
