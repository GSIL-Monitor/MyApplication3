package com.cxy.magazine.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.carrier.CarrierService;
import android.support.v7.widget.Toolbar;
import android.util.AndroidException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.ArticleRecommBean;
import com.cxy.magazine.bmobBean.CollectBean;
import com.cxy.magazine.bmobBean.RecommBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.jsInterface.JavascriptInterface;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MagazineContentActivity extends BasicActivity {

    private String httpUrl = "";
    private WebSettings mWebSettings;

    @BindView(R.id.wv_content)
    WebView mWebview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.containerAd)
    ViewGroup adContainer;
    @BindView(R.id.collectButton)
    ImageView collectButton;
    @BindView(R.id.recommTv)
    TextView recommTv;
    private User user;
    private String articleObjectId = null;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private String title = "", articleId = "", time = "";
    private Integer recommCount = 0;
    private String articleRecommId = null;
    private StringBuilder content = null;
    private static final String MAGAZINE_URL = "http://m.fx361.com";
    private String htmlStr = "<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "body{margin-left:15px;margin-right:12px;}h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:200%;margin-top:30px;} img{width:100%;}  .sj{font-size:15px;color:#a6a5a5;}"
            + "</style></head><body>";
    private boolean isCollect = false;    //是否收藏
    private String intentUrl = "";

    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;
    private String TAG = "tencentAd";
    private int checkedIndex = 1;
    //广告id数组
    private String[] adIds = {Constants.NativeExpressPosID1, Constants.NativeExpressPosID2, Constants.NativeExpressPosID3};
    @BindView(R.id.adView)
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_content);
        ButterKnife.bind(this);
        // getSupportActionBar().setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setWebView();
        intentUrl = getIntent().getStringExtra("url");
        httpUrl = (MAGAZINE_URL + intentUrl).replace("page", "news").replace("shtml", "html");    //(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");
        articleId = intentUrl.split("/")[4].split(".shtml")[0];

        content = new StringBuilder(htmlStr);
        //   mProgressDialog=ProgressDialog.show(this, null, "请稍后");
        Utils.showTipDialog(MagazineContentActivity.this, "加载中", QMUITipDialog.Builder.ICON_TYPE_LOADING);

        Thread getHtml = new GetHtml();
        getHtml.start();


        user = BmobUser.getCurrentUser(User.class);


    }

    @Override
    protected void onStart() {
        super.onStart();
        selectCollect();     //查询收藏情况


    }

    //查询该文章的收藏情况
    public void selectCollect() {
        if (user != null) {
            BmobQuery<CollectBean> collctQuery = new BmobQuery<CollectBean>();
            collctQuery.addWhereEqualTo("user", user);
            collctQuery.addWhereEqualTo("articleId", articleId);
            collctQuery.findObjects(new FindListener<CollectBean>() {
                @Override
                public void done(List<CollectBean> list, BmobException e) {
                    if (e == null && list != null) {
                        //    isFirst = true;
                        if (list.size() == 1) {   //查询到收藏数据
                            //  rbCollect.setChecked(true);
                            articleObjectId = list.get(0).getObjectId();
                            isCollect = true;
                            collectButton.setImageResource(R.drawable.ic_collect_selected);

                        }
                        //   isFirst = false;
                    } else {
                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }
    }

    public void selectRecomm() {
        BmobQuery<ArticleRecommBean> recommQuery = new BmobQuery<>();
        recommQuery.addWhereEqualTo("articleId", articleId);
        recommQuery.findObjects(new FindListener<ArticleRecommBean>() {
            @Override
            public void done(List<ArticleRecommBean> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        recommCount = list.get(0).getRecommCount();  //推荐次数
                        articleRecommId = list.get(0).getObjectId();
                        recommTv.setText("推荐" + recommCount);
                    } else {
                        //插入该文章的评论数据
                        ArticleRecommBean recommBean = new ArticleRecommBean();
                        recommBean.setArticleId(articleId);
                        recommBean.setArticleTitle(title);
                        recommBean.setArticleTime(time);
                        recommBean.setArticleUrl(intentUrl);
                        recommBean.setRecommCount(0);
                        recommBean.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if (e == null) {
                                    articleRecommId = objectId;
                                }
                            }
                        });  // end save
                    }
                }
            }

        });


    }

    @OnClick(R.id.collectView)
    public void collectClick() {

        if (user != null) {
            if (!isCollect) {        //没有收藏
                //Utils.toastMessage(MagazineContentActivity.this,"选中");
                //收藏
                CollectBean collectBean = new CollectBean();
                collectBean.setUser(user);
                collectBean.setArticleUrl(intentUrl);
                collectBean.setArticleTitle(title);
                collectBean.setArticleId(articleId);
                collectBean.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Utils.toastMessage(MagazineContentActivity.this, "收藏文章成功");
                            articleObjectId = objectId;
                            collectButton.setImageResource(R.drawable.ic_collect_selected);
                            isCollect = true;
                        } else {
                            Utils.toastMessage(MagazineContentActivity.this, "收藏文章失败:" + e.getMessage());

                        }
                    }
                });
            } else {      //已收藏，删除收藏
                //     Utils.toastMessage(MagazineContentActivity.this,"取消");
                //删除收藏
                final CollectBean collectBean = new CollectBean();
                collectBean.setObjectId(articleObjectId);
                collectBean.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Utils.toastMessage(MagazineContentActivity.this, "取消收藏成功");
                            //  rbCollect.setBackgroundResource(R.drawable.collect_no_selected);
                            collectButton.setImageResource(R.drawable.ic_collect_no_selected);
                            isCollect = false;
                        } else {
                            Utils.toastMessage(MagazineContentActivity.this, "取消收藏失败:" + e.getMessage());
                        }
                    }
                });

            }
        } else {
            Utils.toastMessage(MagazineContentActivity.this, "请先返回登录，再收藏");
        }


    }

    @OnClick(R.id.recommView)
    public void recommView() {
        //TODO:推荐
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("articleRecommId", articleRecommId);
        startActivity(intent);

    }


    private void refreshAdmob() {

/*

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
               Log.e("admob","广告加载失败");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
*/

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    public void setWebView() {
        mWebSettings = mWebview.getSettings();
        mWebSettings.setTextSize(WebSettings.TextSize.NORMAL);
        // 设置与Js交互的权限
        mWebSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //防止中文乱码
        mWebSettings.setDefaultTextEncodingName("UTF-8");


        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //这段js函数的功能就是注册监听，遍历所有的img标签，并添加onClick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
                mWebview.loadUrl("javascript:(function(){"
                        + "var objs = document.getElementsByTagName(\"img\"); "
                        + "for(var i=0;i<objs.length;i++)  " + "{"
                        + "    objs[i].onclick=function()  " + "    {  "
                        + "        window.imagelistner.openImage(this.src);  "
                        + "    }  " + "}" + "})()");
            }
        });
    }


    class GetHtml extends Thread {
        @Override
        public void run() {

            try {
                String html = OkHttpUtil.get(httpUrl);
                if (!Utils.isEmpty(html)) {
                    Document docHtml = Jsoup.parse(html);
                    Element mainDiv = docHtml.getElementsByClass("main").first();
                    title = mainDiv.getElementsByClass("bt").first().text();  //文章标题   h3
                    time = mainDiv.getElementsByClass("sj").first().text();
                    mainDiv.getElementsByTag("h3").get(1).remove();
                    mainDiv.getElementsByClass("others").first().remove();
                    content.append(mainDiv.html());
                    content.append("</body></html>");
                    uiHandler.sendEmptyMessage(100);
                }


            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);
            }
        }
    }

    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 100) {

                //  mProgressDialog.dismiss();
                Utils.dismissDialog();
                String[] imageUrls = Utils.returnImageUrlsFromHtml(content.toString());
                mWebview.addJavascriptInterface(new JavascriptInterface(MagazineContentActivity.this, imageUrls), "imagelistner");
                mWebview.loadData(content.toString(), "text/html; charset=UTF-8", null);
                //查询推荐情况
                selectRecomm();
                //TODO：设置Google广告
               refreshAdmob();
            }
            if (msg.what == 101) {
                Utils.dismissDialog();
                String error = "<h3>抱歉，该篇文章暂时无法阅读！<h3>";
                mWebview.loadData(error, "text/html; charset=UTF-8", null);
                //设置广告
                refreshAdmob();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_magazine_content, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        //设置字体大小
        if (item.getItemId() == R.id.fontSize) {

            setFontSize();
        }

        return true;
    }

    public void setFontSize() {
        final String[] items = new String[]{"小号字", "中号字(默认)", "大号字", "特大号字"};
        new QMUIDialog.CheckableDialogBuilder(MagazineContentActivity.this)
                .setCheckedIndex(checkedIndex)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //   Toast.makeText(MagazineContentActivity.this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                        //TODO:改变字体大小
                        switch (which) {
                            case 0:
                                mWebSettings.setTextSize(WebSettings.TextSize.SMALLER);
                                break;
                            case 1:
                                mWebSettings.setTextSize(WebSettings.TextSize.NORMAL);
                                break;
                            case 2:
                                mWebSettings.setTextSize(WebSettings.TextSize.LARGER);
                                break;
                            case 3:
                                mWebSettings.setTextSize(WebSettings.TextSize.LARGEST);
                                break;

                        }

                        checkedIndex = which;
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();


    }



    //销毁Webview
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWebview != null) {
            //  mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        Log.i(LOG_TAG, "MagazineContentActivity------->onDestroy");


        if (mAdView!=null){
            mAdView.destroy();
        }
    }


}
