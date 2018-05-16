package com.cxy.yuwen;

/**
 * Created by cxy on 2017/4/18.
 */

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

import com.eagle.pay66.Pay66;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.cxy.yuwen.activity.MainActivity;


import java.util.LinkedList;
import java.util.List;



/**
 * 您可以参考本类中的代码来接入小米游戏广告SDK。在接入过程中，有如下事项需要注意：
 * 1.请将 APP_ID 值替换成您在小米开发者网站上申请的 AppID。
 * 2.调试广告时，需要调用 AdSdk.setMockOn()；正式发布时，请勿调用 AdSdk.setMockOn()
 */
public class MyApplication extends MultiDexApplication {

    //请注意，千万要把以下的 APP_ID 替换成您在小米开发者网站上申请的 AppID。否则，可能会影响你的应用广告收益。
    /**
     * AppID： 2882303761517566012
     AppKey： 5431756668012
     AppSecret： bKHfBLzT9Z1dRAz1amNhHA==
     */
    private static final String APP_ID = "2882303761517566012";
    private static final String APP_KEY="5431756668012";   //5431756668012
    private static final String PAY_66_APPLICATION_ID="dbb2622519e940d6900a35baf2dac30a";

    public static final String TAG = "com.example.yuwen";
    private static DemoHandler sHandler = null;
    private static MainActivity sMainActivity = null;

    private List<Activity> activitys = null;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();


        //66支付
        Pay66.init(PAY_66_APPLICATION_ID, getApplicationContext());

        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        //打开Log
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
        if (sHandler == null) {
            sHandler = new DemoHandler(getApplicationContext());
        }


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
        if (activitys != null && activitys.size() > 0) {
            if(!activitys.contains(activity)){
                activitys.add(activity);
            }
        }else{
            activitys.add(activity);
        }
    }
    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }



    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static DemoHandler getHandler() {
        return sHandler;
    }

    public static void setMainActivity(MainActivity activity) {
        sMainActivity = activity;
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            if (sMainActivity != null) {
              //  sMainActivity.refreshLogInfo();
              //  Toast.makeText(context, MainActivity.logList.toString(), Toast.LENGTH_LONG).show();
                Log.i(TAG, MainActivity.logList.toString());
            }
            if (!TextUtils.isEmpty(s)) {
              //  Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                Log.i(TAG, MainActivity.logList.toString());
            }
        }
    }


}
