package com.qq.e.union.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;


public class InterstitialADActivity extends Activity implements OnClickListener {

  InterstitialAD iad;
  String posId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interstitial_ad);
    ((EditText) findViewById(R.id.posId)).setText(Constants.InterteristalPosID);
    this.findViewById(R.id.showIAD).setOnClickListener(this);
    this.findViewById(R.id.showIADAsPPW).setOnClickListener(this);
    this.findViewById(R.id.closePPWIAD).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.showIAD:
        showAD();
        break;
      case R.id.showIADAsPPW:
        showAsPopup();
        break;
      case R.id.closePPWIAD:
        closeAsPopup();
        break;
      default:
        break;
    }
  }

  private InterstitialAD getIAD() {
    String posId = getPosID();
    if (iad != null && this.posId.equals(posId)) {
      return iad;
    }
    this.posId = posId;
    if (this.iad != null) {
      iad.closePopupWindow();
      iad.destroy();
      iad = null;
    }
    if (iad == null) {
      iad = new InterstitialAD(this, Constants.APPID, posId);
    }
    return iad;
  }

  private void showAD() {
    getIAD().setADListener(new AbstractInterstitialADListener() {

      @Override
      public void onNoAD(AdError error) {
        Log.i(
            "AD_DEMO",
            String.format("LoadInterstitialAd Fail, error code: %d, error msg: %s",
                error.getErrorCode(), error.getErrorMsg()));
      }

      @Override
      public void onADReceive() {
          Log.i("AD_DEMO", "onADReceive");
        iad.show();
      }
    });
    iad.loadAD();
    DemoUtil.hideSoftInput(this);
  }

  private void showAsPopup() {
    getIAD().setADListener(new AbstractInterstitialADListener() {

      @Override
      public void onNoAD(AdError error) {
        Log.i(
            "AD_DEMO",
            String.format("LoadInterstitialAd Fail, error code: %d, error msg: %s",
                error.getErrorCode(), error.getErrorMsg()));
      }

      @Override
      public void onADReceive() {
        iad.showAsPopupWindow();
      }
    });
    iad.loadAD();
    DemoUtil.hideSoftInput(this);
  }

  private void closeAsPopup() {
    if (iad != null) {
      iad.closePopupWindow();
    }
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.InterteristalPosID : posId;
  }
}
