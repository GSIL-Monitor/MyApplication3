package Util;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by cxy on 2017/4/3.
 */

public class OkHttpUtil {
    public static final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

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
       // System.out.println(visitUrl);

        Request request = new Request.Builder().url(visitUrl.toString()).build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }

    /**
     *post方法
     * @param url
     * @param data，要发送给服务器的数据
     * @return
     * @throws IOException
     */
    public static String postData(String url, String data) throws IOException {
        RequestBody body = new FormBody.Builder().add("data", data).build();

        Request request = new Request.Builder()
                          .url(url)
                          .post(body)
                          .build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }
    public static String postJson(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 上传文件和其他数据
     * @param url  服务器地址
     * @param filePath 要上传的文件路径
     * @param mediaType 文件类型 如：application/json、image/jpeg、audio/mp4、audio/mpeg :MP3 或者MPEG音频
     * @param jsonData 携带的json数据
     * @return
     * @throws IOException
     */
    public static String postFileDate(String url,String filePath,String mediaType,String jsonData) throws IOException {
        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
       /* // 这里演示添加用户ID
        //builder.addFormDataPart("userId", "20160519142605");
        for (Map.Entry<String,String> entry:params.entrySet()){   //遍历参数map

            builder.addFormDataPart(entry.getKey(),entry.getValue());

        }*/
        builder.addFormDataPart("file", filePath, RequestBody.create(MediaType.parse(mediaType), new File(filePath))).addFormDataPart("jsonData",jsonData);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String resultValue = response.body().string();
           // Log.d(TAG, "响应体 " + resultValue);
            return resultValue;
        }
        return result;
    }

    /**
     * 上传文件
     * @param url
     * @param filePaths 文件路径数组
     * @param mediaType
     * @return
     */
    public static String postFile(String url, List<String> filePaths, String mediaType) throws IOException{
        String result="error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String filePath : filePaths){
            builder.addFormDataPart("files", filePath, RequestBody.create(MediaType.parse(mediaType), new File(filePath)));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String resultValue = response.body().string();
            // Log.d(TAG, "响应体 " + resultValue);
            return resultValue;
        }
        return result;
    }


    public static void main(String[] args){
     /*   String url="http://api.avatardata.cn/TangShiSongCi/Search";
        Map params=new HashMap<String,String>();
        params.put("key","9b42454896f54202be3767fd55930654");
        params.put("keyword","中");*/

        try {
            String data=get(Util.SERVER_URL);
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
