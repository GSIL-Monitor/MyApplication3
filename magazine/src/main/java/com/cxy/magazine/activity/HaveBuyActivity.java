package com.cxy.magazine.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cxy.magazine.R;
import com.cxy.magazine.adapter.BookshelfAdapter;
import com.cxy.magazine.adapter.BuyAdapter;
import com.cxy.magazine.bmobBean.Bookshelf;
import com.cxy.magazine.bmobBean.BuyBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.SampleFooter;
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
import cn.bmob.v3.listener.UpdateListener;

public class HaveBuyActivity extends BasicActivity {
    @BindView(R.id.recyclerView_buy)
    LRecyclerView mRecyclerView;

    private BuyAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private User user;
    private TextView tvFoot;
    private ArrayList<BuyBean> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_buy);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("已购杂志");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bookList=new ArrayList<BuyBean>();
     //   setBookList();
        setRecyclerView();


    }

    public void setBookList(){
        bookList.clear();
        User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<BuyBean> bmobQuery=new BmobQuery<BuyBean>();
        bmobQuery.addWhereEqualTo("user", user);    // 查询当前用户的所有收藏内容
        bmobQuery.order("-updatedAt");      //按照创建时间排序
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        bmobQuery.setLimit(50);
        if (NetWorkUtils.isNetworkConnected(this)) {
             bmobQuery.findObjects(new FindListener<BuyBean>() {
                 @Override
                 public void done(List<BuyBean> list, BmobException e) {
                     if (e==null && list!=null){
                         bookList.addAll(list);
                         if (list.size()<=0){
                             tvFoot.setText("你还没有已购杂志，快去购买几本吧！每本仅需2元");
                         }
                         mRecyclerView.refreshComplete(1000);  //刷新完成
                         mLRecyclerViewAdapter.notifyDataSetChanged();
                     }else{
                         Toast.makeText(HaveBuyActivity.this, "出错了，请稍候再试！", Toast.LENGTH_SHORT).show();
                     }
                 }
             });

        }else{
            Utils.toastMessage(HaveBuyActivity.this,"网络已断开，请检查网络连接");
        }
    }

    public void setRecyclerView(){
        GridLayoutManager manager=new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(manager);
        recycleViewAdapter=new BuyAdapter(this,bookList,manager);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setLoadMoreEnabled(false);
        //添加foot
        SampleFooter footerView = new SampleFooter(this);
        tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);
        mLRecyclerViewAdapter.addFooterView(footerView);

        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                setBookList() ;
            }
        });
        mRecyclerView.refresh();


        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String href=bookList.get(position).getDirectoryUrl();
                Intent intent=new Intent(HaveBuyActivity.this, MagazineDirectoryActivity.class);
                intent.putExtra("href",href);
                startActivity(intent);
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
