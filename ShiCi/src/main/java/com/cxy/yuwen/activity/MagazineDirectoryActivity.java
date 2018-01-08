package com.cxy.yuwen.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.yuwen.Adapter.DataAdapter;
import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.bmobBean.Member;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.fragment.MyFragment;
import com.cxy.yuwen.tool.RecyclerAdapter;
import com.cxy.yuwen.tool.Util;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.a.a.This;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MagazineDirectoryActivity extends BasicActivity {
    private static final String MAGAZINE_URL="http://m.fx361.com";
    private String httpUrl="";
    private String magazineTitle="",magazineIntro="",magazineTime="",magazineHistoryHref="",coverImageUrl="";
    private List<HashMap> dataList;
    private DataAdapter dataAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private  View header=null;
    @BindView(R.id.rv_directory)   LRecyclerView mRecyclerView;
    @BindView(R.id.magazine_title) TextView tv_title;
    @BindView(R.id.toolbar)   Toolbar toolbar;

    private TextView tv_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_directory);
        ButterKnife.bind(this);
        //设置Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        httpUrl=getIntent().getStringExtra("href");
        dataList=new ArrayList<HashMap>();
        setRecycleView();
        Thread thread=new GetData();
        thread.start();
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
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);
        //add a FooterView
        CommonFooter footerView = new CommonFooter(this, R.layout.layout_empty);
        mLRecyclerViewAdapter.addFooterView(footerView);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                User user = BmobUser.getCurrentUser(User.class);
                if (user == null) {
                    Util.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "亲，登录后才可查看内容哦", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                } else {

                    if (position > 10) {


                        //判断用户的会员状态
                        BmobQuery<Member> query = new BmobQuery<Member>();
                        query.addWhereEqualTo("user", user);
                        query.findObjects(new FindListener<Member>() {
                            @Override
                            public void done(List<Member> list, BmobException e) {
                                if (e == null) {
                                    if (list.size() <= 0) {   //不是会员，提示购买会员
                                        AlertDialog dlg = new AlertDialog.Builder(MagazineDirectoryActivity.this).setTitle("提示").setMessage("亲，该部分内容会员才可观看")
                                                .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("取消", null).create();
                                        dlg.setCanceledOnTouchOutside(false);
                                        dlg.show();
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
                                                AlertDialog dlg = new AlertDialog.Builder(MagazineDirectoryActivity.this).setTitle("提示").setMessage("亲，该部分内容为会员专项，你的会员已过期！")
                                                        .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(MagazineDirectoryActivity.this, MemberActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .setNegativeButton("取消", null).create();
                                                dlg.setCanceledOnTouchOutside(false);
                                                dlg.show();

                                            } else {  //没有过期，正常查看
                                                String type = dataList.get(position).get("type").toString();
                                                if ("item".equals(type)) {
                                                    String url = dataList.get(position).get("href").toString();
                                                    //跳转至内容显示Activity
                                                    Intent intent = new Intent(MagazineDirectoryActivity.this, MagazineContentActivity.class);
                                                    intent.putExtra("url", MAGAZINE_URL + url);
                                                    startActivity(intent);
                                                }
                                            }
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                    }
                                } else {
                                    Util.toastMessage(MagazineDirectoryActivity.this, "查询会员状态出错：" + e.getMessage());
                                }
                            }
                        });


                    }else{   //前10条随意查看
                        String type = dataList.get(position).get("type").toString();
                        if ("item".equals(type)) {
                            String url = dataList.get(position).get("href").toString();
                            //跳转至内容显示Activity
                            Intent intent = new Intent(MagazineDirectoryActivity.this, MagazineContentActivity.class);
                            intent.putExtra("url", MAGAZINE_URL + url);
                            startActivity(intent);
                        }

                    }

                }

            }
        });

    }




    class GetData extends Thread {
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element introDiv=docHtml.getElementsByClass("magBox1").first();
                magazineTime=introDiv.getElementsByTag("p").first().text();
                coverImageUrl=introDiv.getElementsByTag("a").first().attr("href");
                magazineIntro=introDiv.getElementsByClass("rec").first().getElementsByTag("p").first().text();
                magazineTitle=docHtml.getElementsByTag("h3").first().text();
                magazineHistoryHref=docHtml.getElementsByClass("btn_history act_history").first().attr("href");   //没有前缀

                Element dirDiv=docHtml.getElementById("dirList");  //目录div
                Elements dirElements=dirDiv.getElementsByClass("dirItem02");
                for (Element dirElement : dirElements){
                    String subTitle=dirElement.getElementsByTag("h5").first().text();
                    HashMap titleMap=new HashMap<String,String>();
                    titleMap.put("type","title");
                    titleMap.put("text",subTitle);
                    dataList.add(titleMap);

                    Elements lis=dirElement.getElementsByTag("ul").first().getElementsByTag("li");
                    for (Element li : lis){
                        String text=li.getElementsByTag("a").first().text();
                        String href=li.getElementsByTag("a").first().attr("href");
                        HashMap dirMap=new HashMap<String,String>();
                        dirMap.put("type","item");
                        dirMap.put("text",text);
                        dirMap.put("href",href);
                        dataList.add(dirMap);
                    }
                }

                handler.sendEmptyMessage(100);


            } catch (IOException e) {
                e.printStackTrace();
                Util.toastMessage(MagazineDirectoryActivity.this,e.getMessage());
            }


        }
    }

     Handler handler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
              super.handleMessage(msg);
              if (msg.what==100){
                  tv_title.setText(magazineTitle);
                  tv_time.setText(magazineTime+"目录");
                  mLRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为toolbar创建Menu
        getMenuInflater().inflate(R.menu.menu_magazine, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId()==R.id.addShelf){
           // Util.toastMessage(MagazineDirectoryActivity.this,"加入书架");

            User user= BmobUser.getCurrentUser(User.class);
            if (user == null) {   //未登录
                Util.showConfirmCancelDialog(MagazineDirectoryActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(MagazineDirectoryActivity.this, LoginActivity.class);
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
                                Util.toastMessage(MagazineDirectoryActivity.this,e.getMessage());
                            }
                    }
                });
            }


        }
       /* if (item.getItemId()==R.id.scanHistory){
           // Util.toastMessage(MagazineDirectoryActivity.this,"浏览往期");
            Intent intent=new Intent(this,MagazineHistoryActivity.class);
            intent.putExtra("historyUrl",magazineHistoryHref);
            intent.putExtra("title",magazineTitle);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.scanIntro){
            Util.toastMessage(MagazineDirectoryActivity.this,"期刊介绍");
        }*/
        return true;
    }

}
