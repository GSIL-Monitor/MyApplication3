package com.youmi.android.addemo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

/**
 * <p>原生插屏广告演示窗口</p>
 * Created by Alian Lee on 2016-11-25 11:12.
 */
public class NativeSpotAdActivity extends BaseActivity {

	/**
	 * 原生插屏广告控件容器
	 */
	private RelativeLayout mNativeSpotAdLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native_spot_ad);

		// 设置原生插屏广告
		setupNativeSpotAd();
	}

	/**
	 * 设置原生插屏广告
	 */
	public void setupNativeSpotAd() {
		mNativeSpotAdLayout = (RelativeLayout) findViewById(R.id.rl_native_spot_ad);

		// 设置插屏图片类型，默认竖图
		// 横图
		//		SpotManager.getInstance(mContext).setImageType(SpotManager
		// .IMAGE_TYPE_HORIZONTAL);
		// 竖图
		SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);

		// 设置动画类型，默认高级动画
		//		// 无动画
		//		SpotManager.getInstance(mContext).setAnimationType(SpotManager
		// .ANIMATION_TYPE_NONE);
		//		// 简单动画
		//		SpotManager.getInstance(mContext).setAnimationType(SpotManager
		// .ANIMATION_TYPE_SIMPLE);
		// 高级动画
		SpotManager.getInstance(mContext)
				.setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

		Button btnShowNativeSpotAd = (Button) findViewById(R.id.btn_show_native_spot_ad);
		btnShowNativeSpotAd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取原生插屏控件
				View nativeSpotView = SpotManager.getInstance(mContext)
						.getNativeSpot(mContext, new SpotListener() {

							@Override
							public void onShowSuccess() {
								logInfo("原生插屏展示成功");
							}

							@Override
							public void onShowFailed(int errorCode) {
								logError("原生插屏展示失败");
								switch (errorCode) {
								case ErrorCode.NON_NETWORK:
									showShortToast("网络异常");
									break;
								case ErrorCode.NON_AD:
									showShortToast("暂无原生插屏广告");
									break;
								case ErrorCode.RESOURCE_NOT_READY:
									showShortToast("原生插屏资源还没准备好");
									break;
								case ErrorCode.SHOW_INTERVAL_LIMITED:
									showShortToast("请勿频繁展示");
									break;
								case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
									showShortToast("请设置插屏为可见状态");
									break;
								default:
									showShortToast("请稍后再试");
									break;
								}
							}

							@Override
							public void onSpotClosed() {
								logDebug("原生插屏被关闭");
							}

							@Override
							public void onSpotClicked(boolean isWebPage) {
								logDebug("原生插屏被点击");
								logInfo("是否是网页广告？%s", isWebPage ? "是" : "不是");
							}
						});
				if (nativeSpotView != null) {
					RelativeLayout.LayoutParams layoutParams =
							new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
					if (mNativeSpotAdLayout != null) {
						mNativeSpotAdLayout.removeAllViews();
						// 添加原生插屏控件到容器中
						mNativeSpotAdLayout.addView(nativeSpotView, layoutParams);
						if (mNativeSpotAdLayout.getVisibility() != View.VISIBLE) {
							mNativeSpotAdLayout.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		//原生控件点击后退关闭
		if (mNativeSpotAdLayout != null && mNativeSpotAdLayout.getVisibility() != View.GONE) {
			mNativeSpotAdLayout.removeAllViews();
			mNativeSpotAdLayout.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();
	}
}
