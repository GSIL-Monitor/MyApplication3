package com.example.myapplication;

import android.test.ActivityInstrumentationTestCase2;

import com.yuwen.activity.ZidianActivity;
import com.yuwen.tool.DBHelper;
import com.yuwen.tool.DBOperate;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<ZidianActivity> {

    private ZidianActivity AppTest;

    public ApplicationTest() {
        super(ZidianActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        AppTest = getActivity();
    }


    public void testquery(){
        DBHelper dbHelper=new DBHelper(AppTest);
        DBOperate dBOperate=new DBOperate(dbHelper);
        dBOperate.query();
    }
}