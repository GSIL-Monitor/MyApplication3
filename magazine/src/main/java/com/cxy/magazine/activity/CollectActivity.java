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
import com.cxy.magazine.bmobBean.CollectBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CollectActivity extends BasicActivity {

    @BindView(R.id.LRecycleView)
    LRecyclerView mLRecycleView;
    CollectAdapter collectAdapter=null;
    LRecyclerViewAdapter mLRecyclerAdapter=null;
    List<CollectBean> collectBeanList=null;
    TextView tvFoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("我的收藏");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collectBeanList=new ArrayList<CollectBean>();
        setmLRecycleView();
      //  getData();
    }

    public void setmLRecycleView(){

        collectAdapter=new CollectAdapter(collectBeanList,this);
        mLRecyclerAdapter=new LRecyclerViewAdapter(collectAdapter);
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
                getData();
            }
        });
        mLRecycleView.refresh();
        mLRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(CollectActivity.this,MagazineContentActivity.class);
                intent.putExtra("url",collectBeanList.get(position).getArticleUrl());
                startActivity(intent);
            }
        });
    }
    public void getData(){
        User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<CollectBean> query=new BmobQuery<CollectBean>();
        query.addWhereEqualTo("user",user);
        query.findObjects(new FindListener<CollectBean>() {
            @Override
            public void done(List<CollectBean> list, BmobException e) {
                if (e==null && list!=null){
                    collectBeanList.clear();
                    if (list.size()>0){
                        collectBeanList.addAll(list);
                    }else{
                        tvFoot.setText("你还没有收藏文章，赶快去收藏吧！");
                    }
                    mLRecycleView.refreshComplete(1000);  //刷新完成
                    mLRecyclerAdapter.notifyDataSetChanged();


                }else{
                    Utils.toastMessage(CollectActivity.this,"查询数据失败:"+e.getMessage());
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
