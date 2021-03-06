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
import com.cxy.magazine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cxy on 2017/12/31.
 */


public class ImageTextAdapter extends RecyclerView.Adapter<ImageTextAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<HashMap> dataDisplayList;
    private GridLayoutManager gridLayoutManager;

  //  private Bitmap defaultImage;

    public ImageTextAdapter(Context context, ArrayList<HashMap> dataDisplayList,GridLayoutManager gridLayoutManager) {
        this.context = context;
        this.dataDisplayList = dataDisplayList;
        this.gridLayoutManager=gridLayoutManager;
      //  defaultImage = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.default_book);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.magazine_cover_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final HashMap hashMap=dataDisplayList.get(position);
        final String imageSrc=hashMap.get("imageSrc").toString();

        holder.tvCoverName.setText(hashMap.get("name").toString());
        holder.tvCoverOrder.setText(hashMap.get("time").toString());


        int width=(gridLayoutManager.getWidth()-120)/2;
        int height=width+width/3;
        ViewGroup.LayoutParams lp = holder.imCover.getLayoutParams();
        lp.width = width;
        lp.height = height;
        holder.imCover.setLayoutParams(lp);


        Glide.with(context)
                .load(imageSrc)
                .placeholder(R.drawable.default_book)
                .error(R.drawable.default_book)
                .into(holder.imCover);


    }



    @Override
    public int getItemCount() {
        return dataDisplayList.size();
    }

    /*Handler uiHandler=new Handler(){
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
     //   Bitmap bitmap;
     //   String imTag="";



        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }


}//