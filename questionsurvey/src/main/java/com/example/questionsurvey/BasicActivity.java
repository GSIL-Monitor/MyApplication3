package com.example.questionsurvey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import Util.ACache;


public class BasicActivity extends AppCompatActivity {
    protected static final String LOG_TAG="question";
    protected ACache mCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        mCache=ACache.get(this);

    }

}
