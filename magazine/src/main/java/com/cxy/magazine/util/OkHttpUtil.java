package com.cxy.magazine.util;
import android.support.annotation.Nullable;

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
   /* Authenticator authenticator=new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            PasswordAuthentication authentication = new PasswordAuthentication(null, "cxycxycxy".toCharArray());
            return authentication;
        }
    };*/
    public static final OkHttpClient client2 = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("47.88.54.113", 8081)))
            .proxyAuthenticator(new okhttp3.Authenticator() {
                @Nullable
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    String credential = Credentials.basic(null, "cxycxycxy");
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            })
            .build();
    //

    /**8081
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()!=200){
            response.close();
            throw new Exception("网络连接失败，StatusCode="+response.code());
        }

        String data=response.body().string();
        response.close();
        return data;

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
