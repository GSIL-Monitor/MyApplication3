package com.example.yuwen;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.cxy.yuwen.activity.FeedbackActivity;
import com.cxy.yuwen.activity.ZidianActivity;
import com.cxy.yuwen.tool.EmailUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by cxy on 2018/1/14.
 */
@RunWith(AndroidJUnit4.class)
public class TestAndroid extends ActivityInstrumentationTestCase2<FeedbackActivity> {
    public TestAndroid() {
        super(FeedbackActivity.class);
    }
    @Test
    public void sendEmail(){
        //EmailUtil.autoSendMail(getActivity(),"456");
    }
}
