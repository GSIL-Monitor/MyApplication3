package com.yuwen.tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuwen.entity.Article;
import com.yuwen.entity.Chengyu;
import com.yuwen.entity.CiYu;
import com.yuwen.activity.MainActivity;
import com.yuwen.myapplication.R;

import java.util.List;

import static com.yuwen.fragment.MainFragment.FLAG_CI;
import static com.yuwen.fragment.MainFragment.FLAG_IDIOM;
import static com.yuwen.fragment.MainFragment.FLAG_POEM;
import static com.yuwen.fragment.MainFragment.mark;

/**
 * Created by cxy on 2016/7/12.
 */
public class Adapter extends BaseAdapter {

    private List Items;
    private LayoutInflater mInflater;

    public Adapter(Context context, List newsItems) {
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
        if (mark==FLAG_POEM) {
            Article item = (Article)Items.get(position);
            holder.articleTitle.setText(item.getTitle());
        }else if (mark==FLAG_IDIOM){          //成语
            Chengyu chengyu= (Chengyu)Items.get(position);
            holder.articleTitle.setText(chengyu.getName());
        }else if (mark==FLAG_CI){
            CiYu ciYu=(CiYu) Items.get(position);
            holder.articleTitle.setText(ciYu.getName());
        }


        return convertView;
    }

    public final class ViewHolder{

        TextView articleTitle;

    }
}
