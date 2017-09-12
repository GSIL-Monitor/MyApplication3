package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cxy on 2016/7/12.
 */
public class Adapter extends BaseAdapter {

    private List<Article> newsItems;
    private LayoutInflater mInflater;

    public Adapter(Context context, List<Article> newsItems) {
        this.newsItems = newsItems;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return newsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        //判断是否缓存
        if (convertView==null){
            holder=new ViewHolder();
            //通过LayoutInflater实例化布局
            convertView=mInflater.inflate(R.layout.item,null);
            holder.articleTitle=(TextView)convertView.findViewById(R.id.textView);   //下次不用再findViewById()

            convertView.setTag(holder);

        }else{
            //通过tag找到缓存的布局
            holder=(ViewHolder)convertView.getTag();
        }
        //设置布局控件中控件要显示的布局
        Article item=newsItems.get(position);
        holder. articleTitle.setText(item.getTitle());


        return convertView;
    }

    public final class ViewHolder{

        TextView articleTitle;

    }
}
