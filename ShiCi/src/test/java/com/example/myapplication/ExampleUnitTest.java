package com.example.myapplication;

import com.yuwen.tool.Email;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.yuwen.tool.OkHttpUtil.get;
import static org.junit.Assert.assertTrue;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
   /* @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }*/
    @Test
    public void postEmail(){
        //assertEquals(true,Email.postEmail("第一封电子邮件"));
        assertTrue(Email.postEmail("第一封电子邮件"));
    }
    @Test
    public void encrypt(){
        //assertEquals(true,Email.postEmail("第一封电子邮件"));
        Utils.encryptBySHA("123456");
    }

    @Test
    public void okHttpGetWithParams(){
        String url="http://api.avatardata.cn/TangShiSongCi/Search";
        Map params=new HashMap<String,String>();
        params.put("key","9b42454896f54202be3767fd55930654");
        params.put("keyword","中");
        params.put("page",3);
        try {
            String data=get(url,params);
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}