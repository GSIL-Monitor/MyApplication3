package com.cxy.yuwen.tool;



import android.app.Activity;
import android.content.Context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;
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
    public static boolean checkNetworkState(Activity activity){
        boolean networkSate=NetWorkUtils.isNetworkConnected(activity);
        if (!networkSate){
            Util.toastMessage(activity,"网络连接不可用，请检查网络状态");
        }

        return networkSate;
    }

    //根据指定长度生成字母和数字的随机数
    //0~9的ASCII为48~57
    //A~Z的ASCII为65~90
    //a~z的ASCII为97~122
    public static String createRandomCharData(int length)
    {
        StringBuilder sb=new StringBuilder();
        Random rand=new Random();//随机用以下三个随机生成器
        Random randdata=new Random();
        int data=0;
        for(int i=0;i<length;i++)
        {
            int index=rand.nextInt(3);
            //目的是随机选择生成数字，大小写字母
            switch(index)
            {
                case 0:
                    data=randdata.nextInt(10);//仅仅会生成0~9
                    sb.append(data);
                    break;
                case 1:
                    data=randdata.nextInt(26)+65;//保证只会产生65~90之间的整数
                    sb.append((char)data);
                    break;
                case 2:
                    data=randdata.nextInt(26)+97;//保证只会产生97~122之间的整数
                    sb.append((char)data);
                    break;
            }
        }
        String result=sb.toString();
        return result;

    }


}
