package com.qq.e.union.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;


public class BannerActivity extends Activity implements OnClickListener {

  ViewGroup bannerContainer;
  BannerView bv;
  String posId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_banner);
    bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
    ((EditText) findViewById(R.id.posId)).setText(Constants.BannerPosID);
    this.findViewById(R.id.refreshBanner).setOnClickListener(this);
    this.findViewById(R.id.closeBanner).setOnClickListener(this);
    this.getBanner().loadAD();
  }

  private BannerView getBanner() {
    String posId = getPosID();
    if( this.bv != null && this.posId.equals(posId)) {
      return this.bv;
    }
    if(this.bv != null){
      bannerContainer.removeView(bv);
      bv.destroy();
    }
    this.posId = posId;
    this.bv = new BannerView(this, ADSize.BANNER, Constants.APPID,posId);
    // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
    // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
    bv.setRefresh(30);
    bv.setADListener(new AbstractBannerADListener() {

      @Override
      public void onNoAD(AdError error) {
        Log.i(
            "AD_DEMO",
            String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                error.getErrorMsg()));
      }

      @Override
      public void onADReceiv() {
        Log.i("AD_DEMO", "ONBannerReceive");
      }
    });
    bannerContainer.addView(bv);
    return this.bv;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.refreshBanner:
        doRefreshBanner();
        break;
      case R.id.closeBanner:
        doCloseBanner();
        break;
      default:
        break;
    }
  }

  private void doRefreshBanner() {
    DemoUtil.hideSoftInput(this);
    getBanner().loadAD();
  }

  private void doCloseBanner() {
    bannerContainer.removeAllViews();
    if (bv != null) {
      bv.destroy();
      bv = null;
    }
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.BannerPosID : posId;
  }
}
