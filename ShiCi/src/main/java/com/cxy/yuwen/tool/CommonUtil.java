package com.cxy.yuwen.tool;



import android.app.Activity;
import android.content.Context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * Created by cxy on 2017/8/19.
 */

public class CommonUtil {
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


    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 验证密码
     * @param password
     * @return 校验通过返回true，否则返回false
     */
   public static boolean isPassword(String password){
       //密码正则表达式 6-20 位，字母、数字
       String regStr = "^[a-zA-Z0-9]{6,20}$";
       return Pattern.matches(regStr, password);


   }
    public static void checkNetworkState(Activity activity){
        boolean networkSate=NetWorkUtils.isNetworkConnected(activity);
        if (!networkSate){
            Util.toastMessage(activity,"网络连接不可用，请检查网络状态");
        }
    }


}
