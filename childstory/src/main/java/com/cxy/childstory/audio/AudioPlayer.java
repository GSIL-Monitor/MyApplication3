package com.cxy.childstory.audio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.cxy.childstory.R;
import com.cxy.childstory.model.Story;
import com.cxy.childstory.service.PlayAudioService;



public class AudioPlayer {

    private static AudioPlayer audioPlayer=null;
    private static Context mContext;
    private static PlayAudioService playAudioService;
    private static boolean mBound=false;

    private static final String LOG_TAG="AudioPlayer";
    private static final String ACTION_AUDIO="com.cxy.child.story.action.audio";
    private static final String CLICK_TYPE_TAG="TYPE";
    private static final String TYPE_PLAY="PALY";
    private static final String TYPE_STOP="STOP";
    private static final String TYPE_ClOSE="CLOSE";
    private static  int isPlaying=1;
    private  static  final int NOTIFICTION_ID=158;

    private static Story mStory=new Story();
 //   private static AudioBroadcastReceiver broadcastReceiver;


    public static AudioPlayer getInstance(Context context){
        if (audioPlayer==null){
            audioPlayer=new AudioPlayer(context);
        }
        return audioPlayer;
    }

    private AudioPlayer(Context context){
        Log.i(LOG_TAG,"调用构造方法");
        mContext=context;

    }
    //开始播放
    public void playAudio(Story story){
        isPlaying=1;
        mStory=story;
        mStory.setImagePath(story.getImagePath());
        mStory.setTitle(story.getTitle());
        mStory.setAudioPaths(story.getAudioPaths());
        if (playAudioService==null){
            Intent intent=new Intent(mContext,PlayAudioService.class);
            mContext.startService(intent);
            mContext.bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
            initNotification();

        }else{
            if (mBound){
                //TODO:更新UI
                //设置歌曲名称
                if (!TextUtils.isEmpty(mStory.getTitle())){
                    contentView.setTextViewText(R.id.song_title,mStory.getTitle());
                    //隐藏播放按钮
                    contentView.setViewVisibility(R.id.iv_play, View.GONE);
                    //显示暂停按钮
                    contentView.setViewVisibility(R.id.iv_play_stop, View.VISIBLE);

                    notificationManager.notify(NOTIFICTION_ID, notification);
                //    sendReceiver();
                }
                playAudioService.playAudio(mStory.getAudioPaths());
            }
        }

    }

    //暂停播放
    public void pausePlay(){
        isPlaying=0;
        //隐藏暂停按钮
        contentView.setViewVisibility(R.id.iv_play_stop, View.GONE);
        //显示播放按钮
        contentView.setViewVisibility(R.id.iv_play, View.VISIBLE);
        notificationManager.notify(NOTIFICTION_ID, notification);
        playAudioService.pausePlayAudio();
      //  sendReceiver();
    }
   //继续播放
    public void continuePlay(){
        isPlaying=1;
        //隐藏播放按钮
        contentView.setViewVisibility(R.id.iv_play, View.GONE);
        //显示暂停按钮
        contentView.setViewVisibility(R.id.iv_play_stop, View.VISIBLE);
        notificationManager.notify(NOTIFICTION_ID, notification);
        playAudioService.continuePlayAudio();
      //  sendReceiver();
    }

  /*  public void sendReceiver(){
        Intent intent=new Intent(PlayerActivity.ACTION_PLAY_ACTIVITY);
        intent.putExtra("story",mStory);
        intent.putExtra("isPlaying",isPlaying);
        mContext.sendBroadcast(intent);
    }*/

    //清除通知栏
    public void clearNatifaction(){
        notificationManager.cancel(NOTIFICTION_ID);
        playAudioService.pausePlayAudio();
        isPlaying=0;
    }

    private boolean unBind=false;
    public void unBindService(){
        if (mBound && !unBind){
            mContext.unbindService(mConnection);
            unBind=true;
        }


    }


    /*
     * 初始化通知栏
     */
     public static Notification notification;
     private static NotificationManager notificationManager;
     private static  RemoteViews contentView=null;
     private PendingIntent pendingIntent=null;
    private void initNotification() {
         notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


         contentView = new RemoteViews(mContext.getPackageName(),R.layout.include_play_audio);
        if (!TextUtils.isEmpty(mStory.getImagePath())){
            //展示网络图片
           // contentView.setImageViewResource(R.id.iv_story_cover,R.drawable.pic);//图片展示

        }
        //设置歌曲名称
        if (!TextUtils.isEmpty(mStory.getTitle())){
            contentView.setTextViewText(R.id.song_title,mStory.getTitle());
        }
        //隐藏播放按钮
        contentView.setViewVisibility(R.id.iv_play, View.GONE);
        //显示暂停按钮
        contentView.setViewVisibility(R.id.iv_play_stop, View.VISIBLE);

        //设置播放onclick事件
        Intent intentPlay = new Intent(mContext,AudioBroadcastReceiver.class);
        intentPlay.setAction(ACTION_AUDIO);
        intentPlay.putExtra(CLICK_TYPE_TAG,TYPE_PLAY);
        PendingIntent pIntentPlay = PendingIntent.getBroadcast(mContext, 2, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.iv_play, pIntentPlay);

        //设置stop事件
        Intent intentStop=new Intent(mContext,AudioBroadcastReceiver.class);
        intentStop.setAction(ACTION_AUDIO);
        intentStop.putExtra(CLICK_TYPE_TAG,TYPE_STOP);
        PendingIntent pIntentStop = PendingIntent.getBroadcast(mContext, 3, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.iv_play_stop, pIntentStop);

        //设置close事件
        Intent intentClose=new Intent(mContext,AudioBroadcastReceiver.class);
        intentClose.setAction(ACTION_AUDIO);
        intentClose.putExtra(CLICK_TYPE_TAG,TYPE_ClOSE);
        PendingIntent pIntentClose= PendingIntent.getBroadcast(mContext, 4, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.iv_close, pIntentClose);

     /*   Intent intent = new Intent(mContext, PlayerActivity.class);
        intent.putExtra("story",mStory);
        intent.putExtra("isPlaying",isPlaying);
        pendingIntent = PendingIntent.getActivity(mContext, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT );*/


         notification = new NotificationCompat.Builder(mContext,"audio")
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis())
                .setTicker("开始播放音频")
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = notification.FLAG_NO_CLEAR;//设置通知点击或滑动时不被清除


        notificationManager.notify(NOTIFICTION_ID, notification);//开启通知


    }








    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayAudioService.PlayAudioBinder binder = (PlayAudioService.PlayAudioBinder) service;
            playAudioService = binder.getService();
            mBound = true;
            playAudioService.playAudio(mStory.getAudioPaths());


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    //广播监听器
    public static class AudioBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
               if (intent.getAction().equals(ACTION_AUDIO)){
                   String clickType=intent.getStringExtra(CLICK_TYPE_TAG);

                   if (clickType.equals(TYPE_PLAY)){  //开始播放


                       audioPlayer.continuePlay();
                   }
                   if (clickType.equals(TYPE_STOP)){  //暂停播放

                       audioPlayer.pausePlay();
                   }
                   if (clickType.equals(TYPE_ClOSE)){
                       audioPlayer.clearNatifaction();
                   }

               }
        }
    }



}
