package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cxy.magazine.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cxy on 2017/12/27.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap> dataList;
    private LayoutInflater mInflater;
    private Context context;

    public SearchAdapter(List<HashMap> dataList, Context context) {
        this.dataList = dataList;
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mInflater.inflate(R.layout.magazine_subtitle_item, parent, false);
            ItemHolder viewHolder = new ItemHolder(view);
            return viewHolder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap map=dataList.get(position);
        String text=map.get("text").toString();


             ItemHolder itemHolder=(ItemHolder)holder;
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
            itemTv = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}
