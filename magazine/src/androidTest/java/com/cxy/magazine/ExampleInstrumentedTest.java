package com.cxy.magazine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cxy.magazine.bmobBean.MsgNotification;
import com.cxy.magazine.bmobBean.User;

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
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String BmobApplicationId ="be69c91d46af21288d5b855ee9fe158e";


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.cxy.magazine", appContext.getPackageName());
    }


    @Before
    public  void initPay66(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        //初始化Bmob
        Bmob.initialize(appContext,BmobApplicationId);
        System.out.println("init");
    }
    @Test
    public void insertMessages(){
        System.out.println("查询用户");

        BmobQuery<User> userQuery=new BmobQuery<User>();
        userQuery.setLimit(1000);

        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                System.out.println("查询成功");
                if (e==null){
                    List<BmobObject> msgList=new ArrayList<BmobObject>();
                    for (User user : list){
                        //发送消息
                        MsgNotification msg=new MsgNotification();
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
                            if(e==null){
                                for(int i=0;i<list.size();i++){
                                    BatchResult result = list.get(i);
                                    BmobException ex =result.getError();
                                    if(ex==null){
                                        Log.i("bmob","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                    }else{
                                        Log.i("bmob","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                    }
                                }
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });


                }
            }
        });
    }  //
}
