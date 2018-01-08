package com.cxy.yuwen.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.tool.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.xiaomi.ad.common.SdkConfig.getContext;

public class MagazineDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.add_shelf)
    TextView tv_addShelf;
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
    private Bitmap bookCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_detail);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //获取屏幕宽度
        WindowManager m = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels/2;
        int height=width+width/3;

        //设置Imageview宽度和高度
        ViewGroup.LayoutParams layoutParams = im_cover.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        im_cover.setLayoutParams(layoutParams);

        httpUrl = getIntent().getStringExtra("href");
        Thread thread=new GetData();
        thread.start();


    }
    //加入书架
    @OnClick(R.id.add_shelf)
    public void addShelf()
    {
        User user= BmobUser.getCurrentUser(User.class);
        if (user == null) {   //未登录
            Util.showConfirmCancelDialog(MagazineDetailActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
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
                        Util.toastMessage(MagazineDetailActivity.this,e.getMessage());
                    }
                }
            });
        }

    }
    //开始阅读
    @OnClick(R.id.start_read)
    public void  startRead(){
        Intent intent=new Intent(MagazineDetailActivity.this,MagazineDirectoryActivity.class);
        intent.putExtra("href",httpUrl);
        startActivity(intent);
    }
    //浏览往期
    @OnClick(R.id.watch_history)
   public void watchHistory(){
        // Util.toastMessage(MagazineDirectoryActivity.this,"浏览往期");
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
                bookCover=Util.getbitmap(coverImageUrl);
                handler.sendEmptyMessage(100);
            } catch (Exception e) {
                e.printStackTrace();
                Util.toastMessage(MagazineDetailActivity.this,"亲，出错了，请稍候重试");
            }
        }

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==100){
                tv_title.setText(magazineTitle);
                tv_time.setText(magazineTime);
                im_cover.setImageBitmap(bookCover);
                tv_intro.setText(magazineIntro);
            }
        }
    };

}

