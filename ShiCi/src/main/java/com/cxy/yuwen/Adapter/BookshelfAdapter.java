package com.cxy.yuwen.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.tool.Util;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cxy on 2017/12/31.
 */


public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.MyViewHolder>{
    private Context context;
    private List<Bookshelf> dataDisplayList;

    private Bitmap defaultImage;

    public BookshelfAdapter(Context context, List<Bookshelf> dataDisplayList) {
        this.context = context;
        this.dataDisplayList = dataDisplayList;
        defaultImage = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.default_book);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.magazine_cover_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Bookshelf bookshelf=dataDisplayList.get(position);
        final String imageSrc=bookshelf.getCoverUrl();

        holder.tvCoverName.setText(bookshelf.getBookName());
        holder.tvCoverOrder.setText(bookshelf.getPulishTime());
        holder.imCover.setImageBitmap(defaultImage);
        holder.imCover.setTag(imageSrc);
        holder.imTag=imageSrc;


        //新的线程中根据url获取图片
        new Thread(){
            @Override
            public void run() {
                Bitmap bitmap= Util.getbitmap(imageSrc);
                holder.bitmap=bitmap;
                Message message=new Message();
                message.what=101;
                message.obj=holder;
                uiHandler.sendMessage(message);

            }
        }.start();


    }



    @Override
    public int getItemCount() {
        return dataDisplayList.size();
    }

    Handler uiHandler=new Handler(){
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
    };

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