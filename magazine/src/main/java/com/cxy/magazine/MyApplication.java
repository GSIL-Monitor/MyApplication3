package com.cxy.magazine;

import android.app.Activity;
import android.app.Application;

import com.cxy.magazine.activity.MainActivity;
import com.eagle.pay66.Pay66;
import com.xiaomi.ad.AdSdk;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxy on 2018/1/17.
 */

public class MyApplication extends Application {
    private static final String PAY_66_APPLICATION_ID="42cb2259320944d9a297ce09bf23e8eb";
    private static final String XIAOMI_APP_ID="2882303761517702196";
    private static MainActivity sMainActivity = null;

    private List<Activity> activitys = null;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化66支付
        Pay66.init(PAY_66_APPLICATION_ID, getApplicationContext());
        //初始化小米广告
        AdSdk.initialize(this, XIAOMI_APP_ID);   //小米广告
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


}
