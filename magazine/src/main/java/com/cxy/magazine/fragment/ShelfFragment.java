package com.cxy.magazine.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cxy.magazine.bmobBean.Bookshelf;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.R;
import com.cxy.magazine.activity.LoginActivity;
import com.cxy.magazine.activity.MagazineDirectoryActivity;
import com.cxy.magazine.adapter.BookshelfAdapter;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class ShelfFragment extends BaseFragment {

    @BindView(R.id.recyclerView_shelf)
    LRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    QMUIEmptyView emptyView;

    private BookshelfAdapter recycleViewAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private User user;
    Unbinder unbinder;
    boolean needInit=true;


    private ArrayList<Bookshelf> bookList;

    public ShelfFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            mRecyclerView.refreshComplete(1);
          /*  Utils.showConfirmCancelDialog(getActivity(), "提示", "请先登录,以同步书架！", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {

                    needInit=true;
                    Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent1);
                }

            });*/
          mRecyclerView.setVisibility(View.GONE);
          emptyView.setVisibility(View.VISIBLE);
          emptyView.show(false, "提示", "请先登录,以同步书架！", "去登录", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  needInit=true;
                  Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                  startActivity(intent1);
              }
          });
        } else {
            bookList.clear();
            if (NetWorkUtils.isNetworkConnected(context)) {
                BmobQuery<Bookshelf> query = new BmobQuery<Bookshelf>();

                query.addWhereEqualTo("user", user);    // 查询当前用户的所有收藏内容
                query.order("-updatedAt");      //按照创建时间排序
                //返回50条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(50);

                query.findObjects(new FindListener<Bookshelf>() {
                    @Override
                    public void done(List<Bookshelf> queryList, BmobException e) {
                        if (e == null && queryList!=null) {
                            Log.i("bmob", "查询成功：共" + queryList.size() + "条数据。");

                            bookList.addAll(queryList);
                            mAcache.put("shelfCache",bookList);
                            mRecyclerView.refreshComplete(1000);
                            mLRecyclerViewAdapter.notifyDataSetChanged();
                            if(queryList.size()<=0){
                              //  Utils.showResultDialog(getActivity(),"你的书架空空如也，快去添加几本吧","提示");
                                mRecyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                                emptyView.show(null,"你的书架空空如也，快去添加几本吧");

                            }

                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                           // Toast.makeText(getContext(), "出错了，请稍候再试！", Toast.LENGTH_SHORT).show();
                         //   QMUIEmptyView emptyView=new QMUIEmptyView(getActivity());
                            mRecyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.show(null,"出错了，请稍候再试！");
                        }
                    }
                });
            }else{

                mRecyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.show(null,"网络已断开，请检查网络连接");
            }


        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (needInit) {
            Object shelfObject = mAcache.getAsObject("shelfCache");
            if (shelfObject != null ) {
                bookList.clear();
                ArrayList<Bookshelf> tempBookList = (ArrayList<Bookshelf>) shelfObject;
                bookList.addAll(tempBookList);
                mLRecyclerViewAdapter.notifyDataSetChanged();
                if(tempBookList.size()<=0){

                    mRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.show(null,"你的书架空空如也，快去添加几本吧");
                }
            } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    mRecyclerView.refresh();


            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();


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


        //设置下拉刷新
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllBook();
            }
        });
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                needInit=false;
                String href=bookList.get(position).getDirectoryUrl();
                Intent intent=new Intent(getActivity(), MagazineDirectoryActivity.class);
                intent.putExtra("href",href);
                intent.putExtra("type","shelf");
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
                                 Utils.toastMessage(getActivity(),"删除书籍成功");
                                 bookList.remove(position);
                                 mAcache.put("shelfCache",bookList);
                                 mLRecyclerViewAdapter.notifyDataSetChanged();
                             }else{
                                Utils.toastMessage(getActivity(),e.getMessage());
                             }
                         }
                     });
                    }
                }).show();
            }
        });

    }
}
