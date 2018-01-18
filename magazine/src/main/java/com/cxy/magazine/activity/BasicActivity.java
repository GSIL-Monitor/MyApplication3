package com.cxy.magazine.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cxy.magazine.MyApplication;
import com.cxy.magazine.util.ACache;


public class BasicActivity extends AppCompatActivity {
    protected ACache mCache;
    protected static final String LOG_TAG = "com.cxy.magzine";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache=ACache.get(this);
        MyApplication.getInstance().addActivity(this);

    }
}
