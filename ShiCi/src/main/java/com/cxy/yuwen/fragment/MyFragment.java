package com.cxy.yuwen.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.yuwen.MyApplication;
import com.cxy.yuwen.activity.CollectActivity;
import com.cxy.yuwen.activity.FeedbackActivity;
import com.cxy.yuwen.activity.LoginActivity;
import com.cxy.yuwen.activity.MessageActivity;
import com.cxy.yuwen.activity.SettingInfomationActivity;
import com.cxy.yuwen.bmobBean.MsgNotification;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.Utils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;


public class MyFragment extends Fragment implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    public  static final String appUrl="http://a.app.qq.com/o/simple.jsp?pkgname=com.cxy.yuwen";
    User user;
    private NavigationView navigationView;
    private TextView tvLogin;
    private View layoutView;
    private ImageView headImageView;
    private Bitmap headImage;
    private Drawable defaultHeadImage;
    private static  final int IMAGE_LOAD_FINISHED=100;
    private ACache aCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment_my, container, false);
        tvLogin = (TextView) layoutView.findViewById(R.id.tv_login);
        headImageView=(ImageView)layoutView.findViewById(R.id.userImage);
        navigationView = (NavigationView) layoutView.findViewById(R.id.nav_view);

        aCache=ACache.get(this.getContext());

        //获取默认用户头像
        Resources resources = this.getResources();
        defaultHeadImage = resources.getDrawable(R.drawable.people);


       // setUserInfo();
        tvLogin.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        Log.i("fragment","----------onCreateView()");
        return layoutView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  ActionBar actionBar=((AppCompatActivity) getActivity()).getSupportActionBar();
      //  actionBar.show();
        Log.i("fragment","----------onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserInfo();
        setMessgae();

    }

    @Override
    public void onResume() {
        super.onResume();



    }

    public void setUserInfo(){
        user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
        if (user!=null){
            tvLogin.setText(user.getUsername());

            //获取用户头像
            if (!Utils.isEmpty(user.getHeadImageUrl())){
              //  headImage=aCache.getAsBitmap("headImageBitmap");  //从缓存中获取用户头像
                    Thread thread=new GetImageThread();
                    thread.start();



            }

        }else{
             tvLogin.setText("点击登录学习账号");
             headImageView.setImageDrawable(defaultHeadImage);
        }

    }

    class GetImageThread extends  Thread{
        @Override
        public void run() {

            headImage = Utils.getbitmap(user.getHeadImageUrl());
            if (headImage!=null){
                handler.sendEmptyMessage(IMAGE_LOAD_FINISHED);
            }
        }
    }




    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case IMAGE_LOAD_FINISHED:
                    headImageView.setImageBitmap(headImage);
                  //  aCache.put("headImageBitmap",headImage);
                    break;
            }

        }

    };



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_login) {
            if (user != null) {  //用户详情
                Intent intent = new Intent(getActivity(), SettingInfomationActivity.class);
                startActivity(intent);
            } else {   //跳转到注册界面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.collect) {
            if (user!=null){
                Intent intent=new Intent(getActivity(),CollectActivity.class);
                startActivity(intent);
            }else{
                Utils.showConfirmCancelDialog(getActivity(), "提示", "请先登录！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent1);
                    }
                });
            }


        }
        if (id == R.id.feedback) {
            // Toast.makeText(MainActivity.this, "你点击了反馈", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),FeedbackActivity.class);
            startActivity(intent);

        }
        if(id==R.id.seeWxapp){
            //TODO:跳转小程序
            String appId = "wx3d2d930bc45c3cfc"; // 填应用AppId
            IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), appId);

            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            req.userName = "gh_d56dc58bc19d"; // 填小程序原始id    贼坑
            req.path = "pages/index/index";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
            boolean result=api.sendReq(req);
            Log.i("com.cxy.yuwen","跳转结果"+result);


        }

        if(id==R.id.share){

            Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "语文助手这个App真不错，快来下载\n"+appUrl);
            sendIntent.setType("text/plain");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));
        }

        if (id==R.id.exit){   //退出
          //  SpotManager.getInstance( MyApplication.getInstance()).onAppExit();
            MyApplication.getInstance().exit();
        }

      /*  if (id==R.id.member_recharge){  //会员充值
            Intent intent=new Intent(getActivity(),MemberActivity.class);
            startActivity(intent);
        }
        if(id==R.id.invite){

           *//* Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "语文助手这个App真不错，快来下载\n"+appUrl);
            sendIntent.setType("text/plain");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));*//*
            if (user!=null){
                Intent intent=new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }else{   //跳转到登陆页
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
            }

        }*/

        if (id==R.id.messageNotification){
            if (user!=null){

                Intent intent=new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }else{   //跳转到登陆页
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
            }
        }

      /*  if(id==R.id.sendMsg){
            sendMsg();
        }
*/




        return true;
    }

    public void  sendMsg(){
            BmobQuery<User> userQuery = new BmobQuery<User>();
            int skip=1600;
            userQuery.setSkip(skip).setLimit(500).order("-updatedAt").findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {

                    if (e == null) {
                        System.out.println("查询成功"+list.size());
                        List<BmobObject> msgList = new ArrayList<BmobObject>();
                        for (User user : list) {
                            //发送消息
                            MsgNotification msg = new MsgNotification();
                            msg.setUser(user);
                            msg.setTitle("小程序上线通知");
                            msg.setDetail("语文助手小程序版本正式上线，小程序名称“语文助手Lite”。\n小程序版本界面更加简洁，功能更加强大，其中我们重点优化了诗词查询功能。“诗词查询”全新升级为“古文查询”，从此不仅能查询诗词，所有的文言文一网打尽。 古文查询支持对标题、作者、正文的模糊搜索。举例来说，你输入苏轼，点击古文搜索，会搜索出标题中包含“苏轼”、作者为“苏轼”、正文包含“苏轼”的所有诗词和文章。\n 说的再多不如大家亲自体验，赶紧前往微信小程序搜索“语文助手Lite”，亲自体验吧。（再次强调，名称为“语文助手Lite”，搜的时候，名字一定要输全，否则可能搜不到）");
                            msg.setRead(false);

                            msgList.add(msg);

                        }
                        for (int i=0;i<10;i++){
                            List<BmobObject> msgList1 = msgList.subList(i*50,i*50+50);
                            //批量插入
                            new BmobBatch().insertBatch(msgList1).doBatch(new QueryListListener<BatchResult>() {
                                @Override
                                public void done(List<BatchResult> list, BmobException e) {
                                    if (e == null) {
                                        for (int i = 0; i < list.size(); i++) {
                                            BatchResult result = list.get(i);
                                            BmobException ex = result.getError();
                                            if (ex == null) {
                                                Log.i("bmob", "第" + i + "个数据添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                                            } else {
                                                Log.i("bmob", "第" + i + "个数据添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                                            }
                                        }
                                    } else {
                                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                }
                            });    //end insert
                        }



                    }
                }

            });

        }



    public void setMessgae() {
        final LinearLayout msgLayout = (LinearLayout) navigationView.getMenu().findItem(R.id.messageNotification).getActionView();
        //  final TextView msg = (TextView) msgLayout.findViewById(R.id.msg);
        final  ImageView msgImg=(ImageView)msgLayout.findViewById(R.id.msg_notify_img);

        if (user != null) {
            BmobQuery<MsgNotification> msgQuery = new BmobQuery<>();
            msgQuery.addWhereEqualTo("user", user);
            msgQuery.findObjects(new FindListener<MsgNotification>() {
                @Override
                public void done(List<MsgNotification> list, BmobException e) {
                    if (e==null && list!=null) {
                        boolean hasNotRead=false;
                        if (list.size() > 0) {
                            for (MsgNotification msg : list) {
                                if (!msg.getRead()) {
                                    hasNotRead=true;
                                    break;
                                }
                            }

                        }
                        if (hasNotRead){
                            msgImg.setVisibility(View.VISIBLE);
                        }else{
                            msgImg.setVisibility(View.GONE);
                        }
                    }else{

                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        Log.i("bmob",e.toString());
                    }
                }
            });
        }else{   //没有登陆
            msgImg.setVisibility(View.GONE);
        }
    }
}
