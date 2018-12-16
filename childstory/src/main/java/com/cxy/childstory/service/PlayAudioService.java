package com.cxy.childstory.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class PlayAudioService extends Service {
    private final IBinder mBinder=new PlayAudioBinder();
    final MediaPlayer mediaPlayer=new MediaPlayer();
    private List<String> audioPaths;
    private int mCounter=0;
    
    private static final String LOG_TAG="PlayAudioService";
    



    @Override
    public void onCreate() {
       // mediaPlayer = new MediaPlayer();
        Log.i(LOG_TAG,"onCreate()---->"+Thread.currentThread().getName());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //  开始播放
                mediaPlayer.start();

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完成事件
                mCounter+=1;
                try {
                    if (mCounter<audioPaths.size()){
                        mediaPlayer.setDataSource(audioPaths.get(mCounter));
                        mediaPlayer.prepareAsync();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG,"onStartCommand()---->"+Thread.currentThread().getName());

        return super.onStartCommand(intent, flags, startId);
    }

    public void  playAudio(List<String> audioPathList){
        this.audioPaths=audioPathList;
        try {
            //清空资源
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioPaths.get(0));

            //3 准备播放
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void pausePlayAudio(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            }
    }
    public void continuePlayAudio(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG,"onBind()---->"+Thread.currentThread().getName());
       return  mBinder;
    }


    public class PlayAudioBinder extends Binder {
        public PlayAudioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayAudioService.this;
        }
    }

}
