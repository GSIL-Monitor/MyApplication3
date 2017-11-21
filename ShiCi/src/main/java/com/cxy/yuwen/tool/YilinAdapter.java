package com.cxy.yuwen.tool;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.activity.YilinArticleActivity;
import com.cxy.yuwen.activity.YilinDirectoryActivity;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cxy on 2017/11/19.
 */

public class YilinAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ParcelableMap> dataList;
    private LayoutInflater mInflater;
    private Context context;
    private static final int TYPE_TITLE = 0;//两种状态 标题 2017年
    private static final int TYPE_ITEM = 1;   //2017年第5期
    private static final String TYPE_YEAR="1";
    private static final String TYPE_TIME="2";
    public static final String YILIN_URL="http://www.92yilin.com/";
    public static final String DIRECTORY_FLAG="directory",ARTICLE_FLAG="article";
    private String activityFalg,directoryUrl;

    public YilinAdapter(Context context,ArrayList<ParcelableMap> dataList,String activityFalg) {
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
        this.dataList = dataList;
        this.activityFalg=activityFalg;
    }
    public YilinAdapter(Context context,ArrayList<ParcelableMap> dataList,String activityFalg,String directoryUrl) {
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
        this.dataList = dataList;
        this.activityFalg=activityFalg;
        this.directoryUrl=directoryUrl;
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
          if (holder instanceof TitleHolder){
              TitleHolder titleHolder=(TitleHolder)holder;
              HashMap map=dataList.get(position).getParamMap();
              titleHolder.titleTv.setText((String)map.get("text"));
          }
          if (holder instanceof ItemHolder){    //第几期
              ItemHolder itemHolder=(ItemHolder)holder;
              final HashMap map=dataList.get(position).getParamMap();
              itemHolder.itemTv.setText((String)map.get("text"));

              itemHolder.itemTv.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Log.i("yilin","点击了");
                      if (activityFalg.equals(DIRECTORY_FLAG)){
                          Intent intent=new Intent(context, YilinDirectoryActivity.class);
                          intent.putExtra("url",(String) map.get("href"));
                          context.startActivity(intent);
                      }
                      if (activityFalg.equals(ARTICLE_FLAG)){
                          Intent intent=new Intent(context, YilinArticleActivity.class);
                          intent.putExtra("url",YILIN_URL+directoryUrl+"/"+(String)map.get("href"));
                          context.startActivity(intent);
                      }

                  }
              });

          }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String type = (String) dataList.get(position).getParamMap().get("type");
        if (type.equals(TYPE_YEAR)){
            return TYPE_TITLE;
        }else if (type.equals(TYPE_TIME)){
            return TYPE_ITEM;
        }
       return TYPE_ITEM;
  }
}

class TitleHolder extends RecyclerView.ViewHolder{
    TextView titleTv;
    public TitleHolder(View itemView) {
        super(itemView);
        titleTv=(TextView) itemView.findViewById(R.id.yilin_title);
    }
}

class ItemHolder extends RecyclerView.ViewHolder{
    TextView itemTv;
    public ItemHolder(View itemView) {
        super(itemView);
        itemTv=(TextView) itemView.findViewById(R.id.yilin_item);
    }
}
