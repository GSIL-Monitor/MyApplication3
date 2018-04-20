package com.cxy.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cxy.yuwen.Adapter.MessageAdapter;
import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.MsgNotification;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.tool.Util;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.view.CommonFooter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
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
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mLRecycleView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mLRecycleView.setHasFixedSize(true);
        //添加foot
        CommonFooter footerView = new CommonFooter(this, R.layout.layout_empty);
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
        User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<MsgNotification> query=new BmobQuery<MsgNotification>();
        query.addWhereEqualTo("user",user);
        query.findObjects(new FindListener<MsgNotification>() {
            @Override
            public void done(List<MsgNotification> list, BmobException e) {
                if (e==null){
                    msgList.clear();
                    if (list.size()>0){
                        msgList.addAll(list);
                    }else{
                        tvFoot.setText("你暂时没有收到新消息通知！");
                    }
                    mLRecycleView.refreshComplete(1000);  //刷新完成
                    mLRecyclerAdapter.notifyDataSetChanged();
                }else{
                    Util.toastMessage(MessageActivity.this,"出错了："+e.getMessage());
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

