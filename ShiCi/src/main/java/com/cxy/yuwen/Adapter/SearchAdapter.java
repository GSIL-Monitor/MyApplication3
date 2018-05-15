package com.cxy.yuwen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.yuwen.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cxy on 2017/12/27.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap> dataList;
    private static final int TYPE_TITLE = 0;//两种状态 标题 2017年
    private static final int TYPE_ITEM = 1;   //2017年第5期
    private LayoutInflater mInflater;
    private Context context;

    public SearchAdapter(List<HashMap> dataList, Context context) {
        this.dataList = dataList;
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.yilin_item_content, parent, false);
            ItemHolder viewHolder = new ItemHolder(view);
            return viewHolder;
        }
        if (viewType == TYPE_TITLE) {
            View titleView=mInflater.inflate(R.layout.yilin_title,parent,false);
            return new TitleHolder(titleView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap map=dataList.get(position);
        String text=map.get("text").toString();
        if (holder instanceof TitleHolder){
            TitleHolder titleHolder=(TitleHolder)holder;
            titleHolder.titleTv.setText(text);

        }
        if (holder instanceof ItemHolder){
             ItemHolder itemHolder=(ItemHolder)holder;
             itemHolder.itemTv.setText(text);

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String type=dataList.get(position).get("type").toString();
        if ("title".equals(type)){
            return TYPE_TITLE;
        }
        if ("item".equals(type)){
            return  TYPE_ITEM;
        }
        return  TYPE_ITEM;
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        TextView titleTv;

        public TitleHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.yilin_title);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTv;

        public ItemHolder(View itemView) {
            super(itemView);
            itemTv = (TextView) itemView.findViewById(R.id.yilin_item);
        }
    }
}
