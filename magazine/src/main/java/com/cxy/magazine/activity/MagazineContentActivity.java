package com.cxy.magazine.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.CollectBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MagazineContentActivity extends BasicActivity implements  NativeExpressAD.NativeExpressADListener{

    private String httpUrl="";
    private WebSettings mWebSettings;

    @BindView(R.id.wv_content)  WebView mWebview;
    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.containerAd) ViewGroup adContainer;
    @BindView(R.id.collectButton)
    ImageButton collectButton;
    private User user;
    private String articleObjectId=null;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private String title="",articleId="";
    private StringBuilder content=null;
    private static final String MAGAZINE_URL="http://m.fx361.com";
    private String htmlStr="<html><head><meta charset=\"utf-8\"><style type=\"text/css\">"
            + "body{margin-left:15px;margin-right:12px;}h3{font-size:22px;} p{font-size:18px;color:#373737;line-height:180%;margin-top:30px;} img{width:100%;}  .sj{font-size:15px;color:#a6a5a5;}"
            + "</style></head><body>";
    private boolean isCollect=false;    //是否收藏
    private String intentUrl="";

    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;
    private String TAG="tencentAd";
    private int checkedIndex = 1;

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
        intentUrl=getIntent().getStringExtra("url");
        httpUrl=(MAGAZINE_URL + intentUrl).replace("page","news").replace("shtml","html");    //(MAGAZINE_URL + url).replace("page","news").replace("shtml","html");
        articleId=intentUrl.split("/")[4].split(".shtml")[0];
      //  setWebView();
        content=new StringBuilder(htmlStr);
     //   mProgressDialog=ProgressDialog.show(this, null, "请稍后");
        Utils.showTipDialog(MagazineContentActivity.this,"加载中");
        Thread getHtml=new GetHtml();
        getHtml.start();

        user= BmobUser.getCurrentUser(User.class);

        selectCollect();     //查询收藏情况




    }

//查询该文章的收藏情况
public void selectCollect(){
    if (user!=null) {
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
                        isCollect=true;
                        collectButton.setImageResource(R.drawable.collect_selected);

                    }
                 //   isFirst = false;
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
}

@OnClick(R.id.collectButton)
public void collectClick(){

            if (user!=null) {
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
                             //   rbCollect.setBackgroundResource(R.drawable.collect_selected);
                                collectButton.setImageResource(R.drawable.collect_selected);
                                isCollect=true;
                            } else {
                                Utils.toastMessage(MagazineContentActivity.this, "收藏文章失败:" + e.getMessage());

                            }
                        }
                    });
                } else{      //已收藏，删除收藏
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
                                collectButton.setImageResource(R.drawable.collect_no_selected);
                                isCollect=false;
                            } else {
                                Utils.toastMessage(MagazineContentActivity.this, "取消收藏失败:" + e.getMessage());
                            }
                        }
                    });

                }
            }else{
                Utils.toastMessage(MagazineContentActivity.this,"请先返回登录，再收藏");
            }


}


    // 1.加载广告，先设置加载上下文环境和条件
    private void refreshAd() {
        nativeExpressAD = new NativeExpressAD(MagazineContentActivity.this,new ADSize(ADSize.FULL_WIDTH,ADSize.AUTO_HEIGHT),Constants.APPID, Constants.NativeExpressPosID, this);// 传入Activity
        // 注意：如果您在联盟平台上新建原生模板广告位时，选择了“是”支持视频，那么可以进行个性化设置（可选）
       /* nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); */
        nativeExpressAD.loadAD(1);
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TAG, String.format("onADError, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        Log.i(TAG, "onADLoaded: " + adList.size());
        // 释放前一个展示的NativeExpressADView的资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (adContainer.getVisibility() != View.VISIBLE) {
            adContainer.setVisibility(View.VISIBLE);
        }

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        // 广告可见才会产生曝光，否则将无法产生收益。
        adContainer.addView(nativeExpressADView);
        nativeExpressADView.render();
    }

    @Override
    public void onRenderFail(NativeExpressADView adView) {
        Log.i(TAG, "onRenderFail");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView adView) {
        Log.i(TAG, "onRenderSuccess");
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClicked(NativeExpressADView adView) {
        Log.i(TAG, "onADClicked");
    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        Log.i(TAG, "onADClosed");
        // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
        if (adContainer != null && adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView adView) {
        Log.i(TAG, "onADLeftApplication");
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView adView) {
        Log.i(TAG, "onADOpenOverlay");
    }

    @Override
    public void onADCloseOverlay(NativeExpressADView adView) {
        Log.i(TAG, "onADCloseOverlay");
    }


    public void setWebView(){
        mWebSettings = mWebview.getSettings();
        mWebSettings.setTextSize(WebSettings.TextSize.NORMAL);
       /* mWebSettings.setDefaultFontSize(22);
        //支持缩放，默认为true。
        mWebSettings .setSupportZoom(false);
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
         // 设置支持缩放
          mWebSettings.setSupportZoom(true);
       // 设置缩放工具的显示
        mWebSettings.setBuiltInZoomControls(true);
        //设置默认编码
        mWebSettings .setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        mWebSettings .setLoadsImagesAutomatically(true);
        mWebview.loadUrl(httpUrl);

        mWebview.setWebViewClient(new WebViewClient() {

            //设置不用系统浏览器打开,直接显示在当前Webview
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });*/



    }

    class GetHtml extends Thread{
        @Override
        public void run() {
            try {
                Document docHtml = Jsoup.connect(httpUrl).get();
                Element mainDiv=docHtml.getElementsByClass("main").first();
                title=mainDiv.getElementsByClass("bt").first().text();  //文章标题   h3
                mainDiv.getElementsByTag("h3").get(1).remove();
                mainDiv.getElementsByTag("ul").first().remove();
                content.append(mainDiv.html());
                content.append("</body></html>");



                uiHandler.sendEmptyMessage(100);
            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(101);
            }
        }
    }

    Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==100){

              //  mProgressDialog.dismiss();
                Utils.dismissDialog();
                mWebview.loadData(content.toString(), "text/html; charset=UTF-8", null);
                //设置广告
                refreshAd();
            }
            if (msg.what==101){
                Utils.dismissDialog();
                String error="<h3>抱歉，该篇文章暂时无法阅读！<h3>";
                mWebview.loadData(error, "text/html; charset=UTF-8", null);
                //设置广告
                refreshAd();
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
        if (item.getItemId()==R.id.fontSize){

            setFontSize();
        }

        return true;
    }

    public void setFontSize(){
        final String[] items = new String[]{"小号字", "中号字(默认)", "大号字","特大号字"};
        new QMUIDialog.CheckableDialogBuilder(MagazineContentActivity.this)
                .setCheckedIndex(checkedIndex)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     //   Toast.makeText(MagazineContentActivity.this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                        //TODO:改变字体大小
                        switch (which){
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

                        checkedIndex=which;
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();


    }


  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/

    //销毁Webview
    @Override
    protected void onDestroy() {
        /*if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }*/
        Log.i(LOG_TAG,"MagazineContentActivity------->onDestroy");
        super.onDestroy();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

    }


}
