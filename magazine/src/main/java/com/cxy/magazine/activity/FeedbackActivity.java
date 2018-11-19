package com.cxy.magazine.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.FeedbackBean;
import com.cxy.magazine.util.Utils;
import com.google.gson.JsonObject;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class FeedbackActivity extends BasicActivity {
    EditText editFeedback,editQQ,editWechat,editEmail;
    Button buttonCommit;
    public static final String tag="INFO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    //    MyApplication.getInstance().addActivity(this);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("用户反馈");
        bar.setDisplayHomeAsUpEnabled(true);

        editFeedback=(EditText) findViewById(R.id.edit_feedback);
        editQQ=(EditText)findViewById(R.id.edit_qq);
        editEmail=(EditText)findViewById(R.id.edit_mail);
        editWechat=(EditText)findViewById(R.id.edit_wechat);
        buttonCommit=(Button)findViewById(R.id.buttonCommit);

        TextView tvPs=(TextView)findViewById(R.id.ps_tv);
       // tvPs.setText(R.string.downloadHint);
      //  tvPs.setAutoLinkMask(Linkify.WEB_URLS);
        tvPs.setMovementMethod(LinkMovementMethod.getInstance());

        buttonCommit.setOnClickListener(new View.OnClickListener() {
            String qq="",weChat="",email="",feedback="";
            @Override
            public void onClick(View view) {
                feedback=editFeedback.getText().toString();
                qq=editQQ.getText().toString();
                weChat=editWechat.getText().toString();
                email=editEmail.getText().toString();
                if (feedback.length()>0){
                 //   Utils.showProgressDialog(FeedbackActivity.this,"正在发送，请稍候");
                 /*   Utils.showTipDialog(FeedbackActivity.this,"正在发送，请稍候", QMUITipDialog.Builder.ICON_TYPE_LOADING);
                    Thread thread=new Thread(new Runnable() {
                       @Override
                       public void run() {
                           JsonObject jsonObject=new JsonObject();
                           jsonObject.addProperty("反馈内容",feedback);
                           jsonObject.addProperty("QQ",qq);
                           jsonObject.addProperty("微信",weChat);
                           jsonObject.addProperty("邮箱",email);
                           Log.i(tag,jsonObject.toString());
                           boolean flag= EmailUtil.send(jsonObject.toString());
                           if (flag){
                              mUIHandler.sendEmptyMessage(1);
                          }else{
                              mUIHandler.sendEmptyMessage(0);
                          }
                       }
                   }) ;

                    thread.start();
                  */

                    FeedbackBean feedbackBean=new FeedbackBean();
                    feedbackBean.setFeedBackContent(feedback);
                    feedbackBean.setEmail(email);
                    feedbackBean.setQq(qq);
                    feedbackBean.setWechat(weChat);

                    feedbackBean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                             if (e==null){
                                 AlertDialog.Builder builder=new AlertDialog.Builder(FeedbackActivity.this);
                                 builder.setMessage("反馈成功，感谢你的反馈！");
                                 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                     }
                                 });

                                 AlertDialog alertDialog=builder.create();
                                 alertDialog.setCancelable(false);
                                 alertDialog.show();
                             }  else{
                                 new AlertDialog.Builder(FeedbackActivity.this).setMessage("提交失败，请稍候重试！").setPositiveButton("确定", null).create().show();
                             }
                        }
                    });


                }else{
                    new AlertDialog.Builder(FeedbackActivity.this).setMessage("反馈内容不能为空哦！").setPositiveButton("确定", null).create().show();
                }
            }
        });
    }



    /*private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Utils.dismissDialog();
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
    };*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
