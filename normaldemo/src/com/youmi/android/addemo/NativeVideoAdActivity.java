package com.youmi.android.addemo;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.video.VideoAdListener;
import net.youmi.android.normal.video.VideoAdManager;
import net.youmi.android.normal.video.VideoAdSettings;
import net.youmi.android.normal.video.VideoInfoViewBuilder;

/**
 * <p>原生视频广告演示窗口</p>
 * Created by Alian Lee on 2016-11-25 11:36.
 */
public class NativeVideoAdActivity extends BaseActivity {
	
	/**
	 * 原生视频广告控件容器
	 */
	private RelativeLayout mNativeVideoAdLayout;
	
	/**
	 * 视频信息栏容器
	 */
	private RelativeLayout mVideoInfoLayout;
	
	/**
	 * 展示原生视频广告按钮
	 */
	private Button mBtnShowNativeVideoAd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native_video);

		// 初始化视图
		initView();
		// 设置原生视频广告
		setupNativeVideoAd();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		// 原生视频广告控件容器
		mNativeVideoAdLayout = (RelativeLayout) findViewById(R.id.rl_native_video_ad);
		// 视频信息栏容器
		mVideoInfoLayout = (RelativeLayout) findViewById(R.id.rl_video_info);
		// 展示原生视频广告按钮
		mBtnShowNativeVideoAd = (Button) findViewById(R.id.btn_show_native_video);
	}
	
	/**
	 * 设置原生视频广告
	 */
	private void setupNativeVideoAd() {
		// 设置视频广告
		final VideoAdSettings videoAdSettings = new VideoAdSettings();

		//		// 只需要调用一次，由于在主页窗口中已经调用了一次，所以此处无需调用
		//		VideoAdManager.getInstance().requestVideoAd(mContext);
		
		// 设置信息流视图，将图标，标题，描述，下载按钮对应的ID传入
		final VideoInfoViewBuilder videoInfoViewBuilder =
				VideoAdManager.getInstance(mContext).getVideoInfoViewBuilder(mContext)
						.setRootContainer(mVideoInfoLayout).bindAppIconView(R.id.info_iv_icon)
						.bindAppNameView(R.id.info_tv_title).bindAppDescriptionView(R.id.info_tv_description)
						.bindDownloadButton(R.id.info_btn_download);

		mBtnShowNativeVideoAd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取原生视频控件
				View nativeVideoAdView = VideoAdManager.getInstance(mContext)
						.getNativeVideoAdView(mContext, videoAdSettings, new VideoAdListener() {
							@Override
							public void onPlayStarted() {
								logDebug("开始播放视频");
								// 由于多窗口模式下，屏幕较小，所以开始播放时先隐藏展示按钮
								if (Build.VERSION.SDK_INT >= 24) {
									if (isInMultiWindowMode()) {
										hideShowNativeVideoButton();
									}
								}
								// 展示视频信息流视图
								showVideoInfoLayout();
							}
							
							@Override
							public void onPlayInterrupted() {
								showShortToast("播放视频被中断");
								// 中断播放时恢复展示原生视频广告按钮
								showShowNativeVideoButton();
								// 隐藏视频信息流视图
								hideVideoInfoLayout();
								// 释放资源
								if (videoInfoViewBuilder != null) {
									videoInfoViewBuilder.release();
								}
								// 移除原生视频控件
								if (mNativeVideoAdLayout != null) {
									mNativeVideoAdLayout.removeAllViews();
									mNativeVideoAdLayout.setVisibility(View.GONE);
								}
							}
							
							@Override
							public void onPlayFailed(int errorCode) {
								logError("视频播放失败");
								switch (errorCode) {
								case ErrorCode.NON_NETWORK:
									showShortToast("网络异常");
									break;
								case ErrorCode.NON_AD:
									showShortToast("原生视频暂无广告");
									break;
								case ErrorCode.RESOURCE_NOT_READY:
									showShortToast("原生视频资源还没准备好");
									break;
								case ErrorCode.SHOW_INTERVAL_LIMITED:
									showShortToast("请勿频繁展示");
									break;
								case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
									showShortToast("原生视频控件处在不可见状态");
									break;
								default:
									logError("请稍后再试");
									break;
								}
							}
							
							@Override
							public void onPlayCompleted() {
								showShortToast("视频播放成功");
								// 播放完成时恢复展示原生视频广告按钮
								showShowNativeVideoButton();
								// 隐藏视频信息流视图
								hideVideoInfoLayout();
								// 释放资源
								if (videoInfoViewBuilder != null) {
									videoInfoViewBuilder.release();
								}
								// 移除原生视频控件
								if (mNativeVideoAdLayout != null) {
									mNativeVideoAdLayout.removeAllViews();
									mNativeVideoAdLayout.setVisibility(View.GONE);
								}
							}
							
						});
				if (mNativeVideoAdLayout != null) {
					final RelativeLayout.LayoutParams params =
							new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);
					if (nativeVideoAdView != null) {
						mNativeVideoAdLayout.removeAllViews();
						// 添加原生视频广告
						mNativeVideoAdLayout.addView(nativeVideoAdView, params);
						mNativeVideoAdLayout.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		//原生控件点击后退关闭
		if (mNativeVideoAdLayout != null && mNativeVideoAdLayout.getVisibility() != View.GONE) {
			mNativeVideoAdLayout.removeAllViews();
			mNativeVideoAdLayout.setVisibility(View.GONE);

			//隐藏视频信息流视图
			hideVideoInfoLayout();
			return;
		}
		super.onBackPressed();
	}

	//-----------------------必须调用以下全部生命周期方法-------------------------------

	@Override
	protected void onStart() {
		super.onStart();
		// 原生视频广告
		VideoAdManager.getInstance(mContext).onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 原生视频广告
		VideoAdManager.getInstance(mContext).onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// 原生视频广告
		VideoAdManager.getInstance(mContext).onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 原生视频广告
		VideoAdManager.getInstance(mContext).onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 原生视频广告
		VideoAdManager.getInstance(mContext).onDestroy();
	}

	//-----------------------必须调用以上全部生命周期方法-------------------------------

	/**
	 * 隐藏展示原生视频广告按钮
	 */
	private void hideShowNativeVideoButton() {
		if (mBtnShowNativeVideoAd != null && mBtnShowNativeVideoAd.getVisibility() != View.GONE) {
			mBtnShowNativeVideoAd.setVisibility(View.GONE);
		}
	}

	/**
	 * 展示展示原生视频广告按钮
	 */
	private void showShowNativeVideoButton() {
		if (mBtnShowNativeVideoAd != null && mBtnShowNativeVideoAd.getVisibility() != View.VISIBLE) {
			mBtnShowNativeVideoAd.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 展示视频信息流视图
	 */
	private void showVideoInfoLayout() {
		if (mVideoInfoLayout != null && mVideoInfoLayout.getVisibility() != View.VISIBLE) {
			mVideoInfoLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏视频信息流视图
	 */
	private void hideVideoInfoLayout() {
		if (mVideoInfoLayout != null && mVideoInfoLayout.getVisibility() != View.GONE) {
			mVideoInfoLayout.setVisibility(View.GONE);
		}
	}
}
