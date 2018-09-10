package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * Created by hechao on 2018/2/8.
 */

public class NativeExpressADActivity extends Activity {

  private static final String POS_ID = "pos_id";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_express_ad);
    /**
     * 如果选择支持视频的模版样式，请使用{@link Constants#NativeExpressSupportVideoPosID}
     */
    ((EditText) findViewById(R.id.posId)).setText(Constants.NativeExpressPosID);
  }

  /**
   * 如果选择支持视频的模版样式，请使用{@link Constants#NativeExpressSupportVideoPosID}
   */
  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.NativeExpressPosID : posId;
  }

  public void onNormalViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeExpressDemoActivity.class);
    intent.putExtra(POS_ID, getPosID());
    startActivity(intent);
  }

  public void onRecyclerViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeExpressRecyclerViewActivity.class);
    intent.putExtra(POS_ID, getPosID());
    startActivity(intent);
  }
}
