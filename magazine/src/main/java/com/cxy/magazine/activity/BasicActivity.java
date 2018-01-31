package com.cxy.magazine.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cxy.magazine.MyApplication;
import com.cxy.magazine.util.ACache;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.Utils;


public class BasicActivity extends AppCompatActivity {
    protected ACache mCache;
    protected static final String LOG_TAG = "com.cxy.magzine";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache=ACache.get(this);
        MyApplication.getInstance().addActivity(this);
        if(!NetWorkUtils.isNetworkConnected(this)){
            Utils.toastMessage(this,"网络未连接,请检查网络状态");
        }

    }
}
