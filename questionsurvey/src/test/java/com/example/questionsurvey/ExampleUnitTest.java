package com.example.questionsurvey;

import android.provider.ContactsContract;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Util.OkHttpUtil;
import Util.Util;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {

        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void getRandom()
    {
        int i=0;
        while (i < 20) {
            System.out.println(Util.getFixLenthString(6));
            i++;
        }
    }

    @Test
    public void testDate(){
        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(date)+Util.getFixLenthString(3));
    }

}