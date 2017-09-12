package com.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yuwen.myapplication.R;


public class PushActivity extends AppCompatActivity {

    private TextView tvTitle,tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        tvTitle=(TextView) findViewById(R.id.pushTitle);
        tvContent=(TextView)findViewById(R.id.pushContent);

        Intent intent=this.getIntent();
        String title=intent.getStringExtra("title");
        String description=intent.getStringExtra("description");

        tvTitle.setText(title);
        tvContent.setText(description);
    }
}
