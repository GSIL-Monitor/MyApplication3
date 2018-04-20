package com.cxy.magazine.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.bmobBean.Member;
import com.cxy.magazine.bmobBean.MemberPrice;
import com.cxy.magazine.bmobBean.MemberRecharge;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.util.ResponseParam;
import com.cxy.magazine.util.Utils;
import com.eagle.pay66.Pay66;
import com.eagle.pay66.listener.CommonListener;
import com.eagle.pay66.vo.OrderPreMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class MemberActivity extends BasicActivity implements View.OnClickListener{
    TextView memberName,memberInfo,payMoney;
    Button oneBtn,threeBtn,sixBtn,payBtn;
    User user;
    @BindView(R.id.tv_1month_original) TextView tv1Original;
    @BindView(R.id.tv_1month_current) TextView tv1Current;
    @BindView(R.id.tv_3month_original) TextView tv3Original;
    @BindView(R.id.tv_3month_current) TextView tv3current;
    @BindView(R.id.tv_6month_original) TextView tv6Original;
    @BindView(R.id.tv_6month_current) TextView tv6Current;

    View contentView;
    RadioButton alipayBtn,wxpayBtn;
    Dialog  bottomDialog;
    double money;
    private static final String TAG_CREATE_ORDER = "createOrder";
    private static final String TAG_PAY_ORDER = "payOrder";
    private  double originalOne,originalThree,originalSix;   //原来的价格
    private  double currentOne,currentThree,currentSix;   //现在的价格
    int count=0;
    private static final String alipayPackageName = "com.eg.android.AlipayGphone";
    private static final String wxpayPackageName = "com.tencent.mm";
    private static  final int IMAGE_LOAD_FINISHED=100;
    private ImageView imageView;
    private Bitmap headImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();


    }

    private void initView(){
        ActionBar bar= getSupportActionBar();
        bar.setTitle("会员中心");
        bar.setDisplayHomeAsUpEnabled(true);


        memberName=(TextView)findViewById(R.id.tvMemberName);
        memberInfo=(TextView)findViewById(R.id.tvMemberInfo);
        imageView=(ImageView)findViewById(R.id.imageHead);
        ButterKnife.bind(this);


        oneBtn=(Button)findViewById(R.id.btn1Month);
        threeBtn=(Button)findViewById(R.id.btn3Month);
        sixBtn=(Button)findViewById(R.id.btn6Month);

        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null);
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setContentView(contentView);


        payMoney=(TextView)contentView.findViewById(R.id.tvMoney);
        alipayBtn=(RadioButton)contentView.findViewById(R.id.aliPay);   //支付宝支付
        wxpayBtn=(RadioButton)contentView.findViewById(R.id.wxPay);    //微信支付
        payBtn=(Button)contentView.findViewById(R.id.btnPay);






        oneBtn.setOnClickListener(this);
        threeBtn.setOnClickListener(this);
        sixBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);

    }

    public void setPrice(){
        BmobQuery<MemberPrice> query=new BmobQuery<MemberPrice>();
        query.findObjects(new FindListener<MemberPrice>() {
            @Override
            public void done(List<MemberPrice> list, BmobException e) {
                if (e==null&&list!=null){
                    for(MemberPrice memberPrice : list){
                        Integer monthSum=memberPrice.getMonthSum();
                        Double originalPrice=memberPrice.getOriginalPrice();  //原价
                        Double currentPrice=memberPrice.getCurrentPrice();   //现价
                        String remark=memberPrice.getRemark();               //备注
                        switch (monthSum){
                            case 1:

                                tv1Current.setText("¥"+currentPrice.toString());
                                if (originalPrice>0){    //判断是否有原价
                                    tv1Original.setText("¥"+originalPrice.toString());
                                    tv1Original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                }


                                currentOne=currentPrice;
                                break;
                            case 3:

                                tv3current.setText("¥"+currentPrice.toString());
                                if (originalPrice>0){
                                    tv3Original.setText("¥"+originalPrice.toString());
                                    tv3Original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                }


                                currentThree=currentPrice;
                                break;
                            case 6:

                                tv6Current.setText("¥"+currentPrice.toString());

                                if (originalPrice>0){
                                    tv6Original.setText("¥"+originalPrice.toString());
                                    tv6Original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                }


                                currentSix=currentPrice;
                                break;

                        }
                    }
                }
                else{
                    Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkMemberState();   //设置用户信息
        setPrice();  //  设置会员月份价格
    }

    public void checkMemberState(){
        user= BmobUser.getCurrentUser(User.class);
        if (user != null) {
            memberName.setText(user.getUsername());
            setUserImage();

            BmobQuery<Member> memberQuery = new BmobQuery<Member>();
            memberQuery.addWhereEqualTo("user", user);

            memberQuery.findObjects(new FindListener<Member>() {//查询会员信息
                @Override
                public void done(List<Member> list, BmobException e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (e == null && list!=null) {
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
                        Utils.toastMessage(MemberActivity.this, "出错了");
                    }
                }
            });

        }


    }

    public void setUserImage(){
        String userImageUrl=user.getHeadImageUrl();
        if (!Utils.isEmpty(userImageUrl)){
            headImage=mCache.getAsBitmap("headImageBitmap");
            if (headImage!=null){
                imageView.setImageBitmap(headImage);
            }else{
                Thread thread=new GetImageThread();
                thread.start();
            }
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
                    imageView.setImageBitmap(headImage);
                    mCache.put("headImageBitmap",headImage);
                    break;
            }

        }

    };


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
            payMoney.setText("¥"+currentOne);
            money=currentOne;
            showDialog();
        }
        else if (view.getId()==R.id.btn3Month){
            payMoney.setText("¥"+currentThree);
            money=currentThree;
            showDialog();
        }
        else if (view.getId()==R.id.btn6Month){
            payMoney.setText("¥"+currentSix);
            money=currentSix;
            showDialog();
        }
        else if (view.getId()==R.id.btnPay){
            // Utils.toastMessage(MemberActivity.this,"支付宝支付");
            bottomDialog.dismiss();
            // pay();  //Bmob支付
            createOrder();
        }
    }


    public void createOrder(){
        count=0;
        String message="";

        if (money==currentOne){
            message="杂志天下1个月会员支付";
        }else if (money==currentThree){
            message="杂志天下3个月会员支付";
        }else  if (money==currentSix){
            message="杂志天下6个月会员支付";
        }else{
            message="杂志天下会员支付";
        }



        //(int)(money*100)
        Pay66.createOrder((int)(money*100), message, message, new CommonListener() {   //单位：分
            @Override
            public void onStart() {
                Log.d(TAG_CREATE_ORDER, "---onStart");
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG_CREATE_ORDER, "---onError");
                Log.d(TAG_CREATE_ORDER, "--onError--code=" + code + ",msg=" + msg);
                Utils.showResultDialog(MemberActivity.this,msg,"创建订单失败");
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
        });//
    }

    private void pay_66(String orderId, int consume){
        final String orderNumber=orderId;
        String payType = "AliPay";

        if (alipayBtn.isChecked()){
            payType = "AliPay";
            if ( !isAppExist(getApplicationContext(), alipayPackageName)){
                Toast.makeText(getApplicationContext(), "用户未安装支付宝", Toast.LENGTH_SHORT).show();
                return;
            }
        }else if (wxpayBtn.isChecked()){
            payType = "WxPay";
            if ( !isAppExist(getApplicationContext(), wxpayPackageName)){
                Toast.makeText(getApplicationContext(), "用户未安装微信，无法支付", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!installPayPlugin()){  //用户未安装支付插件，无法进行微信支付
                Utils.showConfirmCancelDialog(MemberActivity.this, "提示", "第一次使用微信支付，必须先安装我们的安全插件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        installPayPlugin("db.db");  //安装插件
                    }
                });
                return;
            }
        }


        Pay66.pay(this, orderId, consume, payType, new CommonListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(int code, String reason) {
                Log.d(TAG_PAY_ORDER, "onError---code="+code + ",reason="+reason);
                //  createOrderTv.setText(reason);
                //  Utils.showResultDialog(MemberActivity.this,reason,"出错了");
                Log.i(TAG_CREATE_ORDER,reason);
                if ( code == 4){ //内嵌APP不存在
                    Utils.showConfirmCancelDialog(MemberActivity.this, "提示", "使用微信支付，必须先安装我们的安全插件", new DialogInterface.OnClickListener() {
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
    }//

    /**
     * 检查支付插件是否需要安装/更新
     * 需要的话，则进行安装
     * @return true安装
     */
    boolean installPayPlugin(){
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo("com.eagle.pay66safe", 0);
            Log.d(TAG_CREATE_ORDER, "versionCode = " + packageInfo.versionCode);
            if ( packageInfo != null && !Pay66.isAppNeedUpdate(packageInfo.versionCode)){
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
      //  installPayPlugin("db.db");
        return false;
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
     * 校验手机中是否安装某应用
     * @param context   getApplicationContext()
     * @param packageName   包名
     * @return  true应用已安装
     */
    public static boolean isAppExist(Context context, String packageName) {
        // 获取packagemanager
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(packageInfo ==null){
            return false;
        }else{
            return true;
        }
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
                    Utils.toastMessage(MemberActivity.this,"添加订单失败："+e.getMessage()+","+e.getErrorCode());
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
                if (money==currentOne){
                    addMonth=1;


                }else if (money==currentThree){
                    addMonth=3;

                }else if(money==currentSix){
                    addMonth=6;

                }
                if (e==null&&list!=null){
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
                                    Utils.toastMessage(MemberActivity.this,"添加数据失败："+e.getMessage()+","+e.getErrorCode());
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
                                        Utils.toastMessage(MemberActivity.this,"更新失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });


                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                    }else{
                        Utils.showResultDialog(MemberActivity.this,"出错了","提示");
                    }

                }else{
                    Utils.toastMessage(MemberActivity.this,"查询失败："+e.getMessage()+","+e.getErrorCode());
                    Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
