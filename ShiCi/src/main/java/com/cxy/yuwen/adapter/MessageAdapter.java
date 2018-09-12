package com.cxy.yuwen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.MsgNotification;

import java.util.List;

/**
 * Created by cxy on 2018/2/8.
 */

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ItemHolder>{
    private List<MsgNotification> msgList;
    private Context context;

    public MessageAdapter(List<MsgNotification> msgList, Context context) {
        this.msgList = msgList;
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.message_item,parent,false);
        ItemHolder itemHolder=new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
         MsgNotification msgNotification=msgList.get(position);
         holder.itemTv.setText(msgNotification.getTitle());
         if (!msgNotification.getRead()){   //该消息未读
             holder.itemImage.setVisibility(View.VISIBLE);
         }else{        //已读
             holder.itemImage.setVisibility(View.GONE);
         }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTv;
        ImageView itemImage;

        public ItemHolder(View itemView) {
            super(itemView);
            itemTv = (TextView) itemView.findViewById(R.id.msg_title);
            itemImage=(ImageView)itemView.findViewById(R.id.msg_img);
        }
    }
}
