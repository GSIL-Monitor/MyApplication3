package com.cxy.magazine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.adapter.CommentAdapter;
import com.cxy.magazine.bmobBean.ArticleRecommBean;
import com.cxy.magazine.bmobBean.RecommBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recomm_lr)
    LRecyclerView mRecycleView;
    @BindView(R.id.recomm_edit)
    EditText editText;
    @BindView(R.id.recomm_iv)
    ImageView imageView;
    private User user;
    private String articleRecommId;

    private CommentAdapter commentAdapter;
    private LRecyclerViewAdapter mAdapter;
    private List<RecommBean> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        articleRecommId=intent.getStringExtra("articleRecommId");
        setRecycleView();
    }

    public void setRecycleView(){
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList);
        mAdapter=new LRecyclerViewAdapter(commentAdapter);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.thin_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mRecycleView.addItemDecoration(divider);
        //禁止自动加载更多
        mRecycleView.setLoadMoreEnabled(false);
        //添加foot
        SampleFooter footerView = new SampleFooter(this);
        TextView tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);
       // tvFoot.setText("没有更多数据了");

        mAdapter.addFooterView(footerView);
       //禁止下拉刷新
        mRecycleView.setPullRefreshEnabled(false);


        //获取所有数据
        BmobQuery<RecommBean> query=new BmobQuery<>();
        ArticleRecommBean articleRecommBean=new ArticleRecommBean();
        articleRecommBean.setObjectId(articleRecommId);
        query.addWhereEqualTo("articleRecommBean",new BmobPointer(articleRecommBean));
        query.include("user");
        query.order("-createdAt").findObjects(new FindListener<RecommBean>() {
            @Override
            public void done(List<RecommBean> list, BmobException e) {
               if (e==null){
                   getSupportActionBar().setTitle("共"+list.size()+"次推荐");
                   commentList.addAll(list);
                 //  mRecycleView.refreshComplete(50);  //刷新完成
                   mAdapter.notifyDataSetChanged();
               }
            }
        });

    }

    @OnClick(R.id.recomm_iv)
    public void recomm() {
        user = BmobUser.getCurrentUser(User.class);
        String comment="这个家伙很懒，什么也没说";
        if (user != null) {
            final RecommBean recommBean = new RecommBean();
            recommBean.setUser(user);
            String tempComment=editText.getText().toString();
            if (!TextUtils.isEmpty(tempComment)){
                comment=tempComment;
            }
            recommBean.setComment(comment);
            final ArticleRecommBean articleRecommBean = new ArticleRecommBean();
            articleRecommBean.setObjectId(articleRecommId);
            recommBean.setArticleRecommBean(articleRecommBean);
            recommBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        // 更新推荐记录总数 +1
                        articleRecommBean.increment("recommCount");
                        articleRecommBean.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Utils.toastMessage(CommentActivity.this, "你已成功推荐该篇文章");
                                    commentList.add(0,recommBean);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Utils.toastMessage(CommentActivity.this, "请先返回登录，再来推荐");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
