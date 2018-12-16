package com.cxy.childstory.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxy.childstory.MainActivity;
import com.cxy.childstory.R;
import com.cxy.childstory.base.BaseActivity;
import com.cxy.childstory.model.Story;
import com.cxy.childstory.utils.ServiceHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerActivity extends BaseActivity {

    @BindView(R.id.topbar)
    QMUITopBarLayout topBar;
    @BindView(R.id.song_cover)
    ImageView imageCover;
    @BindView(R.id.song_name)
    TextView tvName;
    @BindView(R.id.iv_play)
    ImageView imagePlay;
    @BindView(R.id.iv_play_stop)
    ImageView imageStop;
    private static Story mStory;
    private static int isPlaying=1;
    private AudioPlayer audioPlayer=null;
    public static final String ACTION_PLAY_ACTIVITY="com.cxy.childstory.audio.PlayerActivity";

    private MessageRecevier messageRecevier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        initTopBar();
        initView();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY_ACTIVITY);
        messageRecevier=new MessageRecevier();
        registerReceiver(messageRecevier,intentFilter);

    }

    private void initTopBar(){
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ServiceHelper.isProessRunning(getApplicationContext(),PlayerActivity.this.getPackageName()) ||
                        !ServiceHelper.isExsitMianActivity(PlayerActivity.this,MainActivity.class)){
                    //启动app
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageRecevier);
        //


    }

    @Override
    public void onBackPressed() {
        if (!ServiceHelper.isProessRunning(getApplicationContext(),this.getPackageName()) ||
                !ServiceHelper.isExsitMianActivity(this,MainActivity.class)){
            //启动app
            Intent intent = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
       // moveTaskToBack(true);
       finish();

    }

    private void  initView(){
        mContext=this;
        mStory= getIntent().getParcelableExtra("story");
        isPlaying=getIntent().getIntExtra("isPlaying",1);
        updateView();
        audioPlayer=AudioPlayer.getInstance(PlayerActivity.this);
    }

    private  void updateView(){
        if (!TextUtils.isEmpty(mStory.getTitle())){
            tvName.setText(mStory.getTitle());
        }
        if (!TextUtils.isEmpty(mStory.getImagePath())){
            Glide.with(mContext).load(mStory.getImagePath()).into(imageCover);
        }
        if (isPlaying==1){  //正在播放
            imageStop.setVisibility(View.VISIBLE);
            imagePlay.setVisibility(View.GONE);
        }else if (isPlaying==0){  //暂停
            imageStop.setVisibility(View.GONE);
            imagePlay.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.iv_play)
    public void playMusic(){
        audioPlayer.continuePlay();
        imageStop.setVisibility(View.VISIBLE);
        imagePlay.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_play_stop)
    public void pausePlay(){
        audioPlayer.pausePlay();
        imageStop.setVisibility(View.GONE);
        imagePlay.setVisibility(View.VISIBLE);
    }

    public  class MessageRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
             String action=intent.getAction();
             if (action.equals(ACTION_PLAY_ACTIVITY)){
                 mStory= intent.getParcelableExtra("story");
                 isPlaying=intent.getIntExtra("isPlaying",1);
                 updateView();
             }

        }
    }
}
