package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ListView listView;
    private Button button;
    private String text = "";
    private List<Article> list=new ArrayList<Article>();
    private Adapter adapter;

    public static String httpUrl= "http://api.avatardata.cn/TangShiSongCi/Search?key=2cd12345f6b14541a187659621653372";
   // public  String httpArg = "keyWord=";

    public static String httpUrl1 = "http://api.avatardata.cn/TangShiSongCi/LookUp?key=2cd12345f6b14541a187659621653372";
    public static String httpArg1 = "id=deb3f7cd-4667-4602-bc04-2134c6a98954";
    private static final int MSG_NEWS_LOADED = 100;   //指示Rss新闻数据已获取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.tv);
        listView = (ListView) findViewById(R.id.lv);
        button = (Button) findViewById(R.id.btn);
        adapter=new Adapter(this,list);
        listView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = textView.getText().toString();
                if (text == null || text.length() <= 0) {

                    new AlertDialog.Builder(MainActivity.this).setMessage("请输入诗词名或作者名!").setPositiveButton("确定", null).create().show();

                } else {

                    Connection connection=new Connection();
                    connection.start();

                }

            }
        });



    }


    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEWS_LOADED:

                    //更新ListView显示
                    adapter.notifyDataSetChanged();

                    break;
            }
        }
    };




    public class Connection extends Thread {
        @Override
        public void run() {
            Log.i("info","新线程启动");
           String httpArg="keyWord="+text;
            String jsonString=request(httpUrl,httpArg);
            Log.i("info","获取的数据");
            Log.i("info",jsonString);
            System.out.println(jsonString);
            try {
               // getData(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
          //  mUIHandler.sendEmptyMessage(MSG_NEWS_LOADED);
        }
    }

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer("数据");
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(" http://api.avatardata.cn/TangShiSongCi/Search?key=2cd12345f6b14541a187659621653372&keyWord=秋兴");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
           //  connection.setRequestProperty("apikey", "0c3a92793aec19894d356d433b9b2622");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
                System.out.println("数据不为空");
                System.out.println("shjuwei"+sbf.toString());
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("开始获取数据4556566");
        System.out.println(result);
        return result;
    }



    public  void getData(String jsonData) throws Exception{

        System.out.println(jsonData);
        list.clear();

        JSONObject jsonObject=new JSONObject(jsonData);
        int errorCode=jsonObject.getInt("error_code");
        if (errorCode==0) {
            JSONArray result=jsonObject.getJSONArray("result");
            for(int i=0;i<result.length();i++){
                JSONObject item=result.getJSONObject(i);
                String name=item.getString("name");
                String id=item.getString("id");
                System.out.println(id);
//                String requestdata=request(httpUrl1,"id="+id);
//                String content=getContent(requestdata);

                System.out.println(name);


                Article article=new Article();
                article.setId(id);
                article.setTitle(name);
                list.add(article);

            }
        }else{


        }

      //  return list;

    }


  /*  public static String getContent(String data) throws JSONException{
        String content="";
        JSONObject jsonObject=new JSONObject(data);
        int errorCode=jsonObject.getInt("error_code");
        if (errorCode==0) {
            JSONObject result=jsonObject.getJSONObject("result");
            String neirong=result.getString("neirong");
            content=neirong;
        }
        return content;
    }
*/

}
