package com.cxy.magazine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.adapter.CollectAdapter;
import com.cxy.magazine.adapter.MessageAdapter;
import com.cxy.magazine.bmobBean.MsgNotification;
import com.cxy.magazine.bmobBean.MsgReadRecord;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class MessageActivity extends BasicActivity {

    @BindView(R.id.mRecycleView)
    LRecyclerView mLRecycleView;
    MessageAdapter msgAdapter=null;
    LRecyclerViewAdapter mLRecyclerAdapter=null;
    TextView tvFoot;

    private List<MsgNotification> msgList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("通知消息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        msgList=new ArrayList<MsgNotification>();

        setLRecycleView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLRecycleView();
    }

    public void setLRecycleView(){

        msgAdapter=new MessageAdapter(msgList,this);
        mLRecyclerAdapter=new LRecyclerViewAdapter(msgAdapter);
        mLRecycleView.setAdapter(mLRecyclerAdapter);

        mLRecycleView.setLayoutManager(new LinearLayoutManager(this));
        //  mLRecycleView.setPullRefreshEnabled(true);
        //禁用自动加载更多功能
        mLRecycleView.setLoadMoreEnabled(false);
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.thin_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mLRecycleView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mLRecycleView.setHasFixedSize(true);
        //添加foot
        SampleFooter footerView = new SampleFooter(this);
        tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);

        mLRecyclerAdapter.addFooterView(footerView);
        //下拉刷新
        mLRecycleView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllData();
            }
        });
        mLRecycleView.refresh();
        mLRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

               Intent intent=new Intent(MessageActivity.this,MsgDetailActivity.class);
               intent.putExtra("msg",msgList.get(position));
               startActivity(intent);
            }
        });
    }

    public void getAllData(){
        final User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<MsgNotification> query1=new BmobQuery<MsgNotification>();
        query1.addWhereEqualTo("user",user);

        BmobQuery<MsgNotification> query2=new BmobQuery<>();
        query2.addWhereEqualTo("msgType",2);

        List<BmobQuery<MsgNotification>> queries=new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<MsgNotification> mainQuery=new BmobQuery<>();
        mainQuery.or(queries);

        mainQuery.order("-createdAt").findObjects(new FindListener<MsgNotification>() {
            @Override
            public void done(final List<MsgNotification> list, BmobException e) {
              if (e==null){
                  msgList.clear();
                  if (list.size()>0){
                      msgList.addAll(list);
                      for (final MsgNotification msgNotification : list){
                          if (null != msgNotification.getMsgType() && msgNotification.getMsgType()==2){
                              BmobQuery<MsgReadRecord> recordQuery=new BmobQuery<>();
                              MsgNotification  newMsg=new MsgNotification();
                              recordQuery.addWhereEqualTo("msgNotification",msgNotification);
                              recordQuery.addWhereEqualTo("user",user);

                              recordQuery.count(MsgReadRecord.class, new CountListener() {
                                  @Override
                                  public void done(Integer integer, BmobException e) {
                                      if (e==null && integer>0){
                                          msgNotification.setRead(true);
                                      }else{
                                          msgNotification.setRead(false);
                                      }
                                    //  msgList.add(msgNotification);
                                    //  msgList.addAll(list);
                                      mLRecycleView.refreshComplete(1000);  //刷新完成
                                      mLRecyclerAdapter.notifyDataSetChanged();
                                  }
                              });

                          }
                         /* else{
                              msgList.add(msgNotification);
                          }*/
                      }
                      //重新排序
            /*          Collections.sort(msgList, new Comparator<MsgNotification>() {
                          @Override
                          public int compare(MsgNotification t1, MsgNotification t2) {
                              return t1.getCreatedAt().compareTo(t2.getCreatedAt());
                          }
                      });*/

                      mLRecycleView.refreshComplete(1000);  //刷新完成
                      mLRecyclerAdapter.notifyDataSetChanged();
                  }else{
                      tvFoot.setText("你暂时没有收到新消息通知！");
                  }

              }else{
                  Utils.toastMessage(MessageActivity.this,"出错了："+e.getMessage());
              }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
