package com.cxy.magazine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.cxy.magazine.MyApplication;
import com.cxy.magazine.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PushActivity extends BasicActivity {

    @BindView(R.id.pushTitle)
    TextView tvTitle;
    @BindView(R.id.pushContent)
    TextView tvContent;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("消息通知");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
