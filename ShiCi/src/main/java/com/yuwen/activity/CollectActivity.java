package com.yuwen.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.yuwen.BmobBean.Collect;
import com.yuwen.BmobBean.User;
import com.yuwen.Entity.Article;
import com.yuwen.Entity.Chengyu;
import com.yuwen.Entity.CiYu;
import com.yuwen.Entity.CollectBean;
import com.yuwen.Entity.Zi;
import com.yuwen.myapplication.R;
import com.yuwen.tool.DBHelper;
import com.yuwen.tool.DBOperate;
import com.yuwen.tool.Divider;
import com.yuwen.tool.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class CollectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Collect> list=new ArrayList<Collect>();
    private  DBHelper dbHelper;
    private DBOperate dbOperate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ActionBar bar= getSupportActionBar();
        bar.setTitle("我的收藏");

        //开始设置RecyclerView
        recyclerView=(RecyclerView)this.findViewById(R.id.recyclerView);
        //设置固定大小
        recyclerView.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(this);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //添加间隔线
        Divider divider = new Divider(new ColorDrawable(0xffcccccc), OrientationHelper.VERTICAL);
        //单位:px
        divider.setMargin(8, 8, 8, 0);
        divider.setHeight(10);
        recyclerView.addItemDecoration(divider);

        //创建适配器，并且设置
        dbHelper=new DBHelper(CollectActivity.this);
        dbOperate=new DBOperate(dbHelper) ;
        //查询数据
        User user= BmobUser.getCurrentUser(User.class);
        BmobQuery<Collect> query = new BmobQuery<Collect>();

        query.addWhereEqualTo("user", user);    // 查询当前用户的所有收藏内容
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);

        query.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> queryList, BmobException e) {
                if(e==null){
                    Log.i("bmob","查询成功：共"+queryList.size()+"条数据。");

                      list.addAll(queryList);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });



        mAdapter = new RecyclerAdapter(CollectActivity.this,list);
        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, Collect bean) {    //点击事件
                 Integer type=bean.getType();
                 if (type.equals(1)){    //字典
                     //Gson gson=new Gson();
                     Zi zi=(Zi) bean.getContent();
                     Intent intent=new Intent(CollectActivity.this,ZidianActivity.class);
                     intent.putExtra("zi",zi);
                     startActivity(intent);
                }
                if (type.equals(2)){    //词语
                    CiYu ciYu=(CiYu) bean.getContent();
                    Intent intent=new Intent(CollectActivity.this,CiYuActivity.class);
                    intent.putExtra("ciYu",ciYu);
                    startActivity(intent);
                }
                if (type.equals(3)){    //成语
                    Chengyu chengyu=(Chengyu)bean.getContent();
                    Intent intent=new Intent(CollectActivity.this,ChengyuActivity.class);
                    intent.putExtra("chengyu",chengyu);
                    startActivity(intent);
                }
                if (type.equals(4)){    //诗词
                    /*Gson gson=new Gson();
                    Article article=gson.fromJson(bean.getContent(),Article.class);*/
                    Article article=(Article)bean.getContent();
                    Intent intent=new Intent(CollectActivity.this,ShiciActivity.class);
                    intent.putExtra("article",article);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(final int position, final Collect bean) {   //长按事件

                ConstraintLayout layout=(ConstraintLayout) findViewById(R.id.collet_constraint);
                Snackbar.make(layout, "确定要删除该项吗？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //执行删除操作

                       bean.delete(new UpdateListener() {
                           @Override
                           public void done(BmobException e) {
                               if(e==null){
                                   Log.i("bmob","删除成功");
                               }else{
                                   Log.i("bmob","删除失败："+e.getMessage()+","+e.getErrorCode());
                               }
                           }
                       });


                        //更新RecycleView
                        list.remove(position);
                        mAdapter.notifyItemRemoved(position);

                    }
                }).show();
            }
        });
        recyclerView.setAdapter(mAdapter);

    }
}
