package com.cxy.yuwen.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.cxy.yuwen.activity.CollectActivity;
import com.cxy.yuwen.activity.LoginActivity;
import com.cxy.yuwen.activity.MagazineDirectoryActivity;
import com.cxy.yuwen.activity.MainActivity;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.bmobBean.Collect;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.tool.Util;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
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
import cn.bmob.v3.listener.UpdateListener;


public class ShelfFragment extends Fragment {

    @BindView(R.id.recyclerView_shelf)
    LRecyclerView mRecyclerView;

    private BookshelfAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private User user;
    Unbinder unbinder;

    private ArrayList<Bookshelf> bookList;

    public ShelfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("shelf","onCreateView");
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shelf, container, false);
        unbinder= ButterKnife.bind(this,view);
        bookList=new ArrayList<Bookshelf>();
     //   getAllBook();
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
       user= BmobUser.getCurrentUser(User.class);
        if (user==null){
            Util.showConfirmCancelDialog(getActivity(), "提示", "请先登录,以同步书架！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent1);
                }
            });
        }else{
            BmobQuery<Bookshelf> query = new BmobQuery<Bookshelf>();

            query.addWhereEqualTo("user", user);    // 查询当前用户的所有收藏内容
            query.order("-updatedAt");      //按照创建时间排序
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


    }

    @Override
    public void onResume() {
     //   Log.i("shelf","onResume");
        super.onResume();
        if (bookList.size()<=0){
            getAllBook();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void setRecyclerView(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),2);
        mRecyclerView.setLayoutManager(manager);

        recycleViewAdapter=new BookshelfAdapter(getContext(),bookList,manager);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(recycleViewAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
       // int spacing = getResources().getDimensionPixelSize(R.dimen.dp_18);
      //  mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(),android.R.color.white));
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

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                //删除书籍
                Snackbar.make(mRecyclerView, "要删除该书籍吗？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     Bookshelf bookshelf=bookList.get(position);
                     bookshelf.delete(new UpdateListener() {
                         @Override
                         public void done(BmobException e) {
                             if (e==null){
                                 Util.toastMessage(getActivity(),"删除书籍成功");
                                 bookList.remove(position);
                                 mLRecyclerViewAdapter.notifyDataSetChanged();
                             }else{
                                Util.toastMessage(getActivity(),e.getMessage());
                             }
                         }
                     });
                    }
                }).show();
            }
        });

    }
}
