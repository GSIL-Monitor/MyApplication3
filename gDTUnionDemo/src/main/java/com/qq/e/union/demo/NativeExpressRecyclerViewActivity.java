package com.qq.e.union.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.GDTLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 在消息流中接入原生模板广告的示例
 *
 * Created by noughtchen on 2017/4/26.
 */

public class NativeExpressRecyclerViewActivity extends Activity implements
    NativeExpressAD.NativeExpressADListener {

  private static final String TAG = NativeExpressRecyclerViewActivity.class.getSimpleName();
  public static final int MAX_ITEMS = 50;
  public static final int AD_COUNT = 3;    // 加载广告的条数，取值范围为[1, 10]
  public static int FIRST_AD_POSITION = 1; // 第一条广告的位置
  public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告

  private RecyclerView mRecyclerView;
  private LinearLayoutManager mLinearLayoutManager;
  private CustomAdapter mAdapter;
  private List<NormalItem> mNormalDataList = new ArrayList<NormalItem>();
  private NativeExpressAD mADManager;
  private List<NativeExpressADView> mAdViewList;
  private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_native_express_recycler_view);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mRecyclerView.setHasFixedSize(true);
    mLinearLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLinearLayoutManager);
    initData();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    // 使用完了每一个NativeExpressADView之后都要释放掉资源。
    if (mAdViewList != null) {
      for (NativeExpressADView view : mAdViewList) {
        view.destroy();
      }
    }
  }

  private void initData() {
    for (int i = 0; i < MAX_ITEMS; ++i) {
      mNormalDataList.add(new NormalItem("No." + i + " Normal Data"));
    }
    mAdapter = new CustomAdapter(mNormalDataList);
    mRecyclerView.setAdapter(mAdapter);
    initNativeExpressAD();
  }

  private String getPosId() {
    return getIntent().getStringExtra("pos_id");
  }

  /**
   * 如果选择支持视频的模版样式，请使用{@link Constants#NativeExpressSupportVideoPosID}
   */
  private void initNativeExpressAD() {
    ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
    mADManager = new NativeExpressAD(NativeExpressRecyclerViewActivity.this, adSize, Constants.APPID, getPosId(), this);
    mADManager.loadAD(AD_COUNT);
  }

  @Override
  public void onNoAD(AdError adError) {
    Log.i(
        TAG,
        String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
            adError.getErrorMsg()));
  }

  @Override
  public void onADLoaded(List<NativeExpressADView> adList) {
    Log.i(TAG, "onADLoaded: " + adList.size());
    mAdViewList = adList;
    for (int i = 0; i < mAdViewList.size(); i++) {
      int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
      if (position < mNormalDataList.size()) {
        NativeExpressADView view = mAdViewList.get(i);
        GDTLogger.i("ad load[" + i + "]: " + getAdInfo(view));
        if (view.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
          view.setMediaListener(mediaListener);
        }
        mAdViewPositionMap.put(view, position); // 把每个广告在列表中位置记录下来
        mAdapter.addADViewToPosition(position, mAdViewList.get(i));
      }
    }
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void onRenderFail(NativeExpressADView adView) {
    Log.i(TAG, "onRenderFail: " + adView.toString());
  }

  @Override
  public void onRenderSuccess(NativeExpressADView adView) {
    Log.i(TAG, "onRenderSuccess: " + adView.toString() + ", adInfo: " + getAdInfo(adView));
  }

  @Override
  public void onADExposure(NativeExpressADView adView) {
    Log.i(TAG, "onADExposure: " + adView.toString());
  }

  @Override
  public void onADClicked(NativeExpressADView adView) {
    Log.i(TAG, "onADClicked: " + adView.toString());
  }

  @Override
  public void onADClosed(NativeExpressADView adView) {
    Log.i(TAG, "onADClosed: " + adView.toString());
    if (mAdapter != null) {
      int removedPosition = mAdViewPositionMap.get(adView);
      mAdapter.removeADView(removedPosition, adView);
    }
  }

  @Override
  public void onADLeftApplication(NativeExpressADView adView) {
    Log.i(TAG, "onADLeftApplication: " + adView.toString());
  }

  @Override
  public void onADOpenOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADOpenOverlay: " + adView.toString());
  }

  @Override
  public void onADCloseOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADCloseOverlay");
  }

  public class NormalItem {
    private String title;

    public NormalItem(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
  }

  /** RecyclerView的Adapter */
  class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    static final int TYPE_DATA = 0;
    static final int TYPE_AD = 1;
    private List<Object> mData;

    public CustomAdapter(List list) {
      mData = list;
    }

    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position, NativeExpressADView adView) {
      if (position >= 0 && position < mData.size() && adView != null) {
        mData.add(position, adView);
      }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
      mData.remove(position);
      mAdapter.notifyItemRemoved(position); // position为adView在当前列表中的位置
      mAdapter.notifyItemRangeChanged(0, mData.size() - 1);
    }

    @Override
    public int getItemCount() {
      if (mData != null) {
        return mData.size();
      } else {
        return 0;
      }
    }

    @Override
    public int getItemViewType(int position) {
      return mData.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
      int type = getItemViewType(position);
      if (TYPE_AD == type) {
        final NativeExpressADView adView = (NativeExpressADView) mData.get(position);
        mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
        if (customViewHolder.container.getChildCount() > 0
            && customViewHolder.container.getChildAt(0) == adView) {
          return;
        }

        if (customViewHolder.container.getChildCount() > 0) {
          customViewHolder.container.removeAllViews();
        }

        if (adView.getParent() != null) {
          ((ViewGroup) adView.getParent()).removeView(adView);
        }

        customViewHolder.container.addView(adView);
        adView.render(); // 调用render方法后sdk才会开始展示广告
      } else {
        customViewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
      }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      int layoutId = (viewType == TYPE_AD) ? R.layout.item_express_ad : R.layout.item_data;
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
      CustomViewHolder viewHolder = new CustomViewHolder(view);
      return viewHolder;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
      public TextView title;
      public ViewGroup container;

      public CustomViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        container = (ViewGroup) view.findViewById(R.id.express_ad_container);
      }
    }
  }    //Adapter end

  private String getAdInfo(NativeExpressADView nativeExpressADView) {
    AdData adData = nativeExpressADView.getBoundData();
    if (adData != null) {
      StringBuilder infoBuilder = new StringBuilder();
      infoBuilder.append("title:").append(adData.getTitle()).append(",")
          .append("desc:").append(adData.getDesc()).append(",")
          .append("patternType:").append(adData.getAdPatternType());
      if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
        infoBuilder.append(", video info: ")
            .append(getVideoInfo(adData.getProperty(AdData.VideoPlayer.class)));
      }
      return infoBuilder.toString();
    }
    return null;
  }

  private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
    if (videoPlayer != null) {
      StringBuilder videoBuilder = new StringBuilder();
      videoBuilder.append("state:").append(videoPlayer.getVideoState()).append(",")
          .append("duration:").append(videoPlayer.getDuration()).append(",")
          .append("position:").append(videoPlayer.getCurrentPosition());
      return videoBuilder.toString();
    }
    return null;
  }

  private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
    @Override
    public void onVideoInit(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoInit: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoLoading(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoLoading: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
      Log.i(TAG, "onVideoReady: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoStart(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoStart: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoPause(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPause: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoComplete(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoComplete: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
      Log.i(TAG, "onVideoError");
    }

    @Override
    public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPageOpen");
    }

    @Override
    public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPageClose");
    }
  };
}
