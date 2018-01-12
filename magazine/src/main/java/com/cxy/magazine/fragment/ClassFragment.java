package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.util.ACache;
import com.github.jdsjlzx.recyclerview.LRecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassFragment extends Fragment {
    public static final String MAGAZIENE_URL="http://www.fx361.com";
    private List<HashMap> magazineList;
    private static final int LOAD_FINISHED=100;
    private MagazineAdapter adapter;
    private ACache mAcache;


    @BindView(R.id.magazineRv)  LRecyclerView mLRecyclerview;
    private Unbinder unbinder;



    public ClassFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_class, container, false);
        unbinder = ButterKnife.bind(this, view);
        return  view;
    }
    public void setLRecyclerview(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //adapter
    class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.magazine_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final HashMap dataMap=magazineList.get(position);
            holder.tvClassName.setText(dataMap.get("text").toString());

            //设置点击事件
            holder.tvClassName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), MagazineActivity.class);
                    intent.putExtra("url",MAGAZIENE_URL+"/"+dataMap.get("href").toString());
                    intent.putExtra("title",dataMap.get("text").toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return magazineList.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder
        {
            @BindView(R.id.class_title)
            TextView tvClassName;


            public MyViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);

            }
        }
    }

}
