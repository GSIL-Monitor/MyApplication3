package com.qq.e.union.demo;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.ContentAdType;
import com.qq.e.ads.contentad.ContentAD;
import com.qq.e.ads.contentad.ContentAdData;
import com.qq.e.ads.contentad.ContentData;
import com.qq.e.ads.contentad.ContentType;
import com.qq.e.ads.nativ.MediaListener;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeMediaADData;
import com.qq.e.comm.util.AdError;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by leilaliu on 2017/5/15.
 */
public class ContentADActivity extends Activity implements ContentAD.ContentADListener {
  private List<ContentAdData> contentAdDataList;
  private ContentAD contentAD;
  ListView listView;
  Button loadAd;
  Button showAd;
  View contentADContainer;
  String posId;
  // 适配器
  private MyAdapter myAdapter = null;
  private static final String TAG = ContentADActivity.class.getSimpleName();
  private int mScrollState;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gdtcontentad_demo);
    ((EditText) findViewById(R.id.posId)).setText(Constants.ContentADPosID);
    loadAd = (Button) findViewById(R.id.loadContentAD);
    showAd = (Button) findViewById(R.id.showContentAD);
    contentADContainer = findViewById(R.id.contentADContainer);
    loadAd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadAD();
      }
    });
    showAd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAD();
      }
    });
    showAd.setEnabled(false);
    listView = (ListView) findViewById(R.id.list);
    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                           int totalItemCount) {
        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
          return;
        }

        if (myAdapter != null) {
          myAdapter.onScroll();
        }
      }
    });
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.ContentADPosID : posId;
  }

  public void loadAD() {
    contentADContainer.setVisibility(View.GONE);
    DemoUtil.hideSoftInput(this);
    showAd.setEnabled(false);
    String posId = getPosID();
    if (contentAD == null || !this.posId.equals(posId)) {
      this.posId = posId;
      this.contentAD = new ContentAD(this, Constants.APPID, posId, this);
    }
    int pageNumber = 1;
    int channel = 105;
    boolean isManualOperation = true;
    contentAD.loadAD(pageNumber, channel, isManualOperation);
  }

  /**
   * 展示内容+广告时，一定要先调用onExposured接口曝光广告，否则将无法调用onClicked点击接口
   */
  public void showAD() {
    this.myAdapter = new MyAdapter(this, contentAdDataList);
    this.listView.setAdapter(this.myAdapter);
    contentADContainer.setVisibility(View.VISIBLE);
  }

  @Override
  public void onContentADLoaded(List<ContentAdData> contentAdDatas) {
    if (contentAdDatas.size() > 0) {
      contentAdDataList = contentAdDatas;
      preLoadVideo();
      showAd.setEnabled(true);
      Toast.makeText(this, "内容+广告加载成功", Toast.LENGTH_LONG).show();
    } else {
      Log.i(TAG, "NOADReturn");
    }
  }

  @Override
  public void onNoContentAD(int errorCode) {
    Log.i(TAG, "ONNoAD:" + errorCode);
  }

  @Override
  public void onContentADStatusChanged(ContentAdData contentAdData) {
    if (contentAdData.getType() == ContentAdType.AD) {
      NativeMediaADData adData = (NativeMediaADData) contentAdData;
      myAdapter.updateBtnText(myAdapter.getPosition(adData), getADButtonText(adData));
    }

  }

  @Override
  public void onContentADError(ContentAdData data, int errorCode) {
    Log.i(TAG, "onContentADError:" + errorCode);
  }

  @Override
  public void onADVideoLoaded(ContentAdData contentAdData) {
    if (contentAdData.getType() == ContentAdType.AD) {
      NativeMediaADData adData = (NativeMediaADData) contentAdData;
      Log.i(TAG, adData.getTitle() + " ---> 视频素材加载完成"); // 仅仅是加载视频文件完成，如果没有绑定MediaView视频仍然不可以播放
    }
  }

  private void preLoadVideo() {
    if (contentAdDataList != null && !contentAdDataList.isEmpty()) {
      for (int i = 0; i < contentAdDataList.size(); i++) {
        ContentAdData ad = contentAdDataList.get(i);
        NativeMediaADData mediaADData = null;
        if (ad.getType() == ContentAdType.AD
            && (mediaADData = (NativeMediaADData) ad).isVideoAD()) {
          mediaADData.preLoadVideo(); // 加载结果在onADVideoLoaded回调中返回
        }
      }
    }
  }

  /**
   * App类广告安装、下载状态的更新（普链广告没有此状态，其值为-1） 返回的AppStatus含义如下： 0：未下载 1：已安装 2：已安装旧版本 4：下载中（可获取下载进度“0-100”）
   * 8：下载完成 16：下载失败
   */
  private String getADButtonText(NativeMediaADData adItem) {
    if (adItem == null) {
      return "……";
    }
    if (!adItem.isAPP()) {
      return "查看详情";
    }
    switch (adItem.getAPPStatus()) {
      case 0:
        return "点击下载";
      case 1:
        return "点击启动";
      case 2:
        return "点击更新";
      case 4:
        return adItem.getProgress() > 0 ? "下载中" + adItem.getProgress() + "%" : "下载中"; // 特别注意：当进度小于0时，不要使用进度来渲染界面
      case 8:
        return "下载完成";
      case 16:
        return "下载失败,点击重试";
      default:
        return "查看详情";
    }
  }

  public class MyAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;

    private List<ContentAdData> mData = null;// 要显示的数据

    protected AQuery $;

    private Map<Integer, ContentAdData> contentAdDataMap = new HashMap<Integer, ContentAdData>();

    public MyAdapter(Context context, List<ContentAdData> data) {
      this.mInflater = LayoutInflater.from(context);
      $ = new AQuery(context);
      this.mData = data;
    }

    @Override
    public int getItemViewType(int position) {

      return mData.get(position).getType().ordinal();
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getCount() {
      if (mData == null) {
        return 0;
      }
      return this.mData.size();
    }

    @Override
    public Object getItem(int i) {

      return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
      final ContentAdData contentAdData = mData.get(position);
      ViewHolder viewHolder = null;
      contentAdDataMap.put(position, contentAdData);
      if (convertView == null || convertView.getTag() == null) {
        viewHolder = new ViewHolder();
        if (contentAdData.getType() == ContentAdType.AD) {
          convertView = this.mInflater.inflate(R.layout.item_ad, null, false);
          viewHolder.mediaView = (MediaView) convertView.findViewById(R.id.gdt_media_view);
          viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.img_logo);
          viewHolder.imgPoster = (ImageView) convertView.findViewById(R.id.img_poster);
          viewHolder.textName = (TextView) convertView.findViewById(R.id.text_title);
          viewHolder.textDesc = (TextView) convertView.findViewById(R.id.text_desc);
          viewHolder.btnDownload = (Button) convertView.findViewById(R.id.btn_download);
          viewHolder.play = (Button) convertView.findViewById(R.id.btn_play);
        } else if (contentAdData.getType() == ContentAdType.INFORMATION) {
          convertView = this.mInflater.inflate(R.layout.contentlistitem, null, false);
          viewHolder.imgPoster = (ImageView) convertView.findViewById(R.id.img);
          viewHolder.textName = (TextView) convertView.findViewById(R.id.title);
          viewHolder.textDesc = (TextView) convertView.findViewById(R.id.from);
          viewHolder.btnDownload = (Button) convertView.findViewById(R.id.clickme);
        }
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      if (contentAdData.getType() == ContentAdType.AD) {
        final NativeMediaADData nativeADDataRef = (NativeMediaADData) contentAdData;
        $.id(viewHolder.imgLogo).image(nativeADDataRef.getIconUrl(), false, true);
        $.id(viewHolder.imgPoster).image(nativeADDataRef.getImgUrl(), false, true, 0, 0,
            new BitmapAjaxCallback() {
              @Override
              protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                // AQuery框架有一个问题，就是即使在图片加载完成之前将ImageView设置为了View.GONE，在图片加载完成后，这个ImageView会被重新设置为VIEW.VISIBLE。
                // 所以在这里需要判断一下，如果已经把ImageView设置为隐藏，开始播放视频了，就不要再显示广告的大图。开发者在用其他的图片加载框架时，也应该注意检查下是否有这个问题。
                if (iv.getVisibility() == View.VISIBLE) {
                  iv.setImageBitmap(bm);
                }
              }
            });
        $.id(viewHolder.textName).text(nativeADDataRef.getTitle());
        $.id(viewHolder.textDesc).text(nativeADDataRef.getDesc());
        $.id(viewHolder.btnDownload).text(getADButtonText(nativeADDataRef));

        viewHolder.mediaView.setVisibility(View.GONE);
        viewHolder.imgPoster.setVisibility(View.VISIBLE);
        if (nativeADDataRef.isVideoAD() && nativeADDataRef.isVideoLoaded()) {
          if (nativeADDataRef.isPlaying()) {
            viewHolder.mediaView.setVisibility(View.VISIBLE);
            viewHolder.imgPoster.setVisibility(View.GONE);
            viewHolder.play.setVisibility(View.GONE);
          } else {
            viewHolder.mediaView.setVisibility(View.VISIBLE);
            viewHolder.imgPoster.setVisibility(View.GONE);
            nativeADDataRef.bindView(viewHolder.mediaView, true); // 只有将MediaView和广告实例绑定之后，才能播放视频
            nativeADDataRef.play();
            /** 设置视频播放过程中的监听器 */
            nativeADDataRef.setMediaListener(new MediaListener() {

              /**
               * 视频播放器初始化完成，准备好可以播放了
               *
               * @param videoDuration 视频素材的总时长
               */
              @Override
              public void onVideoReady(long videoDuration) {
                Log.i(TAG, "onVideoReady, videoDuration = " + videoDuration);
              }

              /** 视频开始播放 */
              @Override
              public void onVideoStart() {
                Log.i(TAG, "onVideoStart");
              }

              /** 视频暂停 */
              @Override
              public void onVideoPause() {
                Log.i(TAG, "onVideoPause");
              }

              /** 视频自动播放结束，到达最后一帧 */
              @Override
              public void onVideoComplete() {
                Log.i(TAG, "onVideoComplete");
              }

              /** 视频播放时出现错误 */
              @Override
              public void onVideoError(AdError error) {
                Log.i(TAG, "onVideoError, errorCode: " + error.getErrorCode());
              }

              /** SDK内置的播放器控制条中的重播按钮被点击 */
              @Override
              public void onReplayButtonClicked() {
                Log.i(TAG, "onReplayButtonClicked");
              }

              /**
               * SDK内置的播放器控制条中的下载/查看详情按钮被点击 注意:
               * 这里是指UI中的按钮被点击了，而广告的点击事件回调是在onADClicked中，开发者如需统计点击只需要在onADClicked回调中进行一次统计即可。
               */
              @Override
              public void onADButtonClicked() {
                Log.i(TAG, "onADButtonClicked");
              }

              /** SDK内置的全屏和非全屏切换回调，进入全屏时inFullScreen为true，退出全屏时inFullScreen为false */
              @Override
              public void onFullScreenChanged(boolean inFullScreen) {
                Log.i(TAG, "onFullScreenChanged, inFullScreen = " + inFullScreen);

                // 原生视频广告默认静音播放，进入到全屏后建议开发者可以设置为有声播放
                if (inFullScreen) {
                  nativeADDataRef.setVolumeOn(true);
                } else {
                  nativeADDataRef.setVolumeOn(false);
                }
              }
            });
          }
        }

        nativeADDataRef.onExposured(convertView.findViewById(R.id.ad_info)); // 需要先调用曝光接口
        viewHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            nativeADDataRef.onClicked(v);
          }
        });

      } else if (contentAdData.getType() == ContentAdType.INFORMATION) {

        final ContentData contentData = (ContentData) contentAdData;
        ViewGroup.LayoutParams imgLayout = viewHolder.imgPoster.getLayoutParams();
        DisplayMetrics dm = convertView.getContext().getResources().getDisplayMetrics();
        float den = dm.density;
        int realWidth = (int) (dm.widthPixels - 10 * den);
        if (contentData.isBigPic()) {
          imgLayout.height = realWidth * 296 / 592;
        } else {
          imgLayout.height = realWidth * 144 / 192;
        }
        viewHolder.imgPoster.setLayoutParams(imgLayout);
        $.id(viewHolder.imgPoster).image(contentData.getImages().get(0), false, true);
        $.id(viewHolder.textName).text(contentData.getTitle());
        if (contentData.getContentType() == ContentType.ARTICLE) {
          $.id(viewHolder.textDesc)
              .text((contentData.getFrom() + "    文章发表时间：" + contentData.getElapseTime()));
          $.id(viewHolder.btnDownload).text("查看文章");
        } else if (contentData.getContentType() == ContentType.VIDEO) {
          $.id(viewHolder.textDesc)
              .text((contentData.getFrom() + "    标签：" + contentData.getLabel()));
          $.id(viewHolder.btnDownload).text("查看视频");
        }

        contentData.onExpouse(convertView.findViewById(R.id.contentContainer)); // 需要先调用曝光接口
        viewHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            contentData.onClick(v);
          }
        });
      }
      return convertView;
    }

    public void updateBtnText(int position, String txt) {
      int firstVisiblePosition = listView.getFirstVisiblePosition();
      int lastVisiblePosition = listView.getLastVisiblePosition();
      if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
        View view = listView.getChildAt(position - firstVisiblePosition);
        Button btn = (Button) view.findViewById(R.id.btn_download);
        btn.setText(txt);
      }
    }

    public int getPosition(ContentAdData contentAdData) {
      Set<Integer> kset = contentAdDataMap.keySet();
      for (Integer ks : kset) {
        if (contentAdData.equals(contentAdDataMap.get(ks))) {
          return ks;
        }
      }
      return -1;
    }

    public void resumeVideo() {
      int first = listView.getFirstVisiblePosition();
      int last = listView.getLastVisiblePosition();
      for (int i = first; i <= last; i++) {
        if (isAd(i) && ((NativeMediaADData) mData.get(i)).isVideoAD()) {
          ((NativeMediaADData) mData.get(i)).resume();
        }
      }
    }

    public void onScroll() {
      int first = listView.getFirstVisiblePosition();
      int last = listView.getLastVisiblePosition();
      for (int i = first; i <= last; i++) {
        if (isAd(i) && ((NativeMediaADData) mData.get(i)).isVideoAD()
                && ((NativeMediaADData) mData.get(i)).isVideoLoaded()) {
          ((NativeMediaADData) mData.get(i)).onScroll(i, listView);
        }
      }
    }

    public void stopVideo() {
      int first = listView.getFirstVisiblePosition();
      int last = listView.getLastVisiblePosition();

      for (int i = first; i <= last; i++) {
        if (isAd(i) && ((NativeMediaADData) mData.get(i)).isVideoAD()) {
          ((NativeMediaADData) mData.get(i)).stop(); // i 为当前这条广告在CustomAdapter数据集中的位置
        }
      }
    }

    private boolean isAd(int position){
      return mData.get(position).getType() == ContentAdType.AD;
    }

    public void destroyVideo() {
      if (mData != null) {
        for (int i = 0; i < mData.size(); ++i) {
          if(isAd(i)){
            ((NativeMediaADData)mData.get(i)).destroy();
          }
        }
      }
    }
  }

  public final class ViewHolder {
    ImageView imgLogo;
    ImageView imgPoster;
    TextView textName;
    TextView textDesc;
    Button btnDownload, play;;
    MediaView mediaView;
  }

  @Override
  protected void onResume() {
    Log.i(TAG, "onResume");
    if (myAdapter != null) {
      myAdapter.resumeVideo();
    }
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    if (myAdapter != null) {
      myAdapter.stopVideo();
    }
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    if (myAdapter != null) {
      myAdapter.destroyVideo();
    }
    super.onDestroy();
  }
}
