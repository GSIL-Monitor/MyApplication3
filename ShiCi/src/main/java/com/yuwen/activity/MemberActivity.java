package com.yuwen.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yuwen.bmobBean.User;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Util;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobUser;


public class MemberActivity extends AppCompatActivity implements View.OnClickListener{
    TextView memberName,memberInfo,payMoney;
    Button oneBtn,threeBtn,sixBtn,payBtn;
    User user;

    View contentView;
    RadioButton rbPay;
    Dialog  bottomDialog;
    double money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        MyApplication.getInstance().addActivity(this);
        initView();

    }

    private void initView(){
        ActionBar bar= getSupportActionBar();
        bar.setTitle("会员中心");

        memberName=(TextView)findViewById(R.id.tvMemberName);
        memberInfo=(TextView)findViewById(R.id.tvMemberInfo);

        oneBtn=(Button)findViewById(R.id.btn1Month);
        threeBtn=(Button)findViewById(R.id.btn3Month);
        sixBtn=(Button)findViewById(R.id.btn6Month);

        contentView = LayoutInflater.from(this).inflate(R.layout.payment, null);
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setContentView(contentView);


        payMoney=(TextView)contentView.findViewById(R.id.tvMoney);
        rbPay=(RadioButton)contentView.findViewById(R.id.rbPay);
        payBtn=(Button)contentView.findViewById(R.id.btnPay);




        user= BmobUser.getCurrentUser(User.class);
        if (user!=null){
         //初始化会员信息
            memberName.setText(user.getUsername());

        }
        oneBtn.setOnClickListener(this);
        threeBtn.setOnClickListener(this);
        sixBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (user==null){   //未登录

            AlertDialog dlg = new AlertDialog.Builder(MemberActivity.this).setMessage("请先登录！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(MemberActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();

        }
        else if (view.getId()==R.id.btn1Month){
            payMoney.setText("￥8");
            money=8;
            showDialog();
        }
        else if (view.getId()==R.id.btn3Month){
            payMoney.setText("￥22");
            money=22;
            showDialog();
        }
        else if (view.getId()==R.id.btn6Month){
            payMoney.setText("￥42");
            money=42;
            showDialog();
        }
        else if (view.getId()==R.id.btnPay){
           // Util.toastMessage(MemberActivity.this,"支付宝支付");
            bottomDialog.dismiss();
            pay();
        }
    }

    public  void pay(){
        if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
            Toast.makeText(MemberActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Util.showProgressDialog(MemberActivity.this,"请稍等","正在获取订单...\nSDK版本号:" + BP.getPaySdkVersion());
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");   //com.bmob.app.sport
            intent.setComponent(cn);
            this.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }


        BP.pay("语文助手会员支付", "语文助手会员支付", money, true, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(MemberActivity.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
               // tv.append(name + "'s pay status is unknow\n\n");
                //hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(MemberActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
              //  tv.append(name + "'s pay status is success\n\n");
               // hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
               /* order.setText(orderId);
                tv.append(name + "'s orderid is " + orderId + "\n\n");*/
                Util.showProgressDialog(MemberActivity.this,null,"获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            MemberActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
//                    installBmobPayPlugin("bp.db");
                    //installApk("bp.db");
                } else {
                    Toast.makeText(MemberActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                            .show();


            }
        }

    });

    }

    /**
     * 检查某包名应用是否已经安装
     *
     * @param packageName 包名
     * @param browserUrl  如果没有应用市场，去官网下载
     * @return
     */
    private boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    Toast.makeText(MemberActivity.this,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    private void showDialog() {
       // bottomDialog.dismiss();


        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        bottomDialog.setCanceledOnTouchOutside(true);

        /*contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });*/

    }
}
