package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.CollectBean;

import java.util.List;

/**
 * Created by cxy on 2018/1/20.
 */

public class CollectAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<CollectBean> dataList;
    private LayoutInflater mInflater;
    private Context context;

    public CollectAdapter(List<CollectBean> dataList, Context context) {
        this.dataList = dataList;
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.magazine_title_item, parent, false);
        CollectAdapter.ItemHolder viewHolder = new CollectAdapter.ItemHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CollectBean collectBean=dataList.get(position);
        String text=collectBean.getArticleTitle();


        CollectAdapter.ItemHolder itemHolder=(CollectAdapter.ItemHolder)holder;
        itemHolder.itemTv.setText(text);


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTv;

        public ItemHolder(View itemView) {
            super(itemView);
            itemTv = (TextView) itemView.findViewById(R.id.yilin_title);
        }
    }
}
