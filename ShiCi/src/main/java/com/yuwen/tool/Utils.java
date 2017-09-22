package com.yuwen.tool;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by cxy on 2017/8/19.
 */

public class Utils {
    public  static final String BmobApplicationId="af1195b5462c886be8636a6845ba773a";
    public static String TencentAppId="1106093430";
    public static final String KEY_SHA = "SHA";



    public static  boolean isEmpty(String s){
        if(s!=null&&s.length()>0){
            return false;
        }else{
            return true;
        }

    }


    /**
     * 加密数据
     * @param inputStr 加密前的数据
     * @return
     */
    public static  String  encryptBySHA(String inputStr)
    {
        BigInteger sha =null;
        System.out.println("=======加密前的数据:"+inputStr);
        byte[] inputData = inputStr.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(inputData);
            sha = new BigInteger(messageDigest.digest());
            System.out.println("SHA加密后:" + sha.toString(32));
        } catch (Exception e) {e.printStackTrace();}
        return sha.toString(32);
    }



}
