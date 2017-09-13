package com.kevin.searchPeople.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kevin.searchPeople.util.Constant.TAG;

/**
 * Created by cxy on 2017/4/3.
 */

public class OkHttpUtil {
    public static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     *post方法
     * @param url
     * @param json，要发送给服务器的json数据
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        FormBody body = new FormBody.Builder().add("data", json).build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 上传文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String post(String filePath) throws IOException {
        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID
//        builder.addFormDataPart("userId", "20160519142605");
        builder.addFormDataPart("image", filePath,
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath)));

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/uploadImage")
                .post(requestBody)
                .build();

        Log.d(TAG, "请求地址 " + Constant.BASE_URL + "/uploadImage");
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String resultValue = response.body().string();
            Log.d(TAG, "响应体 " + resultValue);
            return resultValue;
        }
        return result;
    }
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

}
