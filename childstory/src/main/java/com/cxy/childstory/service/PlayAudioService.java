package com.cxy.childstory.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

public class PlayAudioService extends Service {
    private final IBinder mBinder=new PlayAudioBinder();
    final MediaPlayer mediaPlayer=new MediaPlayer();
    private List<String> audioPaths;
    private int mCounter=0;

    @Override
    public void onCreate() {
       // mediaPlayer = new MediaPlayer();
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
    public void stopPlayAudio(){
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
       return  mBinder;
    }


    public class PlayAudioBinder extends Binder {
        public PlayAudioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayAudioService.this;
        }
    }

}
