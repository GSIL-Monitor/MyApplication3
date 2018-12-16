package com.cxy.childstory.activity;

import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cxy.childstory.MainActivity;
import com.cxy.childstory.R;
import com.cxy.childstory.audio.AudioPlayer;
import com.cxy.childstory.base.BaseActivity;
import com.cxy.childstory.model.Story;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoryDetailActivity extends BaseActivity  {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.iv_story)
    ImageView storyImage;
    @BindView(R.id.tv_summary)
    TextView storySummary;
    @BindView(R.id.tv_content)
    TextView storyContent;
    @BindView(R.id.fb_audio)
    FloatingActionButton playFb;
    @BindView(R.id.groupListView)
    QMUIGroupListView  mGroupListView;

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
            playFb.hide();
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


    AudioPlayer audioPlayer=null;
    //播放
    @OnClick(R.id.fb_audio)
    public void play(View v){
        Toast.makeText(StoryDetailActivity.this,"开始播放音频",Toast.LENGTH_LONG).show();

        audioPlayer=AudioPlayer.getInstance(getApplicationContext());
        Story audioStory=new Story();
        audioStory.setTitle(story.getTitle());
        audioStory.setAudioPaths(story.getAudioPaths());
        audioStory.setImagePath(story.getImagePath());

        audioPlayer.playAudio(audioStory);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if (audioPlayer!=null){
            audioPlayer.unBindService();
        }*/

    }





}
