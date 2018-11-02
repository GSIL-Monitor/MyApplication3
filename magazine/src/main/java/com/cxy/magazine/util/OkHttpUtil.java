package com.cxy.magazine.util;
import android.support.annotation.Nullable;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


/**
 * Created by cxy on 2017/4/3.
 */

public class OkHttpUtil {
    public static final OkHttpClient client = new OkHttpClient.Builder()
                                                 .connectTimeout(30, TimeUnit.SECONDS)
                                                 .readTimeout(30, TimeUnit.SECONDS)
                                                 .writeTimeout(30,TimeUnit.SECONDS)
                                                 .build();
    private static int isConnected=0;
    private static String SERVER_URL="http://38.21.240.36:8081/gethtml?url=";
   /* Authenticator authenticator=new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            PasswordAuthentication authentication = new PasswordAuthentication(null, "cxycxycxy".toCharArray());
            return authentication;
        }
    };*/
    /*public static final OkHttpClient client2 = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("38.21.240.36", 3128)))
            .build();*/
    //

    /**8081
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws Exception {
        String httpUrl=url;
        if (isConnected==0){
            httpUrl=SERVER_URL+url;
        }else {
            httpUrl=url;
        }
        Request request = new Request.Builder()
                .url(httpUrl)
                .addHeader("Connection", "close")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()!=200){
            response.close();
            throw new Exception("网络连接失败，StatusCode="+response.code());
        }
        String data=null;
        if (httpUrl.equals(url)){
            data=response.body().string();
        }
        if (httpUrl.equals(SERVER_URL+url)){
            String result=response.body().string();
            JsonParser jsonParser=new JsonParser();
            JsonObject jsonObject=jsonParser.parse(result).getAsJsonObject();
            String errCode=jsonObject.get("errCode").getAsString();
            if (errCode.equals("0000")){
                data=jsonObject.get("data").getAsString();
            }else{
                throw new Exception("获取内容失败");
            }
        }


        response.close();
        return data;

    }

    /**
     * 检查域名的连接
     * @param domain
     */
    public static void checkConnected(String domain){

        Request request = new Request.Builder()
                .url(domain)
                .addHeader("Connection", "close")
                .build();
        Response response = null;
        try {
             response = client.newCall(request).execute();
             if (response.code()==200){
                isConnected=1;
             }else{
                isConnected=0;
             }
        } catch (IOException e) {
            e.printStackTrace();
           isConnected=0;

        }finally {
            if (response!=null){
                response.close();
            }

        }




    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String get(String url,Map<String,Object> params) throws IOException {

        StringBuilder visitUrl=new StringBuilder(url+"?");
        for (Map.Entry<String,Object> entry:params.entrySet()){   //遍历参数map
            visitUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");

        }
        System.out.println(visitUrl);
        Request request = new Request.Builder()
                .url(visitUrl.toString())
                .build();

        Response response = client.newCall(request).execute();
        String data=response.body().string();
        response.close();
        return data;

    }


    public static void main(String[] args){
        String url="http://api.avatardata.cn/TangShiSongCi/Search";
        Map params=new HashMap<String,String>();
        params.put("key","9b42454896f54202be3767fd55930654");
        params.put("keyword","中");

        try {
            String data=get(url,params);
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
