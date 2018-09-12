package com.cxy.magazine;

import com.cxy.magazine.util.Utils;

import org.junit.Test;

import java.util.Random;

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
    @Test
    public void testIf(){
        int i=3;
        if (i>2){
            System.out.println("---");
            i+=2;
        }
        if (i>3) {
            System.out.println("+++++");
        }
    }

    @Test
    public void randomTest(){
        Random random=new Random();
        for (int i = 0; i <20 ; i++) {
            int r=random.nextInt(3);
            System.out.println("随机数："+r);
        }



    }
}
