package com.cxy.magazine.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cxy.magazine.bmobBean.Bookshelf;
import com.cxy.magazine.bmobBean.BuyBean;
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
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
/*import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.xiaomi.ad.common.SdkConfig.getContext;*/

public class MagazineDetailActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tv_title;
   // @BindView(R.id.add_shelf)
  //  TextView tv_addShelf;
    @BindView(R.id.buy)
    TextView tv_buy;
    @BindView(R.id.start_read)
    TextView tv_startRead;
    @BindView(R.id.watch_history)
    TextView tv_watchHistory;
    @BindView(R.id.im_cover)
    ImageView im_cover;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_intro)
    TextView tv_intro;
    private String httpUrl = "";
    private String magazineTitle = "", magazineIntro = "", magazineTime = "", magazineHistoryHref = "", coverImageUrl = "";
   // private Bitmap bookCover;
    private String magazineId;
    private static final String TAG_CREATE_ORDER = "createOrder";
    private static final String TAG_PAY_ORDER = "payOrder";
    private User user;
    //购买底部框
    View contentView;
    RadioButton alipayBtn,wxpayBtn;
    Dialog bottomDialog;
    double money=2;
    TextView  payMoney;
    Button payBtn;
    int count=0;
    private static final String alipayPackageName = "com.eg.android.AlipayGphone";
    private static final String wxpayPackageName = "com.tencent.mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_detail);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setBottomDialog();

        //获取屏幕宽度
        WindowManager m = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels/2;
        int height=width+width/3;

        //设置Imageview宽度和高度
        ViewGroup.LayoutParams layoutParams = im_cover.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        im_cover.setLayoutParams(layoutParams);

        httpUrl = getIntent().getStringExtra("href");   //http://www.fx361.com/bk/sdzx/index.html
        String[] names=httpUrl.split("//")[1].split("/");
        if (names.length>=4){
            magazineId=names[2]+names[3].split(".html")[0];
        }


        Thread thread=new GetData();
        thread.start();


    }

    public  void  setBottomDialog(){
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null);
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setContentView(contentView);


        payMoney=(TextView)contentView.findViewById(R.id.tvMoney);
        alipayBtn=(RadioButton)contentView.findViewById(R.id.aliPay);   //支付宝支付
        wxpayBtn=(RadioButton)contentView.findViewById(R.id.wxPay);    //微信支付
        payBtn=(Button)contentView.findViewById(R.id.btnPay);

        //确认支付
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                createOrder();
            }
        });
    }
    public void createOrder(){
        count=0;
        String message="杂志购买";
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
                Utils.showResultDialog(MagazineDetailActivity.this,msg,"创建订单失败");
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
                Utils.showConfirmCancelDialog(MagazineDetailActivity.this, "提示", "使用微信支付，必须先安装我们的安全插件", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int which) {
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
                    Utils.showConfirmCancelDialog(MagazineDetailActivity.this, "提示", "第一次使用微信支付，必须先安装我们的安全插件", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int which) {
                            installPayPlugin("db.db");  //安装插件
                        }
                    });

                }
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG_PAY_ORDER, "onSuccess---response="+response);
                    //加入数据库
                    saveBuyBook();
                    count++;





            }

            @Override
            public void onCompleted() {
                Log.d(TAG_PAY_ORDER, "onSuccess---onCompleted");
            }
        });
    }//

    public void  saveBuyBook(){

        BuyBean buyBean=new BuyBean();
        buyBean.setUser(user);
        buyBean.setId(magazineId);
        buyBean.setBookName(magazineTitle);
        buyBean.setPublishTime(magazineTime);
        buyBean.setCoverUrl(coverImageUrl);
        buyBean.setDirectoryUrl(httpUrl);
        buyBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){

                    Snackbar.make(tv_title, "已成功购买该杂志", Snackbar.LENGTH_LONG).setAction("", null).show();
                }else {
                    Utils.toastMessage(MagazineDetailActivity.this,"购买书籍失败："+e.getMessage()+",请联系客服");
                }
            }
        });

    }

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
     //   installPayPlugin("db.db");
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
            Uri uri = dealUri_N(getApplicationContext(), intent, file );
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

    //加入书架
   /* @OnClick(R.id.add_shelf)
    public void addShelf()
    {
        User user= BmobUser.getCurrentUser(User.class);
        if (user == null) {   //未登录
            Utils.showConfirmCancelDialog(MagazineDetailActivity.this, "提示", "请先登录！", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(MagazineDetailActivity.this, LoginActivity.class);
                    startActivity(intent1);
                }
            });
        } else {
            //加入书架
            Bookshelf bookshelf=new Bookshelf();
            bookshelf.setUser(user);
            bookshelf.setBookName(magazineTitle);
            bookshelf.setPulishTime(magazineTime);
            bookshelf.setCoverUrl(coverImageUrl);
            bookshelf.setDirectoryUrl(httpUrl);


            bookshelf.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {

                    if (e==null){

                        Snackbar.make(tv_title, "已将该杂志加入书架", Snackbar.LENGTH_LONG).setAction("", null).show();
                    }else {
                        Utils.toastMessage(MagazineDetailActivity.this,e.getMessage());
                    }
                }
            });
        }

    }*/
    //开始阅读
    @OnClick(R.id.start_read)
    public void  startRead(){
        Intent intent=new Intent(MagazineDetailActivity.this,MagazineDirectoryActivity.class);
        intent.putExtra("href",httpUrl);
        startActivity(intent);
    }

   //购买书籍
   @OnClick(R.id.buy)
   public void buy(){
        user= BmobUser.getCurrentUser(User.class);
       if (user == null) {   //未登录
           Utils.showConfirmCancelDialog(MagazineDetailActivity.this, "提示", "请先登录！", new QMUIDialogAction.ActionListener() {
               @Override
               public void onClick(QMUIDialog dialog, int i) {
                   Intent intent1 = new Intent(MagazineDetailActivity.this, LoginActivity.class);
                   startActivity(intent1);
               }
           });
       } else {
           showDialog();
       }

   }

    private void showDialog() {
        payMoney.setText("¥ 2.00");


        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        bottomDialog.setCanceledOnTouchOutside(true);



    }
    //浏览往期
    @OnClick(R.id.watch_history)
   public void watchHistory(){
        //Util.toastMessage(MagazineDirectoryActivity.this,"浏览往期");
        Intent intent=new Intent(this,MagazineHistoryActivity.class);
        intent.putExtra("historyUrl",magazineHistoryHref);
        intent.putExtra("title",magazineTitle);
        startActivity(intent);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    class GetData extends Thread {
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element introDiv = docHtml.getElementsByClass("magBox1").first();
                magazineTime = introDiv.getElementsByTag("p").first().text();
                coverImageUrl = introDiv.getElementsByTag("a").first().attr("href");
                magazineIntro = introDiv.getElementsByClass("rec").first().getElementsByTag("p").first().text();
                magazineTitle = docHtml.getElementsByTag("h3").first().text();
                magazineHistoryHref = docHtml.getElementsByClass("btn_history act_history").first().attr("href");   //没有前缀
            //    bookCover= Utils.getbitmap(coverImageUrl);
                handler.sendEmptyMessage(100);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(101);
            }
        }

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==100){
                Glide.with(MagazineDetailActivity.this)
                        .load(coverImageUrl)
                        .placeholder(R.drawable.default_book)
                        .error(R.drawable.default_book)
                        .into(im_cover);
                tv_title.setText(magazineTitle);
                tv_time.setText(magazineTime);
                tv_intro.setText(magazineIntro);
            }else if (msg.what==101){
            //    tv_addShelf.setEnabled(false);
                tv_buy.setEnabled(false);
                tv_startRead.setEnabled(false);
                tv_watchHistory.setEnabled(false);
             //   Utils.toastMessage(MagazineDetailActivity.this,"亲，出错了，该杂志内容暂时无法阅读，换一本吧！");
                tv_intro.setText("亲，出错了，该杂志内容暂时无法阅读，换一本吧！");
            }
        }
    };

}

