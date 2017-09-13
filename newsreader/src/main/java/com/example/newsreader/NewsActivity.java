package com.example.newsreader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.newsreader.bean.NewsBean;


public class NewsActivity extends AppCompatActivity {

    ActionBar actionBar = null;
    NewsBean news = null;
    private ShareActionProvider mShareActionProvider;
    private ScrollView scrollView;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        //接收intent传递过来的数据
        Intent intent = this.getIntent();
        news = (NewsBean) intent.getSerializableExtra("news");

        //setTitle(title);

        TextView titleView = (TextView) findViewById(R.id.news_title);
        TextView pubDateView = (TextView) findViewById(R.id.news_pubDate);
        ScrollView scrollView=(ScrollView)findViewById(R.id.myScrollView);
        final WebView webView = (WebView) findViewById(R.id.newsDetail);

        titleView.setText(news.title);

        pubDateView.setText("(发布日期：" + news.pubDate + ")");


        //WebView参数设置(是否支持多窗口，是否支持缩放)
        WebSettings settings = webView.getSettings();
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(false);
        settings.setDefaultFontSize(18);
        //加载显示新闻描述内容

        webView.loadDataWithBaseURL(null, news.content, "text/html", "utf-8", null);





        mDetector = new GestureDetectorCompat(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                if(e1.getRawX() - e2.getRawX() > 200){
//                    showNext();//向左滑动，显示图片列表
//                    return true;
//                }

                if (e2.getRawX() - e1.getRawX() > 230) {
                    finish();//向右滑动
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);

            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //TODOAuto-generatedmethodstub
        mDetector.onTouchEvent(ev); //让GestureDetector响应触碰事件
        super.dispatchTouchEvent(ev); //让Activity响应触碰事件
        return false;
        }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        scrollView.onTouchEvent(event); //让ScrollView响应触碰事件
//        return false;
//
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
       // mShareActionProvider = (ShareActionProvider) item.getActionProvider();
                mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, news.link);
                sendIntent.setType("text/plain");
                setShareIntent(sendIntent);

        // Return true to display menu
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_brower:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(news.link));
                startActivity(intent);
                break;
 //           case R.id.menu_item_share:
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, news.link);
//                sendIntent.setType("text/plain");
//                setShareIntent(sendIntent);
//                Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();

  //              break;

        }
        return super.onOptionsItemSelected(item);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
//            startActivity(shareIntent);
        }
    }
}
