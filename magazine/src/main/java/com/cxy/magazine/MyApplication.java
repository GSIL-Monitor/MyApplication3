package com.cxy.magazine;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cxy.magazine.activity.MainActivity;
import com.payelves.sdk.EPay;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxy on 2018/1/17.
 */

public class MyApplication extends MultiDexApplication {
    private static final String PAY_66_APPLICATION_ID="42cb2259320944d9a297ce09bf23e8eb";
    private static final String XIAOMI_APP_ID="2882303761517702196";
    private static final String XIAOMI_APP_KEY="5141770234196";

    private List<Activity> activitys = null;
    private static MyApplication instance;
    public static final String TAG="xiaomipush";

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化66支付
      //  Pay66.init(PAY_66_APPLICATION_ID, getApplicationContext());

        //初始化支付精灵
        String openId = "vR7uw3MEF";
        String token = "7188e8cb933b4e929d60ef80a748e938";
        String appKey = "6955625076948993";
        String channel = "xiaomi";

        EPay.getInstance(getApplicationContext()).init(openId,token,appKey,channel);

        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, XIAOMI_APP_ID, XIAOMI_APP_KEY);
        }
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);



    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public MyApplication() {
        activitys = new LinkedList<Activity>();
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return
     */
    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }
    public void addActivity(Activity activity) {
        if (activitys != null ) {
            if(!activitys.contains(activity)){
                activitys.add(activity);
            }
        }
    }

/*    public void closeActivity(Activity activity){
        if (activitys.contains(activity)){
            activitys.remove(activity);
        }
        activity.finish();
    }*/
    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                if (activity!=null){
                    activity.finish();
                }

            }
        }
      // System.exit(0);
    }



}
