package com.cxy.magazine.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.MyApplication;
import com.cxy.magazine.bmobBean.Bookshelf;
import com.cxy.magazine.bmobBean.BuyBean;
import com.cxy.magazine.bmobBean.Member;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.adapter.DataAdapter;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.ResponseParam;
import com.cxy.magazine.view.SampleFooter;
import com.cxy.magazine.view.SampleHeader;
import com.eagle.pay66.Pay66;
import com.eagle.pay66.listener.CommonListener;
import com.eagle.pay66.vo.OrderPreMessage;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.cxy.magazine.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;


public class MagazineDirectoryActivity extends BasicActivity {
  //  private static final String MAGAZINE_URL = "http://m.fx361.com";
    private String httpUrl = "";
    private String magazineTitle = "", magazineTime = "", coverImageUrl = "",magazineHistoryHref="";
    private String magazineId="";
    private List<HashMap> dataList;
    private DataAdapter dataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
 //   private View header = null;
    private int memberState = 0;    // 1 :不是会员 2：是会员，但会员已过期 3：是会员，且未过期
    private int buyState=0;     //1、已购买  2、未购买
    private boolean checkdMember=false;
    @BindView(R.id.rv_directory)
    LRecyclerView mRecyclerView;
    @BindView(R.id.magazine_title)
    TextView tv_title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TextView tv_time;
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
        setContentView(R.layout.activity_magazine_directory);
        ButterKnife.bind(this);
        //设置Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        httpUrl = getIntent().getStringExtra("href");
        dataList = new ArrayList<HashMap>();

        String[] names=httpUrl.split("//")[1].split("/");
        magazineId=names[2]+names[3].split(".html")[0];


        // queryMemberState();
      //  checkBuyState();
        setRecycleView();
        setBottomDialog();

    }




    public void setRecycleView() {


        dataAdapter = new DataAdapter(dataList, this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(dataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        /*//创建线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //垂直方向
        layoutManager.setOrientation(OrientationHelper.VERTICAL);*/
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.thin_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);
        //add a HeaderView
        SampleHeader headerView = new SampleHeader(this);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        mLRecyclerViewAdapter.addHeaderView(headerView);

        //禁用下拉刷新功能
      //  mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Thread thread = new GetData();
                thread.start();
            }
        });
        mRecyclerView.refresh();
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);
        //add a FooterView
        SampleFooter footerView = new SampleFooter(this);
        TextView tvFoot = (TextView) footerView.findViewById(R.id.tv_foot);
        tvFoot.setText("没有更多数据了");
        mLRecyclerViewAdapter.addFooterView(footerView);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                if (position > 10) {
                   User user=BmobUser.getCurrentUser(User.class);
                   if (user==null){

                      Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "亲，登录后才可查看内容哦", new QMUIDialogAction.ActionListener() {
                          @Override
                          public void onClick(QMUIDialog dialog, int index) {
                              checkdMember=false;
                              Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
                              startActivity(intent1);
                          }
                      });
                   }else{   //user不为null，肯定已经查询过会员状态了
                          if (buyState==1){
                              checkdMember=true;
                              toContentView(position);
                          }else{
                              readArticle(position);
                          }


                   }


                } else {   //前10条随意查看
                    checkdMember=true;
                    toContentView(position);


                }




            }
        });

    }

    @OnClick(R.id.btn_buy)
    public void  buyPerMagazine(){
        User user = BmobUser.getCurrentUser(User.class);
        if (user == null) {   //未登录
            Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "请先登录！", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialogs, int i) {
                    //    dialogs.dismiss();
                    Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
                    startActivity(intent1);
                }
            });
        } else {
            //TODO:购买杂志
            if (buyState==1){
                Utils.toastMessage(MagazineDirectoryActivity.this,"你已购买该本杂志，可免费畅读所有内容");
            }else{
                showDialog();
            }

        }
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

    /*@OnClick(R.id.close_btn)
    public void close(){
        MyApplication.getInstance().closeActivities();
    }*/
    private void readArticle(int position) {
        if (memberState == 1) {  //不是会员，提示购买会员


            new QMUIDialog.MessageDialogBuilder(MagazineDirectoryActivity.this)
                    .setTitle("提示")
                    .setMessage("亲，该部分内容会员才可观看，请充值会员或者单独购买该本杂志")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "去充值会员", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            checkdMember=false;
                            Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                            startActivity(intent);
                        }
                    })
                    .create().show();
        }

        if (memberState == 2) {   //是会员，已过期



            new QMUIDialog.MessageDialogBuilder(MagazineDirectoryActivity.this)
                    .setTitle("提示")
                    .setMessage("亲，该部分内容会员才可观看，请充值会员或者返回上一页单独购买该本杂志")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "去充值会员", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            checkdMember=false;
                            Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                            startActivity(intent);
                        }
                    })
                    .create().show();

        }
        if (memberState == 3) {  //没有过期，正常查看
            checkdMember=true;
            toContentView(position);
        }//
    }

    public void toContentView(int position){
        String type = dataList.get(position).get("type").toString();
        if ("item".equals(type)) {

            String url = dataList.get(position).get("href").toString();
            //跳转至内容显示Activity
            Intent intent = new Intent(MagazineDirectoryActivity.this, MagazineContentActivity.class);
            //       String mobileUrl=(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");
            intent.putExtra("url", url);
            startActivity(intent);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkdMember){
            queryBuyState();
            queryMemberState();

        }

        Log.i(LOG_TAG,"MagazineDetailActivity---onStart()");

    }



    private void queryMemberState() {
        Log.i(LOG_TAG,"查询用户会员状态");
        User  user = BmobUser.getCurrentUser(User.class);
        if(user!=null){
            //判断用户的会员状态
            BmobQuery<Member> query = new BmobQuery<Member>();
            query.addWhereEqualTo("user", user);
            query.findObjects(new FindListener<Member>() {
                @Override
                public void done(List<Member> list, BmobException e) {
                    if (e == null && list!=null) {
                        if (list.size() <= 0) {   //不是会员，提示购买会员
                            memberState = 1;
                        } else {   //分两种情况：1、是会员且会员没有过期 2、是会员，已过期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Member queryMember = list.get(0);
                            String finishTime = queryMember.getFinishTime();  //数据库里存储的会员到期时间
                            Calendar nowCal = Calendar.getInstance();  //当前日期
                            Calendar finishCal = Calendar.getInstance();   //会员到期日期
                            try {

                                nowCal.setTime(sdf.parse((sdf.format(new Date()))));
                                finishCal.setTime(sdf.parse(finishTime));
                                int value = finishCal.compareTo(nowCal);
                                if (value == -1) {   //会员已经过期
                                    memberState = 2;

                                } else {  //没有过期，正常查看
                                    memberState = 3;
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        }
                        //查询完状态之后，查看文章
                     //   readArticle(position);

                    } else {
                        Utils.toastMessage(MagazineDirectoryActivity.this, "查询会员状态出错：" + e.getMessage());
                    }
                }
            });//
        }
    }

    //查询该书的购买情况
    public void queryBuyState(){
        Log.i(LOG_TAG,"查询购买状态");
        User user=BmobUser.getCurrentUser(User.class);
        if (user!=null){
            BmobQuery<BuyBean> query=new BmobQuery<BuyBean>();
            query.addWhereEqualTo("user",user);
            query.addWhereEqualTo("id",magazineId);
            query.findObjects(new FindListener<BuyBean>() {
                @Override
                public void done(List<BuyBean> list, BmobException e) {
                    if (e==null&&list!=null){
                        if (list.size()>0){
                            buyState=1;
                            Utils.toastMessage(MagazineDirectoryActivity.this,"你已购买该本杂志，可免费畅读所有内容");
                        }else{
                            buyState=2;
                        }
                    }
                }
            });
        }
    }

    public void createOrder(){
        count=0;
        String message="杂志购买";
        //(int)(money*100)
        Pay66.createOrder((int)(money*100), message, message, new CommonListener() {   //单位：分
            @Override
            public void onStart() {
               // Log.d(TAG_CREATE_ORDER, "---onStart");
            }

            @Override
            public void onError(int code, String msg) {
              //  Log.d(TAG_CREATE_ORDER, "---onError");
            //    Log.d(TAG_CREATE_ORDER, "--onError--code=" + code + ",msg=" + msg);
                Utils.showResultDialog(MagazineDirectoryActivity.this,msg,"创建订单失败");
            }

            @Override
            public void onSuccess(String response) {
              //  Log.d(TAG_CREATE_ORDER, "---onSuccess");
              //  Log.d(TAG_CREATE_ORDER, "---onSuccess--response=" + response);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                ResponseParam<OrderPreMessage> responseParam = gson.fromJson(response, new TypeToken<ResponseParam<OrderPreMessage>>() {
                }.getType());
                if ( responseParam!=null && responseParam.getData() !=null){
                  //  Log.d(TAG_CREATE_ORDER, "---onSuccess--orderId=" + responseParam.getData().getOrderId());
                    //防止重复提交订单
                    if (count<1){
                        pay_66(responseParam.getData().getOrderId(), responseParam.getData().getConsume()); //进行支付
                        // count++;
                    }


                }else {
                    // 不包含订单信息时，处理后台返回异常信息
                //    Log.d(TAG_CREATE_ORDER,response);
                }

            }

            @Override
            public void onCompleted() {
              //  Log.d(TAG_CREATE_ORDER, "---onCompleted");
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
                Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "使用微信支付，必须先安装我们的安全插件", new QMUIDialogAction.ActionListener() {
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
              //  Log.d(TAG_PAY_ORDER, "onError---code="+code + ",reason="+reason);
                //  createOrderTv.setText(reason);
                //  Utils.showResultDialog(MemberActivity.this,reason,"出错了");
             //   Log.i(TAG_CREATE_ORDER,reason);
                if ( code == 4){ //内嵌APP不存在
                    Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "第一次使用微信支付，必须先安装我们的安全插件", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int which) {
                            installPayPlugin("db.db");  //安装插件
                        }
                    });

                }
            }

            @Override
            public void onSuccess(String response) {
             //   Log.d(TAG_PAY_ORDER, "onSuccess---response="+response);
                //加入数据库
                saveBuyBook();
                count++;



            }

            @Override
            public void onCompleted() {
              //  Log.d(TAG_PAY_ORDER, "onSuccess---onCompleted");
            }
        });
    }//

    public void  saveBuyBook(){

        BuyBean buyBean=new BuyBean();
        User user=BmobUser.getCurrentUser(User.class);
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
                    Utils.toastMessage(MagazineDirectoryActivity.this,"购买书籍失败："+e.getMessage()+",请联系客服");
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
          //  Log.d(TAG_CREATE_ORDER, "versionCode = " + packageInfo.versionCode);
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


    class GetData extends Thread {
        @Override
        public void run() {


            try {
                dataList.clear();

                String html= OkHttpUtil.get(httpUrl);
                Document docHtml = Jsoup.parse(html);
                Element introDiv = docHtml.getElementsByClass("magBox1").first();
                magazineTime = introDiv.getElementsByTag("p").first().text();
                coverImageUrl = introDiv.getElementsByTag("a").first().attr("href");
              //  magazineIntro = introDiv.getElementsByClass("rec").first().getElementsByTag("p").first().text();
                magazineTitle = docHtml.getElementsByTag("h3").first().text();
                magazineHistoryHref = docHtml.getElementsByClass("btn_history act_history").first().attr("href");   //没有前缀

                Element dirDiv = docHtml.getElementById("dirList");  //目录div
                Elements dirElements = dirDiv.getElementsByClass("dirItem02");
                for (Element dirElement : dirElements) {
                    String subTitle = dirElement.getElementsByTag("h5").first().text();
                    HashMap titleMap = new HashMap<String, String>();
                    titleMap.put("type", "title");
                    titleMap.put("text", subTitle);
                    dataList.add(titleMap);

                    Elements lis = dirElement.getElementsByTag("ul").first().getElementsByTag("li");
                    for (Element li : lis) {
                        String text = li.getElementsByTag("a").first().text();
                        String href = li.getElementsByTag("a").first().attr("href");
                        HashMap dirMap = new HashMap<String, String>();
                        dirMap.put("type", "item");
                        dirMap.put("text", text);
                        dirMap.put("href", href);
                        dataList.add(dirMap);
                    }
                }

                handler.sendEmptyMessage(100);


            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(101);
            }


        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                mRecyclerView.refreshComplete(1000);
                tv_title.setText(magazineTitle);
                tv_time.setText(magazineTime + "目录");
                mLRecyclerViewAdapter.notifyDataSetChanged();
            } else if (msg.what == 101) {
                Utils.toastMessage(MagazineDirectoryActivity.this, "出错了,该杂志内容暂无法查看，换本杂志看看吧！");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为toolbar创建Menu
        String type = getIntent().getStringExtra("type");
        getMenuInflater().inflate(R.menu.menu_magazine_directory, menu);
        if (type == null) {
            menu.removeItem(R.id.scanHistory);
        }
        if ("shelf".equals(type)){  //从书架中进入，隐藏加入书架
            menu.removeItem(R.id.addShelf);

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MyApplication.getInstance().closeActivity(this);
        }
        if (item.getItemId() == R.id.addShelf) {
            // Util.toastMessage(MagazineDirectoryActivity.this,"加入书架");

            User user = BmobUser.getCurrentUser(User.class);
            if (user == null) {   //未登录
             Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "请先登录！", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialogs, int i) {
                    //    dialogs.dismiss();
                        Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    }
                });
            } else {
                //加入书架
                final  Bookshelf bookshelf = new Bookshelf();
                bookshelf.setUser(user);
                bookshelf.setBookName(magazineTitle);
                bookshelf.setPulishTime(magazineTime);
                bookshelf.setCoverUrl(coverImageUrl);
                bookshelf.setDirectoryUrl(httpUrl);


                bookshelf.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                        if (e == null) {
                           ArrayList<Bookshelf> shelfList=null;
                           Object object=mCache.getAsObject("shelfCache");
                           if (object==null){
                               shelfList=new ArrayList<Bookshelf>();
                           }else{
                               shelfList=( ArrayList<Bookshelf>)object;
                           }
                           shelfList.add(0,bookshelf);
                        //   shelfList.add(bookshelf);
                           mCache.put("shelfCache",shelfList);
                           Snackbar.make(tv_title, "已将该杂志加入书架", Snackbar.LENGTH_LONG).setAction("", null).show();
                        } else {
                            Utils.toastMessage(MagazineDirectoryActivity.this, e.getMessage());
                        }
                    }
                });
            }


        }

        if (item.getItemId()==R.id.scanHistory){
            // Util.toastMessage(MagazineDirectoryActivity.this,"浏览往期");
            Intent intent=new Intent(this,MagazineHistoryActivity.class);
            intent.putExtra("historyUrl",magazineHistoryHref);
            intent.putExtra("title",magazineTitle);
            startActivity(intent);
        }

        return true;
    }

}
