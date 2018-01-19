package com.example.questionsurvey;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxy on 2018/1/3.
 */

public class MyApplication extends Application {
    private List<Activity> activitys = null;
    private static MyApplication instance;
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
