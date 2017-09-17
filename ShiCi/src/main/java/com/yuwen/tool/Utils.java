package com.yuwen.tool;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

/**
 * Created by cxy on 2017/8/19.
 */

public class Utils {
    public  static final String BmobApplicationId="af1195b5462c886be8636a6845ba773a";
    public static String TencentAppId="1106093430";



    public static  boolean isEmpty(String s){
        if(s!=null&&s.length()>0){
            return false;
        }else{
            return true;
        }

    }



}
