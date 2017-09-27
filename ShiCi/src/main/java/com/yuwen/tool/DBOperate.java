package com.yuwen.tool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yuwen.entity.CollectBean;
import com.yuwen.MyApplication;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxy on 2017/9/2.
 */

public class DBOperate {
    DBHelper dbHelper=null;
    SQLiteDatabase sqlDatabase=null;

    //构造方法
    public DBOperate(DBHelper dbHelper){
        this.dbHelper=dbHelper;
        sqlDatabase=this.dbHelper.getWritableDatabase();
    }

    /**
     * 插入
     * @param type  类型   1:生字 2:词语 3:成语 4:诗词
     * @param content 内容
     */
    public void insert(String type,String name,String content){
        //先查询是否已存在该记录
        long count=queryByParams(type,name);
        if(count>0){
              return;
        }else{
            String sql = "insert into collect_table(type,name,content) values('"+type+"','"+name+"','"+content+"')";
            Log.i(MyApplication.TAG,sql);
            sqlDatabase.execSQL(sql);
            Log.i(MyApplication.TAG,"插入成功");
        }


    }

    /**
     * 查询所有数据
     * @return
     */
    public  List<CollectBean> query(){
        List<CollectBean>  list=new ArrayList<CollectBean>();
        //查询获得游标
        Cursor cursor = sqlDatabase.query ("collect_table",null,null,null,null,null,"createTime desc");
        //判断游标是否为空
        if (cursor==null||cursor.getCount()<=0){
            Log.i(MyApplication.TAG,"没有查询到相关数据");
        }
        while(cursor.moveToNext()) {

                 //获得ID
                int id = cursor.getInt(0);
                 //获得类型
                String type=cursor.getString(1);
                  //获得name
                String name=cursor.getString(2);

                String content=cursor.getString(3);
                String createTime=cursor.getString(4);
                String updateTime=cursor.getString(5);

                CollectBean  collectBean=new CollectBean(id,type,name,content,createTime,updateTime);
                list.add(collectBean);

                Log.i(MyApplication.TAG,collectBean.toString());

        }

        return list;
    }

    /**
     * 根据参数查询所有数据
     * @return
     */
    public  long queryByParams(String type, String name){
       // String[] columns=new  String[]{"count(*)"};
       // String selection="type=? and name=?";
        String[] selectionArgs = new  String[]{type,name};
        long count=0;
        //查询获得游标
        Cursor cursor = sqlDatabase.rawQuery("select count(*) from collect_table where type=? and name = ?",
                selectionArgs);
      //  Cursor cursor = sqlDatabase.query ("collect_table",columns,selection,selectionArgs,null,null,null);
        //判断游标是否为空
        if (cursor==null||cursor.getCount()<=0){
            Log.i(MyApplication.TAG,"没有查询到相关数据");
        }
        Log.i(MyApplication.TAG,cursor.toString());
        while(cursor.moveToNext()) {

            //获得count

            count = cursor.getLong(0);
            Log.i(MyApplication.TAG,"数量"+count+"");

        }

        return count;
    }

    public void deleteById(int id) {
        String sql = "delete from collect_table where id="+id;

        Log.i(MyApplication.TAG,sql);
        sqlDatabase.execSQL(sql);
        Log.i(MyApplication.TAG,"删除成功");

    }


}
