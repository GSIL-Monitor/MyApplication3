package com.example.myapplication;

import com.myapp.tool.Email;

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
}