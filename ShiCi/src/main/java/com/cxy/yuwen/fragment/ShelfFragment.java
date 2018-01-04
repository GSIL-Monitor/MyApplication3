package com.cxy.yuwen.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cxy.yuwen.Adapter.BookshelfAdapter;
import com.cxy.yuwen.Adapter.ImageTextAdapter;
import com.cxy.yuwen.R;
import com.cxy.yuwen.activity.MagazineDirectoryActivity;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class ShelfFragment extends Fragment {

    @BindView(R.id.recyclerView_shelf)
    LRecyclerView mRecyclerView;

    private BookshelfAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    Unbinder unbinder;

    private ArrayList<Bookshelf> bookList;

    public ShelfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shelf, container, false);
        unbinder= ButterKnife.bind(this,view);
        bookList=new ArrayList<Bookshelf>();
        getAllBook();
        setRecyclerView();
        return  view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getAllBook(){

        //查询数据
        User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<Bookshelf> query = new BmobQuery<Bookshelf>();

        query.addWhereEqualTo("user", user);    // 查询当前用户的所有收藏内容
        query.order("-createdAt");      //按照创建时间排序
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);

        query.findObjects(new FindListener<Bookshelf>() {
            @Override
            public void done(List<Bookshelf> queryList, BmobException e) {
                if(e==null){
                    Log.i("bmob","查询成功：共"+queryList.size()+"条数据。");

                    bookList.addAll(queryList);
                    mLRecyclerViewAdapter.notifyDataSetChanged();

                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(getContext(), "出错了，请稍候再试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setRecyclerView(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        recycleViewAdapter=new BookshelfAdapter(getContext(),bookList);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        GridItemDecoration divider = new GridItemDecoration.Builder(this.getContext())
                .setHorizontal(R.dimen.activity_horizontal_margin)
                .setVertical(R.dimen.activity_vertical_margin)
                .setColorResource(android.R.color.white)
                .build();
        mRecyclerView.addItemDecoration(divider);
        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String href=bookList.get(position).getDirectoryUrl();
                Intent intent=new Intent(getActivity(), MagazineDirectoryActivity.class);
                intent.putExtra("href",href);
                startActivity(intent);
            }
        });

    }
}
