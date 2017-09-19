package com.yuwen.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.ZiDianConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompositionActivity extends BasicActivity
{

    private Spinner spinnerGrade,spinnerType,spinnerFontSum,spinnerLevel;
    private List<String> gradeList,typeList,fontSumList,levelList;
    private ArrayAdapter<String> gradeAdapter,typeAdapter,fontSumAdapter,levelAdapter;
    private static String url="http://zuowen.api.juhe.cn/zuowen/typeList";
    public static final String APPKEY_COMPOSITION ="78a86fe8b36ca8daff509c33f0345c10";  //作文

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);
        MyApplication.getInstance().addActivity(this);
        checkPermmion(this);

        spinnerGrade=(Spinner)findViewById(R.id.grade);
        spinnerType=(Spinner)findViewById(R.id.comTheme);
        spinnerFontSum=(Spinner)findViewById(R.id.fontNum);
        spinnerLevel=(Spinner)findViewById(R.id.comLevel);


        //适配器
        gradeAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        typeAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        fontSumAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fontSumList);
        levelAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levelList);

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

    }


   Runnable runnable=new Runnable() {
       @Override
       public void run() {
           try {
               Map mapGrade=new HashMap();
               mapGrade.put("key",APPKEY_COMPOSITION);
               mapGrade.put("id",1);
               String gradeData=ZiDianConnection.net(url,mapGrade,"get");

               Map mapType=new HashMap();
               mapType.put("key",APPKEY_COMPOSITION);
               mapType.put("id",2);
               String typeData=ZiDianConnection.net(url,mapGrade,"get");

               Map mapFont=new HashMap();
               mapFont.put("key",APPKEY_COMPOSITION);
               mapFont.put("id",3);
               String fontData=ZiDianConnection.net(url,mapFont,"get");

               Map mapLevel=new HashMap();
               mapLevel.put("key",APPKEY_COMPOSITION);
               mapLevel.put("id",4);
               String levelData=ZiDianConnection.net(url,mapLevel,"get");

           } catch (Exception e) {
               e.printStackTrace();
           }

       }
   };
}
