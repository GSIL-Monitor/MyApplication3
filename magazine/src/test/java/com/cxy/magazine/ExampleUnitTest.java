package com.cxy.magazine;

import com.cxy.magazine.util.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void subStr(){
        String str="2699312.html";
        String[] array=str.split(".html");
        for (String a : array){
            System.out.println(a);
        }

    }
    @Test
    public void creatRandomStr(){
        String str=Utils.createRandomCharData(5);
        System.out.println(str);
    }
}