package com.yuwen.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eagle.pay66.Pay66;
import com.eagle.pay66.listener.CommonListener;
import com.eagle.pay66.vo.OrderPreMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yuwen.bmobBean.Member;
import com.yuwen.bmobBean.MemberRecharge;
import com.yuwen.bmobBean.User;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.ResponseParam;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class MemberActivity extends AppCompatActivity implements View.OnClickListener{
    TextView memberName,memberInfo,payMoney;
    Button oneBtn,threeBtn,sixBtn,payBtn;
    User user;

    View contentView;
    RadioButton alipayBtn,wxpayBtn;
    Dialog  bottomDialog;
    double money;
    private static final String TAG_CREATE_ORDER = "createOrder";
    private static final String TAG_PAY_ORDER = "payOrder";
    private static final double MONEY_ONE_MONTH=8,MONEY_THREE_MONTH=22,MONEY_SIX_MONTH=42;
    int count=0;

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
        alipayBtn=(RadioButton)contentView.findViewById(R.id.aliPay);   //支付宝支付
       // wxpayBtn=(RadioButton)contentView.findViewById(R.id.wxPay);    //微信支付
        payBtn=(Button)contentView.findViewById(R.id.btnPay);






        oneBtn.setOnClickListener(this);
        threeBtn.setOnClickListener(this);
        sixBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkMemberState();
    }

    public void checkMemberState(){
        user= BmobUser.getCurrentUser(User.class);
        if (user != null) {
            memberName.setText(user.getUsername());

            BmobQuery<Member> memberQuery = new BmobQuery<Member>();
            memberQuery.addWhereEqualTo("user", user);

            memberQuery.findObjects(new FindListener<Member>() {//查询会员信息
                @Override
                public void done(List<Member> list, BmobException e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (e == null) {
                        if (list.size() <= 0) {  //未开通会员
                            memberInfo.setText("未开通会员");
                        } else if (list.size() == 1) {
                            Member queryMember = list.get(0);
                            String finishTime = queryMember.getFinishTime();
                            Calendar nowCal = Calendar.getInstance();  //当前时间

                            try {
                                nowCal.setTime(sdf.parse((sdf.format(new Date()))));
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(sdf.parse(finishTime));
                                int value = cal2.compareTo(nowCal);
                                if (value == -1) {   //已经过期

                                    memberInfo.setText("会员已过期");

                                } else {  //还没过期
                                    memberInfo.setText("会员有效期：" + queryMember.getStartTime() + "至" + finishTime);
                                    oneBtn.setText("续费");
                                    threeBtn.setText("续费");
                                    sixBtn.setText("续费");
                                }

                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }


                        }
                    } else {
                        Util.toastMessage(MemberActivity.this, "出错了");
                    }
                }
            });

        }


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
            payMoney.setText("￥"+MONEY_ONE_MONTH);
            money=MONEY_ONE_MONTH;
            showDialog();
        }
        else if (view.getId()==R.id.btn3Month){
            payMoney.setText("￥"+MONEY_THREE_MONTH);
            money=MONEY_THREE_MONTH;
            showDialog();
        }
        else if (view.getId()==R.id.btn6Month){
            payMoney.setText("￥"+MONEY_SIX_MONTH);
            money=MONEY_SIX_MONTH;
            showDialog();
        }
        else if (view.getId()==R.id.btnPay){
           // Util.toastMessage(MemberActivity.this,"支付宝支付");
            bottomDialog.dismiss();
           // pay();  //Bmob支付
            createOrder();
        }
    }

    /**
     * Bmob支付
     */
    /*public  void pay(){
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
               *//* order.setText(orderId);
                tv.append(name + "'s orderid is " + orderId + "\n\n");*//*
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
*/
    public void createOrder(){
        count=0;
        String message="";

       if (money==MONEY_ONE_MONTH){
           message="语文助手1个月会员支付";
       }else if (money==MONEY_THREE_MONTH){
           message="语文助手3个月会员支付";
       }else  if (money==MONEY_SIX_MONTH){
           message="语文助手6个月会员支付";
       }else{
           message="语文助手会员支付";
       }



          //(int)(money*100)
        Pay66.createOrder(10, message, message, new CommonListener() {
            @Override
            public void onStart() {
                Log.d(TAG_CREATE_ORDER, "---onStart");
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG_CREATE_ORDER, "---onError");
                Log.d(TAG_CREATE_ORDER, "--onError--code=" + code + ",msg=" + msg);
                Util.showResultDialog(MemberActivity.this,msg,"创建订单失败");
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG_CREATE_ORDER, "---onSuccess");
                Log.d(TAG_CREATE_ORDER, "---onSuccess--response=" + response);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                ResponseParam<OrderPreMessage> responseParam = gson.fromJson(response, new TypeToken<ResponseParam<OrderPreMessage>>() {
                }.getType());
                if ( responseParam!=null && responseParam.getData() !=null){
                    Log.d(TAG_CREATE_ORDER, "---onSuccess--orderId=" + responseParam.getData().getOrderId());
                    //防止重复提交订单
                    if (count<1){
                        pay_66(responseParam.getData().getOrderId(), responseParam.getData().getConsume()); //进行支付
                       // count++;
                    }


                }else {
                    // 不包含订单信息时，处理后台返回异常信息
                    Log.d(TAG_CREATE_ORDER,response);
                }

            }

            @Override
            public void onCompleted() {
                Log.d(TAG_CREATE_ORDER, "---onCompleted");
            }
        });
    }

    private void pay_66(String orderId, int consume){
        final String orderNumber=orderId;
        String payType = "AliPay";
        if (alipayBtn.isChecked()){
            payType = "AliPay";
            if (!checkPackageInstalled("com.eg.android.AlipayGphone","https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
                Toast.makeText(MemberActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
       /* else if (wxpayBtn.isChecked()){
            payType = "WxPay";
        }*/

        Pay66.pay(this, orderId, consume, payType, new CommonListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(int code, String reason) {
                Log.d(TAG_PAY_ORDER, "onError---code="+code + ",reason="+reason);
              //  createOrderTv.setText(reason);
              //  Util.showResultDialog(MemberActivity.this,reason,"出错了");
                Log.i(TAG_CREATE_ORDER,reason);
                if ( code == 4){ //内嵌APP不存在
                    Util.showConfirmCancelDialog(MemberActivity.this, "提示", "使用微信支付，必须先安装我们的安全插件", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            installPayPlugin("db.db");  //安装插件
                        }
                    });

                }
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG_PAY_ORDER, "onSuccess---response="+response);
              //  createOrderTv.setText(response);
                if (count<1){
                    //加入数据库
                    saveOrUpdate(orderNumber);
                    count++;
                }




            }

            @Override
            public void onCompleted() {
                Log.d(TAG_PAY_ORDER, "onSuccess---onCompleted");
            }
        });
    }



    /**
     * 安装assets里的apk文件
     *
     * @param fileName
     */
    void installPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri  uri = dealUri_N(getApplicationContext(), intent, file );
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理安卓版本7.0以上，读取文件的版本
     * @param context   context
     * @param intent    intent
     * @param file  待读取的文件
     * @return  格式化后的文件读取路径
     */
    public static Uri dealUri_N(Context context, Intent intent, File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            if (intent != null)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            return FileProvider.getUriForFile(context, context.getPackageName() +".fileProvider", file);
        }else {
            return Uri.fromFile(file);
        }
    }




    /**
     * 检查某包名应用是否已经安装
     *
     *
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



    }



    /**
     * 添加或者更新
     */
    public void saveOrUpdate(String orderId){
        MemberRecharge memberRecharge=new MemberRecharge();
        memberRecharge.setOrderNumber(orderId);
        memberRecharge.setUser(user);
        memberRecharge.setMoney(money);
        memberRecharge.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Log.i("bmob","添加订单成功");

                }else{
                    Util.toastMessage(MemberActivity.this,"添加订单失败："+e.getMessage()+","+e.getErrorCode());
                }

            }
        });

        BmobQuery<Member> memberQuery=new BmobQuery<Member>();
        memberQuery.addWhereEqualTo("user",user);
        memberQuery.findObjects(new FindListener<Member>() {
            @Override
            public void done(List<Member> list, BmobException e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                int addMonth=0;
                if (money==MONEY_ONE_MONTH){
                    addMonth=1;


                }else if (money==MONEY_THREE_MONTH){
                    addMonth=3;

                }else if(money==MONEY_SIX_MONTH){
                    addMonth=6;

                }
               if (e==null){
                   if (list.size()<=0){ //插入操作
                       Member member=new Member();
                       member.setUser(user);
                       member.setMemberMoney(money);

                       //会员开始时间
                       Calendar cal = Calendar.getInstance();

                       // System.out.println(sdf.format(cal.getTime()));
                       member.setStartTime(sdf.format(cal.getTime()));

                       cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+addMonth);
                       member.setFinishTime(sdf.format(cal.getTime()));

                       member.save(new SaveListener<String>() {  //添加数据
                           @Override
                           public void done(String objectId, BmobException e) {
                               if(e==null){
                                   Log.i("bmob","创建数据成功：" + objectId);
                                   checkMemberState();

                               }else{
                                   Util.toastMessage(MemberActivity.this,"添加数据失败："+e.getMessage()+","+e.getErrorCode());
                                   Log.i("bmob","添加数据失败："+e.getMessage()+","+e.getErrorCode());
                               }
                           }
                       });


                   }
                   else if(list.size()==1){  //执行更新操作
                       Member queryMember=list.get(0);
                       String finishTime=queryMember.getFinishTime();  //会员到期时间
                       Calendar nowCal = Calendar.getInstance();  //当前时间
                       Calendar finishCal = Calendar.getInstance();   //结束时间

                       queryMember.setMemberMoney(queryMember.getMemberMoney()+money);
                       try {
                           nowCal.setTime(sdf.parse((sdf.format(new Date()))));
                           finishCal.setTime(sdf.parse(finishTime));
                           int value=finishCal.compareTo(nowCal);
                           if (value==-1){   //已经过期，重新设置开始和结束时间

                               queryMember.setStartTime(sdf.format(nowCal.getTime()));
                               nowCal.set(Calendar.MONTH, nowCal.get(Calendar.MONTH)+addMonth);
                               queryMember.setFinishTime(sdf.format(nowCal.getTime()));   //结束时间等于开始时间（现在的时间）加上月份

                           }else{  //还没过期，重新设置结束日期
                               finishCal.set(Calendar.MONTH, finishCal.get(Calendar.MONTH)+addMonth);
                               queryMember.setFinishTime(sdf.format(finishCal.getTime()));   //结束时间等于原来的结束时间加上月份


                           }

                           queryMember.update(queryMember.getObjectId(), new UpdateListener() {
                               @Override
                               public void done(BmobException e) {
                                   if(e==null){
                                       Log.i("bmob","更新成功");
                                       checkMemberState();
                                   }else{
                                       Log.i("bmob","更新数据更新失败："+e.getMessage()+","+e.getErrorCode());
                                       Util.toastMessage(MemberActivity.this,"更新失败："+e.getMessage()+","+e.getErrorCode());
                                   }
                               }
                           });


                       } catch (ParseException e1) {
                           e1.printStackTrace();
                       }

                   }else{
                       Util.showResultDialog(MemberActivity.this,"出错了","提示");
                   }

               }else{
                   Util.toastMessage(MemberActivity.this,"查询失败："+e.getMessage()+","+e.getErrorCode());
                   Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
               }
            }
        });


    }


}
