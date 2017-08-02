package com.example.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

//import com.wandoujia.ads.sdk.Ads;



/**
 * Created by cxy on 2017/4/5.
 */

public class BasicActivity  extends AppCompatActivity {
    protected static final String TAG = "youmi";

    protected Context mContext;
   // protected ViewGroup container;
    private static final String APP_ID = "100050372";
    private static final String SECRET_KEY = "1f2a1939b151da03e65fadba5583b3c5";
    private static final String BANNER = "4a03523b19b56eee9e13d6b351dcc6c5";
    private static final String INTERSTITIAL = "3d9d48ed2b8302ab98a3bdbe7d8ecd77";
   // private static final String APP_WALL = "66caff24c98802b40dbb014bbf39f0be";


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
       // setupBannerAd();
    }*/


   /* *//**
     * 设置广告条广告
     *//*
    protected void setupBannerAd() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Ads.init(mContext, APP_ID, SECRET_KEY);
                    return true;
                } catch (Exception e) {
                    Log.e("ads-sample", "error", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {


                if (success) {
                    *//**
                     * pre load
                     *//*
                    Ads.preLoad(BANNER, Ads.AdFormat.banner);
                   // Ads.preLoad(INTERSTITIAL, Ads.AdFormat.interstitial);
                    //Ads.preLoad(APP_WALL, Ads.AdFormat.appwall);

                    *//**
                     * add ad views
                     *//*
                    View bannerView = Ads.createBannerView(mContext, BANNER);
                    container.addView(bannerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                   *//* Button btI = new Button(MainActivity.this);
                    btI.setText("interstitial");
                    btI.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Ads.showInterstitial(MainActivity.this, INTERSTITIAL);
                        }
                    });
                    container.addView(btI);

                    Button btW = new Button(MainActivity.this);
                    btW.setText("app wall");
                    btW.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Ads.showAppWall(MainActivity.this, APP_WALL);
                        }
                    });
                    container.addView(btW);*//*
                } else {
                    TextView errorMsg = new TextView(mContext);
                    errorMsg.setText("init failed");
                    container.addView(errorMsg);
                }
            }
        }.execute();

    }

    protected  void setInterstitital(){
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Ads.init(mContext, APP_ID, SECRET_KEY);
                    return true;
                } catch (Exception e) {
                    Log.e("ads-sample", "error", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {


                if (success) {
                    *//**
                     * pre load
                     *//*
                   // Ads.preLoad(BANNER, Ads.AdFormat.banner);
                     Ads.preLoad(INTERSTITIAL, Ads.AdFormat.interstitial);
                    //Ads.preLoad(APP_WALL, Ads.AdFormat.appwall);


                    Ads.showInterstitial((Activity)mContext, INTERSTITIAL);
                } else {
                   *//* TextView errorMsg = new TextView(mContext);
                    errorMsg.setText("init failed");
                    container.addView(errorMsg);*//*
                }
            }
        }.execute();

    }*/

}
