package com.yuwen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuwen.entity.Zi;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Adapter;

import java.util.List;

/**
 * Created by cxy on 2017/9/27.
 */

public class ZidianAdapter extends BaseAdapter {
    private List<Zi> Items;
    private LayoutInflater mInflater;

    public ZidianAdapter(Context context, List<Zi> newsItems) {
        this.Items = newsItems;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ZidianAdapter.ViewHolder holder=null;
        //判断是否缓存
        if (convertView==null){
            holder=new ZidianAdapter.ViewHolder();
            //通过LayoutInflater实例化布局
            convertView=mInflater.inflate(R.layout.zidian_item,null);
            holder.hanzi=(TextView)convertView.findViewById(R.id.hanzi);   //下次不用再findViewById()
            holder.pinyin=(TextView)convertView.findViewById(R.id.pinyin);
            holder.duyin=(TextView)convertView.findViewById(R.id.duyin);
            holder.bushou=(TextView)convertView.findViewById(R.id.bushou);
            holder.bihua=(TextView)convertView.findViewById(R.id.bihua);
            holder.jianjie=(TextView)convertView.findViewById(R.id.jianjie);
            holder.xiangjie=(TextView)convertView.findViewById(R.id.xiangjie);

            convertView.setTag(holder);

        }else{
            //通过tag找到缓存的布局
            holder=(ZidianAdapter.ViewHolder)convertView.getTag();
        }
        Zi zi=Items.get(position);
        holder.hanzi.setText(zi.getHanzi());
        holder.pinyin.setText(zi.getPinyin());
        holder.duyin.setText(zi.getDuyin());
        holder.bushou.setText(zi.getBushou());
        holder.bihua.setText(zi.getBihua());
        holder.jianjie.setText(zi.getJianjie());
        holder.xiangjie.setText(zi.getXiangjie());

        return  convertView;
    }


    public final class ViewHolder{

        TextView hanzi,pinyin,duyin,bushou,bihua,jianjie,xiangjie;

    }
}
