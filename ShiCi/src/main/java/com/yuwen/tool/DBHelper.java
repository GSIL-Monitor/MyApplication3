package com.yuwen.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cxy on 2017/9/2.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String db_name = "myDatabse";//自定义的数据库名；
    private static final int version =1;//版本号

    public DBHelper(Context context) {
        super(context, db_name, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String  sql ="create table collect_table(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +             //主键
                "type varchar(10)," +               //类型
                "name varchar(30),"+                //名字
                "content text," +            //内容
                "createTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))," +          //创建时间
                "updateTime TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))" +          //更新时间
                ")";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }



}
