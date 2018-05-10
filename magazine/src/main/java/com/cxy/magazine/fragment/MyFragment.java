package com.cxy.magazine.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cxy.magazine.activity.CollectActivity;
import com.cxy.magazine.activity.FeedbackActivity;
import com.cxy.magazine.activity.HaveBuyActivity;
import com.cxy.magazine.activity.InviteActivity;
import com.cxy.magazine.activity.MainActivity;
import com.cxy.magazine.activity.MessageActivity;
import com.cxy.magazine.bmobBean.MsgNotification;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.MyApplication;
import com.cxy.magazine.R;
import com.cxy.magazine.activity.LoginActivity;
import com.cxy.magazine.activity.MemberActivity;
import com.cxy.magazine.activity.SettingInfomationActivity;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MyFragment extends BaseFragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String appUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.cxy.yuwen";
    User user;
    private NavigationView navigationView;
    private TextView tvLogin;
    private View layoutView;
    private ImageView headImageView;
    private Bitmap headImage;
    private Drawable defaultHeadImage;
    private static final int IMAGE_LOAD_FINISHED = 100;
    //  private mAcache mAcache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment_my, container, false);
        tvLogin = (TextView) layoutView.findViewById(R.id.tv_login);
        headImageView = (ImageView) layoutView.findViewById(R.id.userImage);
        navigationView = (NavigationView) layoutView.findViewById(R.id.nav_view);


        //   mAcache=mAcache.get(this.getContext());

        //获取默认用户头像
        Resources resources = this.getResources();
        defaultHeadImage = resources.getDrawable(R.drawable.head_image);


        // setUserInfo();
        tvLogin.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);


        Log.i("fragment", "----------onCreateView()");
        return layoutView;
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
        Log.i("fragment", "----------onResume()");


    }

    public void setUserInfo() {
        user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
        if (user != null) {
            tvLogin.setText(user.getUsername());

            //获取用户头像
            if (!Utils.isEmpty(user.getHeadImageUrl())) {
               /* headImage = mAcache.getAsBitmap("headImageBitmap");  //从缓存中获取用户头像
                if (headImage != null) {
                    headImageView.setImageBitmap(headImage);
                } else {
                    Thread thread = new GetImageThread();
                    thread.start();
                }*/

                Glide.with(context)
                        .load(user.getHeadImageUrl())
                        .error(R.drawable.head_image)
                        .into(headImageView);


            }

        } else {
            tvLogin.setText("点击登录学习账号");
            headImageView.setImageDrawable(defaultHeadImage);
        }

    }

    class GetImageThread extends Thread {
        @Override
        public void run() {

            headImage = Utils.getbitmap(user.getHeadImageUrl());
            if (headImage != null) {
                handler.sendEmptyMessage(IMAGE_LOAD_FINISHED);
            }

        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case IMAGE_LOAD_FINISHED:
                    headImageView.setImageBitmap(headImage);
                    mAcache.put("headImageBitmap", headImage);
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

        //收藏
        if (id == R.id.collect) {
            if (user != null) {
                Intent intent = new Intent(getActivity(), CollectActivity.class);
                startActivity(intent);
            } else {

                        Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent1);

            }


        }
        //已购
        if (id == R.id.haveBuy) {
            if (user != null) {
                Intent intent = new Intent(getActivity(), HaveBuyActivity.class);
                startActivity(intent);
            } else {
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
            }

        }
        if (id == R.id.feedback) {
            //  Toast.makeText(getActivity(), "你点击了反馈", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intent);

        }

        if (id == R.id.invite) {

           /* Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "语文助手这个App真不错，快来下载\n"+appUrl);
            sendIntent.setType("text/plain");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));*/
            if (user != null) {
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            } else {   //跳转到登陆页
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
            }

        }

        if (id == R.id.exit) {   //退出

            MyApplication.getInstance().exit();
        }

        if (id == R.id.member_recharge) {  //会员充值
            Intent intent = new Intent(getActivity(), MemberActivity.class);
            startActivity(intent);
        }
        if (id == R.id.messageNotification) {
            if (user != null) {

                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            } else {   //跳转到登陆页
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
            }
        }


        return true;
    }

    public void setMessgae() {
        final LinearLayout msgLayout = (LinearLayout) navigationView.getMenu().findItem(R.id.messageNotification).getActionView();
        //  final TextView msg = (TextView) msgLayout.findViewById(R.id.msg);
        final ImageView msgImg = (ImageView) msgLayout.findViewById(R.id.msg_notify_img);

        if (user != null) {
            BmobQuery<MsgNotification> msgQuery = new BmobQuery<>();
            msgQuery.addWhereEqualTo("user", user);
            msgQuery.findObjects(new FindListener<MsgNotification>() {
                @Override
                public void done(List<MsgNotification> list, BmobException e) {
                    if (e == null && list != null) {
                        boolean hasNotRead = false;
                        if (list.size() > 0) {
                            for (MsgNotification msg : list) {
                                if (!msg.getRead()) {
                                    hasNotRead = true;
                                    break;
                                }
                            }

                        }
                        if (hasNotRead) {
                            msgImg.setVisibility(View.VISIBLE);
                        } else {
                            msgImg.setVisibility(View.GONE);
                        }
                    } else {
                        Utils.toastMessage(getActivity(), e.getMessage());
                        Log.i("bmob", e.toString());
                    }
                }
            });
        } else {   //没有登陆
            msgImg.setVisibility(View.GONE);
        }
    }
}
