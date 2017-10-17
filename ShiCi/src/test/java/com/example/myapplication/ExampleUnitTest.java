package com.example.myapplication;

import com.yuwen.tool.Email;
import com.yuwen.tool.CommonUtil;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        CommonUtil.encryptBySHA("123456");
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

    @Test
    public void  testList(){

      List list=new ArrayList();
        for (int i=0;i<5;i++){
            list.add(i);
        }
        list.add(3,"广告");
       System.out.println(list);
    }

    @Test
    public void testDate(){
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(sdf.parse("2017-10-05"));
            System.out.println(cal.getTime().getTime());

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(sdf.parse((sdf.format(new Date()))));
            // cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+6);



            System.out.println( cal2.after(cal));
        } catch (ParseException e) {
            e.printStackTrace();
        }




    }
    @Test
    public void testSort() {
        Integer[] data = {5, 4, 6, 3, 29, 25, 16};

        List<Integer> numList = Arrays.asList(data);

        Collections.sort(numList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println(numList);

    }
}