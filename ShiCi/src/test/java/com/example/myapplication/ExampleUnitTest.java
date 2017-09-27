package com.example.myapplication;

import com.yuwen.tool.Email;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import org.junit.Test;

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

}