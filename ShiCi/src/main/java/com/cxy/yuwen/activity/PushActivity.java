package com.cxy.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.R;


public class PushActivity extends BasicActivity {

    private TextView tvTitle,tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
       // MyApplication.getInstance().addActivity(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

        tvTitle=(TextView) findViewById(R.id.pushTitle);
        tvContent=(TextView)findViewById(R.id.pushContent);

        Intent intent=this.getIntent();
        String title=intent.getStringExtra("title");
        String description=intent.getStringExtra("description");

        tvTitle.setText(title);
        tvContent.setText(description);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent=new Intent(PushActivity.this,MainActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
