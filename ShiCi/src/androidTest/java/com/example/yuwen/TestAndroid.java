package com.example.yuwen;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cxy.yuwen.activity.FeedbackActivity;
import com.cxy.yuwen.bmobBean.MsgNotification;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.tool.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.bean.Init;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * Created by cxy on 2018/1/14.
 */
@RunWith(AndroidJUnit4.class)
public class TestAndroid {

    @Before
    public void initBmob() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        //初始化Bmob
        Bmob.initialize(appContext, Utils.BmobApplicationId, "bmob");
    }

    @Test
    public void sendMsg() {
        System.out.println("查询用户");

        BmobQuery<User> userQuery = new BmobQuery<User>();

        userQuery.setLimit(50).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

                if (e == null) {
                    System.out.println("查询成功"+list.size());
                    List<BmobObject> msgList = new ArrayList<BmobObject>();

                    for (User user : list) {
                        //发送消息
                        MsgNotification msg = new MsgNotification();
                        msg.setUser(user);
                        msg.setTitle("紧急通知");
                        msg.setDetail("近期有用户反映，充值成功之后，会员并未到账，对此我们深表歉意，开发人员正在紧张排查中。如果有用户再遇到此类问题，请及时联系我们，" +
                                "可以通过用户反馈中的渠道联系我们，也可以直接通过QQ号：1746569077联系。杂志天下向广大用户声明，我们绝不会坑用户一分钱，感谢大家的支持！");
                        msg.setRead(false);

                        msgList.add(msg);

                    }

                                   //批量插入
                    new BmobBatch().insertBatch(msgList).doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> list, BmobException e) {
                            if (e == null) {
                                for (int i = 0; i < list.size(); i++) {
                                    BatchResult result = list.get(i);
                                    BmobException ex = result.getError();
                                    if (ex == null) {
                                        Log.i("bmob", "第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                                    } else {
                                        Log.i("bmob", "第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                                    }
                                }
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });


                }
            }
        });


    }
}

