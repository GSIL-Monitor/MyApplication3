package com.cxy.childstory.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cxy.childstory.R;
import com.cxy.childstory.base.BaseActivity;
import com.cxy.childstory.model.Story;
import com.cxy.childstory.model.StorySection;
import com.cxy.childstory.service.PlayAudioService;
import com.qmuiteam.qmui.widget.QMUIAppBarLayout;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoryDetailActivity extends BaseActivity {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.iv_story)
    ImageView storyImage;
    @BindView(R.id.tv_summary)
    TextView storySummary;
    @BindView(R.id.tv_content)
    TextView storyContent;
  /*  @BindView(R.id.fb_audio)
    FloatingActionButton playFb;*/
    @BindView(R.id.groupListView)
    QMUIGroupListView  mGroupListView;

    @BindView(R.id.play_audio_view)
    View playAutoView;
    @BindView(R.id.iv_play)
    ImageView imagePlay;
    @BindView(R.id.iv_play_list)
    ImageView imagePlayList;
    @BindView(R.id.iv_play_last)
    ImageView imagePlayLast;
    @BindView(R.id.iv_play_next)
    ImageView imagePlayNext;
    @BindView(R.id.iv_play_stop)
    ImageView imagePlayStop;

    private PlayAudioService playAudioService;
    private boolean mBound = false;
    private List<String> audioList;


    private Story story=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        ButterKnife.bind(this);
        story=getIntent().getParcelableExtra("story");
        initTopBar();
        initData();

    }

    private void  initTopBar(){
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        if (!TextUtils.isEmpty(story.getTitle())){
            mTopBar.setTitle(story.getTitle());
        }

    }
    private void initData(){
        if (!TextUtils.isEmpty(story.getSummary())){
            storySummary.setText(story.getSummary());
        }
        if (!TextUtils.isEmpty(story.getContent())){
            storyContent.setText(story.getContent());
        }

        if (!TextUtils.isEmpty(story.getImagePath())){
            Glide.with(this).load(story.getImagePath()).into(storyImage);
        }
        audioList=story.getAudioPaths();
        if (audioList==null || audioList.size()<=0){
            //隐藏播放layout
            playAutoView.setVisibility(View.GONE);
        }

        List<String> sectionList=story.getChildStoryIds();
        if (sectionList!=null && sectionList.size()>0){
            String desciption="<<"+story.getTitle()+">>"+" 列表";
            QMUIGroupListView.Section section=QMUIGroupListView.newSection(this).setTitle(desciption);
            for (int i=0;i<sectionList.size();i++){
                String title=story.getTitle()+"：第"+(i+1)+"章";
                final QMUICommonListItemView normalItem = mGroupListView.createItemView(null,
                         title,
                        null,
                        QMUICommonListItemView.HORIZONTAL,
                        QMUICommonListItemView.ACCESSORY_TYPE_NONE);
                normalItem.setOrientation(QMUICommonListItemView.VERTICAL);

                section.addItemView(normalItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(mContext,normalItem.getText(),Toast.LENGTH_SHORT).show();

                    }
                });

            }
            section.addTo(mGroupListView);
        }
    }

    boolean isFirstPlay=true;
    //播放
    @OnClick(R.id.iv_play)
    public void play(View v){
        Toast.makeText(StoryDetailActivity.this,"开始播放音频",Toast.LENGTH_LONG).show();
        if (isFirstPlay){
            Intent intent = new Intent(this, PlayAudioService.class);
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }else{
            if (mBound){
                playAudioService.continuePlayAudio();
            }
        }

        imagePlay.setVisibility(View.GONE);
        imagePlayStop.setVisibility(View.VISIBLE);

       /* if (playAudioService==null){
            Intent intent = new Intent(this, PlayAudioService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        if (!isPlay){   //状态是暂停则播放
            if (mBound){
              imagePlay.setImageResource(R.drawable.ic_play);
              playAudioService.playAudio(audioList);
            }
        }
        if (isPlay){  //状态是播放则暂停
            if (mBound){
                imagePlay.setImageResource(R.drawable.ic_play_list_disable);
                playAudioService.stopPlayAudio();
            }
        }*/
    }

    //点击暂停播放按钮
    @OnClick(R.id.iv_play_stop)
    public void stopPlay(){
        if (mBound){
            playAudioService.stopPlayAudio();
            //隐藏暂停播放
            imagePlayStop.setVisibility(View.GONE);
            //显示播放按钮
            imagePlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayAudioService.PlayAudioBinder binder = (PlayAudioService.PlayAudioBinder) service;
            playAudioService = binder.getService();
            mBound = true;
            isFirstPlay=false;
            //绑定后播放音频

            playAudioService.playAudio(audioList);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
