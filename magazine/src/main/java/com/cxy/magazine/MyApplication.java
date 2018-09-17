package com.cxy.magazine;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;
import com.cxy.magazine.activity.MainActivity;
import com.eagle.pay66.Pay66;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxy on 2018/1/17.
 */

public class MyApplication extends MultiDexApplication {
    private static final String PAY_66_APPLICATION_ID="42cb2259320944d9a297ce09bf23e8eb";
    private static final String XIAOMI_APP_ID="2882303761517702196";
    private static MainActivity sMainActivity = null;

    private List<Activity> activitys = null;
    private List<Activity> closeActivitys=null;
    private static MyApplication instance;
    public static String CURRENT_VERSION="";   //当前版本号

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化66支付
        Pay66.init(PAY_66_APPLICATION_ID, getApplicationContext());

    }
    public MyApplication() {
        activitys = new LinkedList<Activity>();
        closeActivitys=new ArrayList<>();
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
    public  void addCloseActivity(Activity activity){
        if (!closeActivitys.contains(activity)){
            closeActivitys.add(activity);

        }
    }
    public void  closeActivities(){
        for (Activity a:closeActivitys) {
            a.finish();

        }
    }
    public void closeActivity(Activity activity){
        if (activitys.contains(activity)){
            activitys.remove(activity);
        }
        activity.finish();
    }
    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
      // System.exit(0);
    }



}
