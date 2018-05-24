package com.cxy.magazine.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.BuyBean;
import com.cxy.magazine.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by cxy on 2017/12/31.
 */


public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.MyViewHolder>{
    private Context context;
    private List<BuyBean> dataDisplayList;
    private Bitmap defaultImage;
    private GridLayoutManager gridLayoutManager;

    public BuyAdapter(Context context, List<BuyBean> dataDisplayList, GridLayoutManager gridLayoutManager) {
        this.context = context;
        this.dataDisplayList = dataDisplayList;
        this.gridLayoutManager=gridLayoutManager;
        defaultImage = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.default_book);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.magazine_cover_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final BuyBean buyBean=dataDisplayList.get(position);
        final String imageSrc=buyBean.getCoverUrl();

        holder.tvCoverName.setText(buyBean.getBookName());
        holder.tvCoverOrder.setText(buyBean.getPublishTime());
        holder.imCover.setImageBitmap(defaultImage);
     //   holder.imCover.setTag(imageSrc);
      //  holder.imTag=imageSrc;

        int width=(gridLayoutManager.getWidth()-120)/2;
        int height=width+width/3;
        ViewGroup.LayoutParams lp = holder.imCover.getLayoutParams();
        lp.width = width;
        lp.height = height;
        holder.imCover.setLayoutParams(lp);

     /*   holder.imCover.setMaxWidth(width);
        holder.imCover.setMaxHeight(height);*/

        Glide.with(context)
                .load(imageSrc)
                .placeholder(R.drawable.default_book)
                .error(R.drawable.default_book)
                .into(holder.imCover);

      /*  //新的线程中根据url获取图片
        new Thread(){
            @Override
            public void run() {
                Bitmap bitmap= Utils.getbitmap(imageSrc);
                holder.bitmap=bitmap;
                Message message=new Message();
                message.what=101;
                message.obj=holder;
                uiHandler.sendMessage(message);

            }
        }.start();
*/

    }



    @Override
    public int getItemCount() {
        return dataDisplayList.size();
    }

   /* Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyViewHolder holder=(MyViewHolder) msg.obj;
            if (msg.what==101){
                if (holder.imTag.equals(holder.imCover.getTag())){
                    holder.imCover.setImageBitmap(holder.bitmap);
                }
            }
        }
    };*/

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.coverName)
        TextView tvCoverName;
        @BindView(R.id.coverOrder) TextView tvCoverOrder;
        @BindView(R.id.coverImage)
        ImageView imCover;
        Bitmap bitmap;
        String imTag="";



        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }


}//