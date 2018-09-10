package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.RecommBean;
import com.cxy.magazine.bmobBean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 评论Adapter
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private Context context;
    private List<RecommBean> commentList;

    public CommentAdapter(Context context, List<RecommBean> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_comment,parent,false);
        CommentViewHolder viewHolder=new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
           RecommBean recommBean=commentList.get(position);
           User user=recommBean.getUser();
           holder.userName.setText(user.getUsername());
           if (!TextUtils.isEmpty(user.getHeadImageUrl())){
               //头像不为null
               Glide.with(context)
                       .load(user.getHeadImageUrl())
                       .into(holder.headView);

           }
           holder.userComment.setText(recommBean.getComment());
           String time=recommBean.getCreatedAt();
           if (TextUtils.isDigitsOnly(time)){
               time="刚刚";
           }
           holder.commentTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.user_head_iv)
        ImageView headView;
        @BindView(R.id.user_name)
        TextView userName ;
        @BindView(R.id.user_comment)
        TextView userComment;
        @BindView(R.id.comment_time)
        TextView commentTime;



        public CommentViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
