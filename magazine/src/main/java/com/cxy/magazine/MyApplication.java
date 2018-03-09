package com.cxy.magazine;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alipay.euler.andfix.patch.PatchManager;
import com.cxy.magazine.activity.MainActivity;
import com.cxy.magazine.util.Utils;
import com.eagle.pay66.Pay66;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.xiaomi.market.sdk.Patcher;
//import com.xiaomi.ad.AdSdk;

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
    public static PatchManager mPatchManager;
    public static String CURRENT_VERSION="";   //当前版本号

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化66支付
        Pay66.init(PAY_66_APPLICATION_ID, getApplicationContext());
        //初始化小米广告
        MimoSdk.init(this, XIAOMI_APP_ID, "fake_app_key", "fake_app_token");
        try {
            PackageInfo mPackageInfo=this.getPackageManager().getPackageInfo(this.getPackageName(),0);
            CURRENT_VERSION=mPackageInfo.versionName;
            Utils.CURREN_VERSION_CODE=mPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initAndFix();

    }
    private void  initAndFix(){
        mPatchManager = new PatchManager(this);
        mPatchManager.init(CURRENT_VERSION);//current version
        mPatchManager.loadPatch();

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
