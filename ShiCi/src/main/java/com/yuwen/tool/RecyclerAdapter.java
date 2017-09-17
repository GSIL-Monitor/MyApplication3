package com.yuwen.tool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuwen.BmobBean.Collect;
import com.yuwen.Entity.CollectBean;
import com.yuwen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxy on 2017/9/3.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private ArrayList<Collect> collectBeanlist;
    private OnRecyclerItemClickListener itemClickListener;

    public RecyclerAdapter(Context context,ArrayList<Collect> list){
        this.mInflater=LayoutInflater.from(context);
        this.collectBeanlist=list;
    }

    public  void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.itemClickListener=listener;
    }

    /**
     * item显示类型
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item,parent,false);
        //view.setBackgroundColor(Color.GRAY);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
    /**
     * 数据的绑定显示
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Collect bean=collectBeanlist.get(position);
        holder.item_tv.setText(bean.getName());
        holder.item_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(position,bean);
            }
        });
        holder.item_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemClickListener.onItemLongClick(position,bean);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return collectBeanlist.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_tv;
        public ViewHolder(View view){
            super(view);
            item_tv = (TextView)view.findViewById(R.id.textView);
        }
    }

    /**
     * 自定义RecyclerView 中item view点击回调方法
     */
    public interface OnRecyclerItemClickListener{

        void onItemClick(int position,Collect bean);
        void onItemLongClick(int position,Collect bean);
    }


}
