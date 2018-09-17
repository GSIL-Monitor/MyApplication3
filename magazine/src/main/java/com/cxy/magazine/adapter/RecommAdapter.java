package com.cxy.magazine.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.ArticleRecommBean;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cxy on 2018/8/28.
 * 首页推荐Adapter
 */

public class RecommAdapter extends  RecyclerView.Adapter<RecommAdapter.MyViewHolder> {

    private Context context=null;
    private List<Object> mData;
    static final int TYPE_DATA = 0;
    static final int TYPE_AD = 1;
    public RecommAdapter(Context context, List  recommBeanList) {
        this.mData = recommBeanList;
        this.context=context;

    }
    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position,String adTAg) {
        if (position >= 0 && position < mData.size() && adTAg != null) {
            mData.add(position, adTAg);
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
/*    public void removeADView(int position, NativeExpressADView adView) {
        mData.remove(position);
        this.notifyItemRemoved(position); // position为adView在当前列表中的位置
        this.notifyItemRangeChanged(0, mData.size() - 1);
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
       /* View view= LayoutInflater.from(context).inflate(R.layout.adapter_recomm,parent,false);
        MyViewHolder itemHolder=new MyViewHolder(view);
        return itemHolder;*/

        int layoutId = (viewType == TYPE_AD) ? R.layout.item_express_ad : R.layout.adapter_recomm;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
           int type = getItemViewType(position);
        if (TYPE_AD == type) {
            AdRequest adRequest = new AdRequest.Builder().build();
            holder.mAdView.loadAd(adRequest);
        }else {
            ArticleRecommBean recommBean = (ArticleRecommBean)mData.get(position);
            holder.tvTitle.setText(recommBean.getArticleTitle());
            holder.tvTime.setText(recommBean.getArticleTime());
            holder.tvCount.setText(recommBean.getRecommCount().toString());
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (mData.get(position) instanceof String && ((String)mData.get(position)).equals("adview")) ? TYPE_AD : TYPE_DATA;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
       // @BindView(R.id.recomm_title)
        TextView tvTitle;
      //  @BindView(R.id.recomm_time)
        TextView tvTime;
       // @BindView(R.id.recomm_count)
        TextView tvCount;
       // @BindView(R.id.express_ad_container)
       AdView mAdView;



        public MyViewHolder(View view)
        {
            super(view);
            //ButterKnife.bind(this,view);
            tvTitle=(TextView)view.findViewById(R.id.recomm_title);
            tvTime=(TextView)view.findViewById(R.id.recomm_time);
            tvCount=(TextView)view.findViewById(R.id.recomm_count);
            mAdView=(AdView)view.findViewById(R.id.item_adView);

        }
    }
}
