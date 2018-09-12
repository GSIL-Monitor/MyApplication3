package com.qq.e.union.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class DemoListActivity extends Activity implements OnClickListener {

  private static final String TAG = DemoListActivity.class.getSimpleName();
  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (Build.VERSION.SDK_INT >= 19) {
      WebView.setWebContentsDebuggingEnabled(true);
    }
    ((EditText) findViewById(R.id.posId)).setText(Constants.SplashPosID);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Toast.makeText(this, "广点通，结盟而赢", Toast.LENGTH_LONG).show();
      return true;
    } else if (id == R.id.action_muid) {
      Intent intent = new Intent(this, DeviceInfoActivity.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bannerADButton:
        startActivity(new Intent(this, BannerActivity.class));
        break;
      case R.id.interstitialADButton:
        startActivity(new Intent(this, InterstitialADActivity.class));
        break;
      case R.id.nativeADButton:
        startActivity(new Intent(this, NativeADActivity.class));
        break;
      case R.id.nativeADButton_MP:
        startActivity(new Intent(this, MultiProcessNativeADActivity.class));
        break;
      case R.id.nativeVideoADButton:
        startActivity(new Intent(this, NativeVideoADActivity.class));
        break;
      case R.id.nativeExpressADButton:
        startActivity(new Intent(this, NativeExpressADActivity.class));
        break;
      case R.id.contentADButton:
        startActivity(new Intent(this, ContentADActivity.class));
        break;
      case R.id.splashADButton:
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("pos_id", getPosID());
        intent.putExtra("need_logo", needLogo());
        startActivity(intent);
        break;
      default:
        break;
    }
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.SplashPosID : posId;
  }

  private boolean needLogo() {
    return ((CheckBox) findViewById(R.id.checkbox)).isChecked();
  }
}
