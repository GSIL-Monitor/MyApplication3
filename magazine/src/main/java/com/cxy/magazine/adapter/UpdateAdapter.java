package com.cxy.magazine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.entity.UpdateMagazine;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.HashMap;
import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> magazineList;
    private LayoutInflater mInflater;
    private Context context;
    private static final int TYPE_CONTENT=1;
    private static final int TYPE_AD=2;  //广告
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap =null;

    public UpdateAdapter(List magazineList, Context context,HashMap<NativeExpressADView, Integer> map) {
        this.magazineList = magazineList;
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
        this.mAdViewPositionMap=map;
    }
    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (position >= 0 && position <= magazineList.size() && adView != null) {
            magazineList.add(position, adView);
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        magazineList.remove(position);
        this.notifyItemRemoved(position); // position为adView在当前列表中的位置
        this.notifyItemRangeChanged(0, magazineList.size() - 1);   //range 范围
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_CONTENT){
            View view = mInflater.inflate(R.layout.magazine_subtitle_item, parent, false);
            ItemHolder itemHolder=new ItemHolder(view);
            return itemHolder;
        }else{
            View adView=mInflater.inflate(R.layout.item_express_ad,parent,false);
            return  new AdHolder(adView);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (getItemViewType(position)==TYPE_CONTENT){
            UpdateMagazine updateMagazine=(UpdateMagazine)magazineList.get(position);
            String title=updateMagazine.getTitle();
            ItemHolder itemHolder=(ItemHolder)holder;
            itemHolder.itemTv.setText(title);
            itemHolder.itemTv.setTextColor(context.getResources().getColor(R.color.colorFontText));
        }else{
            final NativeExpressADView adView = (NativeExpressADView) magazineList.get(position);
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            AdHolder adHolder=(AdHolder)holder;
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

    }

    @Override
    public int getItemCount() {
        return magazineList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return magazineList.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_CONTENT;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTv;

        public ItemHolder(View itemView) {
            super(itemView);
            itemTv = (TextView) itemView.findViewById(R.id.tv_item);
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
