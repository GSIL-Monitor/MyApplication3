package com.example.questionsurvey;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import Util.OkHttpUtil;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    final  String SERVER_URL="http://localhost:8080/QuestionSurvey/";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.questionsurvey", appContext.getPackageName());
    }

    @Test
    public void postJson(){
        try {
          /*  JSONObject jsonObj = new JSONObject();
            jsonObj.put("username", "张三");
            jsonObj.put("password", "123456");
            OkHttpUtil.post(SERVER_URL,jsonObj.toString());*/
          OkHttpUtil.get(SERVER_URL+"test");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void  postFile(){


    }

}
