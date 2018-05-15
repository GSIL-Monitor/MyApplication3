package com.cxy.yuwen.adapter;

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
import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.Bookshelf;
import com.cxy.yuwen.tool.Utils;

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
    private GridLayoutManager gridLayoutManager;

    public BookshelfAdapter(Context context, List<Bookshelf> dataDisplayList, GridLayoutManager gridLayoutManager) {
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
        final Bookshelf bookshelf=dataDisplayList.get(position);
        final String imageSrc=bookshelf.getCoverUrl();

        holder.tvCoverName.setText(bookshelf.getBookName());
        holder.tvCoverOrder.setText(bookshelf.getPulishTime());
        holder.imCover.setImageBitmap(defaultImage);

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



    class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.coverName)
        TextView tvCoverName;
        @BindView(R.id.coverOrder) TextView tvCoverOrder;
        @BindView(R.id.coverImage)
        ImageView imCover;




        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }


}//