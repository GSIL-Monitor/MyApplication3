package com.cxy.magazine.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cxy.magazine.bmobBean.Bookshelf;
import com.cxy.magazine.bmobBean.BuyBean;
import com.cxy.magazine.bmobBean.Member;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.adapter.DataAdapter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.view.CommonFooter;
import com.github.jdsjlzx.view.CommonHeader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.cxy.magazine.util.Utils;


public class MagazineDirectoryActivity extends BasicActivity {
  //  private static final String MAGAZINE_URL = "http://m.fx361.com";
    private String httpUrl = "";
    private String magazineTitle = "", magazineIntro = "", magazineTime = "", magazineHistoryHref = "", coverImageUrl = "";
    private String magazieId="";
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
 //   User user;


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
        magazieId=names[2]+names[3].split(".html")[0];


        // queryMemberState();
      //  checkBuyState();
        setRecycleView();
     //   Thread thread = new GetData();
    //    thread.start();
        Log.i(LOG_TAG, "MagazineDirectoryActivity onCreate()");
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
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);
        //add a HeaderView
        View headerView = new CommonHeader(this, R.layout.header_magazine_recycleview);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        // ButterKnife.bind(this,headerView);
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
        CommonFooter footerView = new CommonFooter(this, R.layout.layout_empty);
        TextView tvFoot = (TextView) footerView.findViewById(R.id.tv_foot);
        tvFoot.setText("没有更多数据了");
        mLRecyclerViewAdapter.addFooterView(footerView);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                if (position > 10) {
                   User user=BmobUser.getCurrentUser(User.class);
                   if (user==null){
                       Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "亲，登录后才可查看内容哦", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
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

              //不检查会员，华为接口
              /*  String type = dataList.get(position).get("type").toString();
                if ("item".equals(type)) {
                    String url = dataList.get(position).get("href").toString();
                    //跳转至内容显示Activity
                    Intent intent = new Intent(MagazineDirectoryActivity.this, MagazineContentActivity.class);
                    //  String mobileUrl=(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");
                    intent.putExtra("url", url);
                    startActivity(intent);
                }*/


            }
        });

    }

    private void readArticle(int position) {
        if (memberState == 1) {  //不是会员，提示购买会员

            AlertDialog dlg = new AlertDialog.Builder(MagazineDirectoryActivity.this).setTitle("提示").setMessage("亲，该部分内容会员才可观看，请充值会员或者返回单独购买该本杂志")
                    .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkdMember=false;
                            Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();
        }

        if (memberState == 2) {   //是会员，已过期

            AlertDialog dlg = new AlertDialog.Builder(MagazineDirectoryActivity.this).setTitle("提示").setMessage("亲，该部分内容为会员专享，你的会员已过期！请充值会员或者返回单独购买该本杂志")
                    .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkdMember=false;
                            Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();

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
            query.addWhereEqualTo("id",magazieId);
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
        if (type == null) {   //"shelf"
            getMenuInflater().inflate(R.menu.menu_magazine_directory, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.addShelf) {
            // Util.toastMessage(MagazineDirectoryActivity.this,"加入书架");

            User user = BmobUser.getCurrentUser(User.class);
            if (user == null) {   //未登录
                Utils.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    }
                });
            } else {
                //加入书架
                Bookshelf bookshelf = new Bookshelf();
                bookshelf.setUser(user);
                bookshelf.setBookName(magazineTitle);
                bookshelf.setPulishTime(magazineTime);
                bookshelf.setCoverUrl(coverImageUrl);
                bookshelf.setDirectoryUrl(httpUrl);


                bookshelf.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                        if (e == null) {

                            Snackbar.make(tv_title, "已将该杂志加入书架", Snackbar.LENGTH_LONG).setAction("", null).show();
                        } else {
                            Utils.toastMessage(MagazineDirectoryActivity.this, e.getMessage());
                        }
                    }
                });
            }


        }

        return true;
    }

}
