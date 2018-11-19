package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 文章排行Adapter
 */
public class RankAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Object> rankData;
 //   private Context context;
    private LayoutInflater mInflater;
    private static final int TYPE_TITLE=0;
    private static final int TYPE_CONTENT=1;
    private static final int TYPE_AD=2;  //广告
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap =null;

    public RankAdapter(ArrayList  rankData, Context context, HashMap<NativeExpressADView, Integer> map) {
        this.rankData = rankData;
        this.mInflater=LayoutInflater.from(context);
        this.mAdViewPositionMap = map;
    }

    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (position >= 0 && position <= rankData.size() && adView != null) {
            HashMap<String,Object> adMap=new HashMap<>();
            adMap.put("type","ad");
            adMap.put("data",adView);
            rankData.add(position, adMap);
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        rankData.remove(position);
        this.notifyItemRemoved(position); // position为adView在当前列表中的位置
        this.notifyItemRangeChanged(0, rankData.size() - 1);   //range 范围
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_CONTENT){
            View view = mInflater.inflate(R.layout.item_rank, parent, false);
            ItemHolder itemHolder=new ItemHolder(view);
            return itemHolder;
        }

        if (viewType==TYPE_TITLE){
                View titleView=mInflater.inflate(R.layout.magazine_title_item, parent,false);
                return new TitleHolder(titleView);

        }
        if(viewType== TYPE_AD){
            View adView=mInflater.inflate(R.layout.item_express_ad,parent,false);
            return  new AdHolder(adView);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      //  HashMap<String,Object> dataMap=rankData.get(position);
        //设置ad Item
        if (getItemViewType(position)==TYPE_AD){
            HashMap<String,Object> adMap=(HashMap<String, Object>) rankData.get(position);
            final NativeExpressADView adView = (NativeExpressADView) adMap.get("data");
            AdHolder adHolder=(AdHolder)holder;
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            if (adHolder.adContainer.getChildCount() > 0 && adHolder.adContainer.getChildAt(0) == adView) {
                return;
            }

            if (adHolder.adContainer.getChildCount() > 0) {
                adHolder.adContainer.removeAllViews();
            }

            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            adHolder.adContainer.addView(adView);
            adView.render(); // 调用render方法后sdk才会开始展示广告

        }
        //设置content Item
        if (getItemViewType(position)==TYPE_CONTENT){
            HashMap<String,String> dataMap=(HashMap<String, String>) rankData.get(position);
            String title=(String) dataMap.get("title");
            ItemHolder itemHolder=(ItemHolder)holder;
            String time=(String) dataMap.get("time");
            itemHolder.titleTv.setText(title);
            itemHolder.timeTv.setText(time);
        }
        //设置 title Item
        if (getItemViewType(position)==TYPE_TITLE){
            HashMap<String,String> dataMap=(HashMap<String, String>) rankData.get(position);
            String title=(String) dataMap.get("title");
            TitleHolder titleHolder=(TitleHolder)holder;
            titleHolder.titleTv.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        return rankData.size();
    }

    @Override
    public int getItemViewType(int position) {
        HashMap map=(HashMap) rankData.get(position);
        String type=(String)map.get("type");
       if( type.equals("title")){
           return  TYPE_TITLE;
       }
       if (type.equals("item")){
           return TYPE_CONTENT;
       }else{    // ad
           return TYPE_AD;
       }
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        TextView titleTv;

        public TitleHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.yilin_title);

        }
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


    //广告ItemHolder
    class AdHolder extends  RecyclerView.ViewHolder{
        ViewGroup adContainer;

        public AdHolder(View itemView ) {
            super(itemView);
            adContainer = (ViewGroup) itemView.findViewById(R.id.express_ad_container);
        }
    }


}
