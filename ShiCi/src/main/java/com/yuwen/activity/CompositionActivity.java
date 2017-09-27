package com.yuwen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompositionActivity extends BasicActivity
{

    private Spinner spinnerGrade,spinnerType,spinnerFontSum,spinnerLevel;
    private List<Map<String,Object>>  gradeList,typeList,fontSumList,levelList;
    private SimpleAdapter gradeAdapter,typeAdapter,fontSumAdapter,levelAdapter;
    private static String urlType="http://zuowen.api.juhe.cn/zuowen/typeList";
    private static String urlBase="http://zuowen.api.juhe.cn/zuowen/baseList";
    public static final String APPKEY_COMPOSITION ="cf7b0e43bbe17e82cc632163361ccfcf";  //作文
    private  final String TAg="zuowen";
    private Button btnConfirm;
    private Integer gradeId,typeId,wordId,level,page;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);
        MyApplication.getInstance().addActivity(this);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("作文大全");
        checkPermmion(this);

        spinnerGrade=(Spinner)findViewById(R.id.grade);
        spinnerType=(Spinner)findViewById(R.id.comTheme);
        spinnerFontSum=(Spinner)findViewById(R.id.fontNum);
        spinnerLevel=(Spinner)findViewById(R.id.comLevel);
        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        listView=(ListView)findViewById(R.id.comListview);

        Map<String,Object> gradeMap=new HashMap<String,Object>();
        gradeMap.put("name","年级");
        gradeMap.put("id",null);

        Map<String,Object> typeMap=new HashMap<String,Object>();
        typeMap.put("name","题材");
        typeMap.put("id",null);

        Map<String,Object> fontMap=new HashMap<String,Object>();
        fontMap.put("name","字数");
        fontMap.put("id",null);

        Map<String,Object> levelMap=new HashMap<String,Object>();
        levelMap.put("name","等级");
        levelMap.put("id",null);

        gradeList=new ArrayList<Map<String,Object>>();
        typeList=new ArrayList<Map<String,Object>>();
        fontSumList=new ArrayList<Map<String,Object>>();
        levelList=new ArrayList<Map<String,Object>>();

        gradeList.add(gradeMap);
        typeList.add(typeMap);
        fontSumList.add(fontMap);
        levelList.add(levelMap);


        gradeAdapter=new SimpleAdapter(this,gradeList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        typeAdapter=new SimpleAdapter(this,typeList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        fontSumAdapter=new SimpleAdapter(this,fontSumList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        levelAdapter=new SimpleAdapter(this,levelList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});


        //设置样式
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //加载适配器
        spinnerGrade.setAdapter(gradeAdapter);
        spinnerType.setAdapter(typeAdapter);
        spinnerFontSum.setAdapter(fontSumAdapter);
        spinnerLevel.setAdapter(levelAdapter);

        spinnerGrade.setOnItemSelectedListener(itemSelectedListener);
        spinnerType.setOnItemSelectedListener(itemSelectedListener);
        spinnerFontSum.setOnItemSelectedListener(itemSelectedListener);
        spinnerLevel.setOnItemSelectedListener(itemSelectedListener);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final  Map mapBase=new HashMap<String,Object>();
                mapBase.put("key",APPKEY_COMPOSITION);
                if (gradeId != null) {
                    mapBase.put("gradeId",gradeId);
                }
                if (typeId!=null){
                    mapBase.put("typeId",typeId);

                }
                if (wordId!=null){
                    mapBase.put("wordId",wordId);
                }
                if (level!=null){
                    mapBase.put("level",level);
                }

                Thread baseThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        {

                            try {
                                //发送请求
                                String baseData= NetworkConnection.net(urlBase,mapBase,"GET");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
        });



        Thread thread=new Thread(runnable);
        thread.start();



    }


   Runnable runnable=new Runnable() {
       @Override
       public void run() {
           try {
               Map mapGrade=new HashMap<String,Object>();
               mapGrade.put("key",APPKEY_COMPOSITION);
               mapGrade.put("id",1);
               String gradeData= NetworkConnection.net(urlType,mapGrade,"GET");

               Map mapType=new HashMap<String,Object>();
               mapType.put("key",APPKEY_COMPOSITION);
               mapType.put("id",2);
               String typeData= NetworkConnection.net(urlType,mapType,"GET");

               Map mapFont=new HashMap<String,Object>();
               mapFont.put("key",APPKEY_COMPOSITION);
               mapFont.put("id",3);
               String fontData= NetworkConnection.net(urlType,mapFont,"GET");

               Map mapLevel=new HashMap<String,Object>();
               mapLevel.put("key",APPKEY_COMPOSITION);
               mapLevel.put("id",4);
               String levelData= NetworkConnection.net(urlType,mapLevel,"GET");

               resolveJson(gradeData,typeData,fontData,levelData);

           } catch (Exception e) {
               e.printStackTrace();
           }

       }
   };

//解析json数据
public void resolveJson(String gradeData,String typeData,String fontData, String levelData){
    try {
        //解析年级json
        JSONObject gradeJson=new JSONObject(gradeData);
        if (gradeJson.getInt("error_code")==0){   //请求成功
           // gradeList.clear() ;
            JSONArray resultArray=gradeJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                gradeList.add(map);

            }

            handler.sendEmptyMessage(100);
           // gradeAdapter.notifyDataSetChanged();

        }else{
           Log.i(TAG,gradeJson.get("error_code")+":"+gradeJson.get("reason"));
        }

        //解析题材json
        JSONObject typeJson=new JSONObject(typeData);
        if (typeJson.getInt("error_code")==0){   //请求成功
          //  typeList.clear() ;
            JSONArray resultArray=typeJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                typeList.add(map);

            }

            handler.sendEmptyMessage(101);
            // gradeAdapter.notifyDataSetChanged();

        }else{
            Log.i(TAG,typeJson.get("error_code")+":"+typeJson.get("reason"));
        }

        //解析字数json
        JSONObject fontJson=new JSONObject(fontData);
        if (fontJson.getInt("error_code")==0){   //请求成功
          //  fontSumList.clear() ;
            JSONArray resultArray=fontJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                fontSumList.add(map);

            }

            handler.sendEmptyMessage(102);
            // gradeAdapter.notifyDataSetChanged();

        }else{
            Log.i(TAG,fontJson.get("error_code")+":"+fontJson.get("reason"));
        }


        //解析等级json
        JSONObject levelJson=new JSONObject(levelData);
        if (levelJson.getInt("error_code")==0){   //请求成功
          //  levelList.clear() ;
            JSONArray resultArray=levelJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                levelList.add(map);

            }

            handler.sendEmptyMessage(103);
            // gradeAdapter.notifyDataSetChanged();

        }else{
            Log.i(TAG,fontJson.get("error_code")+":"+fontJson.get("reason"));
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


}


   Handler handler=new Handler() {
       @Override
       public void handleMessage(Message msg) {
          switch (msg.what){
              case 100:
                  gradeAdapter.notifyDataSetChanged();
                //  spinnerGrade.setAdapter(gradeAdapter);
                  break;
              case 101:
                  typeAdapter.notifyDataSetChanged();
                  break;
              case 102:
                  fontSumAdapter.notifyDataSetChanged();
                  break;
              case 103:
                  levelAdapter.notifyDataSetChanged();
                  break;


          }
       }
   };


    AdapterView.OnItemSelectedListener itemSelectedListener=new  AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId()==R.id.grade){
                  // Util.toastMessage(CompositionActivity.this,gradeList.get(i).get("name").toString());
                   gradeId=(Integer)gradeList.get(i).get("id");

               }
            if (adapterView.getId()==R.id.comTheme){
                //Util.toastMessage(CompositionActivity.this,typeList.get(i).get("name").toString());
                typeId=(Integer)typeList.get(i).get("id");
            }
            if (adapterView.getId()==R.id.fontNum){
                //Util.toastMessage(CompositionActivity.this,fontSumList.get(i).get("name").toString());
                wordId=(Integer)fontSumList.get(i).get("id");
            }

            if (adapterView.getId()==R.id.comLevel){
                //Util.toastMessage(CompositionActivity.this,levelList.get(i).get("name").toString());
                level=(Integer)levelList.get(i).get("id");
            }







        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

}
