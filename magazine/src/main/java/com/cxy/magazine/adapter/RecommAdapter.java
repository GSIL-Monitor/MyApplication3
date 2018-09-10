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
import com.qq.e.ads.nativ.NativeExpressADView;

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
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();

    public RecommAdapter(Context context, List  recommBeanList,HashMap<NativeExpressADView, Integer> map) {
        this.mData = recommBeanList;
        this.context=context;
        this.mAdViewPositionMap=map;
    }
    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (position >= 0 && position < mData.size() && adView != null) {
            mData.add(position, adView);
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        mData.remove(position);
        this.notifyItemRemoved(position); // position为adView在当前列表中的位置
        this.notifyItemRangeChanged(0, mData.size() - 1);
    }

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
            final NativeExpressADView adView = (NativeExpressADView) mData.get(position);
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            if (holder.container.getChildCount() > 0 && holder.container.getChildAt(0) == adView) {
                return;
            }

            if (holder.container.getChildCount() > 0) {
                holder.container.removeAllViews();
            }

            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            holder.container.addView(adView);
            adView.render(); // 调用render方法后sdk才会开始展示广告
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
        return mData.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
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
        ViewGroup container;



        public MyViewHolder(View view)
        {
            super(view);
            //ButterKnife.bind(this,view);
            tvTitle=(TextView)view.findViewById(R.id.recomm_title);
            tvTime=(TextView)view.findViewById(R.id.recomm_time);
            tvCount=(TextView)view.findViewById(R.id.recomm_count);
            container=(ViewGroup)view.findViewById(R.id.express_ad_container);

        }
    }
}
