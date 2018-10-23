package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.entity.UpdateMagazine;

import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ItemHolder> {

    private List<UpdateMagazine> magazineList;
    private Context context;
    private LayoutInflater mInflater;

    public UpdateAdapter(List<UpdateMagazine> magazineList, Context context) {
        this.magazineList = magazineList;
        this.context = context;
        this.mInflater=LayoutInflater.from(context);

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.magazine_subtitle_item, parent, false);
        ItemHolder itemHolder=new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
         String title=magazineList.get(position).getTitle();
         holder.itemTv.setText(title);
    }

    @Override
    public int getItemCount() {
        return magazineList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTv;

        public ItemHolder(View itemView) {
            super(itemView);
            itemTv = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}
