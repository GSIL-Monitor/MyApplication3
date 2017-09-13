package com.xiaomi.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangzheyuan on 16-8-12.
 */
public class StandardNewsFeedListActivity extends Activity {
    private static final String TAG = StandardNewsFeedListActivity.class.getSimpleName();

    private final static String AD_POSITION_ID = "f0bf4e8cd6aa71607c9d366ab09b56f8";

    private ArrayList<Object> mDataList;
    private int[] mAdPositionList = {0, 12, 22};
    private LayoutInflater mInflater;
    private ArrayList<NativeAdInfoIndex> mStuffList;
    private StandardNewsFeedAd mStandardNewsFeedAd;
    private MyAdapter mMyAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.standard_news_feed_list_layout);
        mStuffList = new ArrayList<NativeAdInfoIndex>();
        mStandardNewsFeedAd = new StandardNewsFeedAd(this);
        mListView = (ListView) findViewById(R.id.adList);
        mMyAdapter = new MyAdapter();
        mDataList = new ArrayList<Object>();
        mInflater = LayoutInflater.from(this);
        for (int i = 0; i < 35; i++) {
            mDataList.add("ListView Item : " + i);
        }
        mListView.setAdapter(mMyAdapter);

        findViewById(R.id.fetchAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    mStuffList.clear();
                    mStandardNewsFeedAd.requestAd(AD_POSITION_ID, mAdPositionList.length, new NativeAdListener() {
                        @Override
                        public void onNativeInfoFail(AdError adError) {
                            Log.e(TAG, "onNativeInfoFail e : " + adError);
                        }

                        @Override
                        public void onNativeInfoSuccess(final List<NativeAdInfoIndex> list) {
                            Log.e(TAG, "onNativeInfoSuccess is " + list);
                            mStuffList.addAll(list);
                            int size = (mStuffList.size() <= mAdPositionList.length) ? mStuffList.size() : mAdPositionList.length;
                            for (int i = 0; i < size; i++) {
                                final int index = i;
                                final NativeAdInfoIndex adInfoResponse = mStuffList.get(index);
                                mStandardNewsFeedAd.buildViewAsync(adInfoResponse, mListView.getWidth(), new AdListener() {
                                    @Override
                                    public void onAdError(AdError adError) {
                                        Log.e(TAG, "onAdError : " + adError + " at index : " + index);
                                    }

                                    @Override
                                    public void onAdEvent(AdEvent adEvent) {
                                        if (adEvent.mType == AdEvent.TYPE_CLICK) {
                                            Log.d(TAG, "ad has been clicked at position: " + index);
                                        } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
//                                            Log.d(TAG, "x button has been clicked at position : " + index);
                                        } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                                            Log.d(TAG, "ad has been showed at position: " + index);
                                        }
                                    }

                                    @Override
                                    public void onAdLoaded() {

                                    }

                                    @Override
                                    public void onViewCreated(View view) {
                                        mMyAdapter.loadAdView(view, index);
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注意要在这里重设一遍adpater，以免事件处理出现问题
        mListView.setAdapter(mMyAdapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private boolean isAdPosition(int index) {
            for (int i = 0; i < mAdPositionList.length; i++) {
                if (mAdPositionList[i] == index) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (isAdPosition(position) && mDataList.get(position) instanceof ViewGroup) {
                return (View) mDataList.get(position);
            } else {
                TextView textView;
                if (convertView != null && convertView instanceof TextView) {
                    textView = (TextView) convertView;
                } else {
                    textView = (TextView) mInflater.inflate(R.layout.list_item_layout, null);
                }
                textView.setText((String) mDataList.get(position));
                return textView;
            }
        }

        public synchronized void loadAdView(View view, int index) {
            mDataList.remove(mAdPositionList[index]);
            this.notifyDataSetChanged();
            mDataList.add(mAdPositionList[index], view);
            this.notifyDataSetChanged();
        }
    }
}
