package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ItemHolder> {

    private ArrayList<HashMap<String,String>> rankData;
    private Context context;
    private LayoutInflater mInflater;


    public RankAdapter(ArrayList<HashMap<String, String>> rankData, Context context) {
        this.rankData = rankData;
        this.context = context;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_rank, parent, false);
        ItemHolder itemHolder=new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        String title=rankData.get(position).get("title");
        String time=rankData.get(position).get("time");
        holder.titleTv.setText(title);
        holder.timeTv.setText(time);
    }

    @Override
    public int getItemCount() {
        return rankData.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView timeTv;

        public ItemHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.article_title);
            timeTv=(TextView)itemView.findViewById(R.id.article_time);
        }
    }
}
