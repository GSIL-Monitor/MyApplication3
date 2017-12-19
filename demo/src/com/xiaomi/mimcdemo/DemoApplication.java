package com.xiaomi.mimcdemo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mimcdemo.common.SystemUtils;
import com.xiaomi.push.mimc.MimcClient;
import com.xiaomi.push.mimc.MimcLogger;

public class DemoApplication extends Application {
    public static final String TAG = "com.xiaomi.MimcDemo";

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {}

            @Override
            public void log(String content, Throwable t) {
                Log.w(TAG, content, t);
                Log2UI(content);
            }

            @Override
            public void log(String content) {
                Log.w(TAG, content);
                Log2UI(content);
            }

            private void Log2UI(String content) {
                Intent intent = new Intent(Constant.ACTION);
                intent.putExtra("msg", content.toString());
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        };
        MimcLogger.setLogger(getApplicationContext(),newLogger);
        MyLog.setLogLevel(MyLog.INFO);
        MyLog.w("Application start");

        SystemUtils.initialize(this);
        MimcClient.initialize(this);
    }

}
