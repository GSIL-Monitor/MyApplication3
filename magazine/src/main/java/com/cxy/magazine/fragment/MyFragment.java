package com.cxy.magazine.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.cxy.magazine.BmobBean.User;
import com.cxy.magazine.MyApplication;
import com.cxy.magazine.R;
import com.cxy.magazine.activity.LoginActivity;
import com.cxy.magazine.activity.MemberActivity;
import com.cxy.magazine.activity.SettingInfomationActivity;
import com.cxy.magazine.util.ACache;
import com.cxy.magazine.util.Utils;

import cn.bmob.v3.BmobUser;


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
        Log.i("fragment","----------onStart()");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragment","----------onResume()");


    }

    public void setUserInfo(){
        user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
        if (user!=null){
            tvLogin.setText(user.getUsername());

            //获取用户头像
            if (!Utils.isEmpty(user.getHeadImageUrl())){
                headImage=aCache.getAsBitmap("headImageBitmap");  //从缓存中获取用户头像
                if (headImage!=null){
                    headImageView.setImageBitmap(headImage);
                }else{
                    Thread thread=new GetImageThread();
                    thread.start();
                }


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
            handler.sendEmptyMessage(IMAGE_LOAD_FINISHED);
        }
    }




    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case IMAGE_LOAD_FINISHED:
                    headImageView.setImageBitmap(headImage);
                    aCache.put("headImageBitmap",headImage);
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
               // Intent intent=new Intent(getActivity(),CollectActivity.class);
               // startActivity(intent);
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
           /* Intent intent=new Intent(getActivity(),FeedbackActivity.class);
            startActivity(intent);*/

        }

        if(id==R.id.share){

            Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "语文助手这个App真不错，快来下载\n"+appUrl);
            sendIntent.setType("text/plain");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));
        }

        if (id==R.id.exit){   //退出

            MyApplication.getInstance().exit();
        }

        if (id==R.id.member_recharge){  //会员充值
            Intent intent=new Intent(getActivity(),MemberActivity.class);
            startActivity(intent);
        }


        return true;
    }
}
