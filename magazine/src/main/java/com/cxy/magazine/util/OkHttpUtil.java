package com.cxy.magazine.util;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by cxy on 2017/4/3.
 */

public class OkHttpUtil {
    public static final OkHttpClient client = new OkHttpClient();

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();

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
        return response.body().string();

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
