package com.cxy.magazine.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxy.magazine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cxy on 2017/12/31.
 */


public class MagazineListAdapter extends RecyclerView.Adapter<MagazineListAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<HashMap<String,String>> dataDisplayList;
    private GridLayoutManager gridLayoutManager;

  //  private Bitmap defaultImage;

    public MagazineListAdapter(Context context, ArrayList<HashMap<String,String>> dataDisplayList, GridLayoutManager gridLayoutManager) {
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
        //final HashMap hashMap=dataDisplayList.get(position);
        //final String imageSrc=hashMap.get("imageSrc").toString();


           // JSONObject jsonObject=dataDisplayList.getJSONObject(position);
            HashMap<String,String> jsonObject=dataDisplayList.get(position);
            holder.tvCoverName.setText(jsonObject.get("name"));    //hashMap.get("name").toString()
            holder.tvCoverOrder.setText("更新至"+jsonObject.get("time"));                         //hashMap.get("time").toString()


            int width=(gridLayoutManager.getWidth()-120)/2;
            int height=width+width/3;
            ViewGroup.LayoutParams lp = holder.imCover.getLayoutParams();
            lp.width = width;
            lp.height = height;
            holder.imCover.setLayoutParams(lp);


            Glide.with(context)
                    .load(jsonObject.get("imageSrc"))
                    .placeholder(R.drawable.default_book)
                    .error(R.drawable.default_book)
                    .override(width,height)
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
        Bitmap bitmap;
     //   String imTag="";



        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }


}//