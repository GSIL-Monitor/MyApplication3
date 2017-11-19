package com.cxy.yuwen.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.YilinAdapter;

public class YilinArticleActivity extends AppCompatActivity {

    private TextView articleTitle,articleContent;
    private String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yilin_article);
        initView();
        Intent intent=getIntent();
        url= YilinAdapter.YILIN_URL+intent.getStringExtra("url");
    }

    public void initView(){
        articleTitle=(TextView) findViewById(R.id.articleTitle);
        articleContent=(TextView)findViewById(R.id.articleContent);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {

        }
    };
}
