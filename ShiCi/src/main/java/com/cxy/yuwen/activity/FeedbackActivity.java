package com.cxy.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.gson.JsonObject;
import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Email;
import com.cxy.yuwen.tool.Util;


public class FeedbackActivity extends AppCompatActivity {
    EditText editFeedback,editQQ,editWechat,editEmail;
    Button buttonCommit;
    public static final String tag="INFO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        MyApplication.getInstance().addActivity(this);
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
                    Util.showProgressDialog(FeedbackActivity.this,null,"正在发送，请稍候");

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
            Util.dismissDialog();
            switch (msg.what) {
                case 1:

                    AlertDialog.Builder builder=new AlertDialog.Builder(FeedbackActivity.this);
                    builder.setMessage("反馈成功，感谢你的反馈！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(FeedbackActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog=builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case 0:
                    new AlertDialog.Builder(FeedbackActivity.this).setMessage("提交失败，请稍候重试！").setPositiveButton("确定", null).create().show();
                    break;
            }
        }
    };



}
