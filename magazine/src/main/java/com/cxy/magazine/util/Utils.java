package com.cxy.magazine.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cxy.magazine.activity.LoginActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cxy on 2018/1/3.
 */

public class Utils {

    public static  final  String SERVER_URL="http://192.168.1.116:8081/questionnaire/"; //http://192.168.1.116:8081/questionnaire/queryQuestionnaire //http://192.168.1.159:8080/QuestionSurvey/test
    private static final String TAG = "SDK_Sample.Util";
    public static String TencentAppId="101457752";    //QQ登录AppId
    public static final String KEY_SHA = "SHA";
 //   private static Dialog mProgressDialog;
    private static Toast mToast;
    public static Integer CURREN_VERSION_CODE=0;
    private  static  QMUITipDialog tipDialog;


    public static final void showResultDialog(Context context, String msg, String title) {
        if(msg == null) return;
        QMUIDialog.MessageDialogBuilder messageDialogBuilder=new QMUIDialog.MessageDialogBuilder(context);
        QMUIDialog dialog;
        messageDialogBuilder.setMessage(msg);
        messageDialogBuilder.setTitle(title);
       // messageDialogBuilder.setLeftAction("确定",null);
        messageDialogBuilder.addAction("知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        dialog= messageDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public static final void showTipDialog(Context context, String message,int iconType) {
        dismissDialog();

        if (TextUtils.isEmpty(message)) {
            message = "正在加载...";
        }
        tipDialog=new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(message).create();
        tipDialog.show();
        if (iconType!=QMUITipDialog.Builder.ICON_TYPE_LOADING){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        tipDialog.dismiss();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }



    }

    public static void showConfirmCancelDialog(Context context,
                                               String title, String message,
                                               final QMUIDialogAction.ActionListener listener) {
        QMUIDialog dialog;

        QMUIDialog.MessageDialogBuilder messageDialogBuilder=new QMUIDialog.MessageDialogBuilder(context);
        messageDialogBuilder.setMessage(message);
        messageDialogBuilder.setTitle(title);
        messageDialogBuilder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        messageDialogBuilder.addAction("确认", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                listener.onClick(dialog,index);
                dialog.dismiss();
            }
        });



        dialog = messageDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


      /*  AlertDialog dlg = new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton("确认", posListener)
                .setNegativeButton("取消", null).create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        return dlg;*/
    //  return dialog;
    }

    public static final void dismissDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
            tipDialog = null;
        }
    }

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     * @param logLevel
     *            填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public static final void toastMessage(final Activity activity,
                                          final String message, String logLevel) {
        if ("w".equals(logLevel)) {
            Log.w("sdkDemo", message);
        } else if ("e".equals(logLevel)) {
            Log.e("sdkDemo", message);
        } else {
            Log.d("sdkDemo", message);
        }
        if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
        }
        mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        mToast.show();

    }

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     *            填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public static final void toastMessage(final Activity activity,
                                          final String message) {
        toastMessage(activity, message, null);
    }

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) {
      //  Log.v(TAG, "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Log.v(TAG, "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "getbitmap bmp fail---");
            bitmap = null;
        }
        return bitmap;
    }
    public static boolean checkNetworkState(Activity activity){
        boolean networkSate=NetWorkUtils.isNetworkConnected(activity);
        if (!networkSate){
            Utils.toastMessage(activity,"网络连接不可用，请检查网络状态");
        }

        return networkSate;
    }
    /*
     * 返回长度为strLength的随机数，在前面补0
    */
    public static String getFixLenthString(int strLength) {

        Random rm = new Random();

        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);

        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
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
     * 校验邮箱
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email){
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return  matcher.matches();

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


    public static String [] returnImageUrlsFromHtml(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
      //  String htmlCode = returnExampleHtml();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }
        if (imageSrcList == null || imageSrcList.size() == 0) {
            Log.e("imageSrcList","资讯中未匹配到图片链接");
            return null;
        }
        return imageSrcList.toArray(new String[imageSrcList.size()]);
    }


}
