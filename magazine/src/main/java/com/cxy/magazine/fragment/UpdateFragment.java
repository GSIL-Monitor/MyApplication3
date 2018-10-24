package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineContentActivity;
import com.cxy.magazine.activity.MagazineDirectoryActivity;
import com.cxy.magazine.adapter.UpdateAdapter;
import com.cxy.magazine.entity.UpdateMagazine;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends BaseFragment {

    private static final String ARG_PARAM = "data";
    private static final String DOMIAN = "http://www.fx361.com";

    private List<UpdateMagazine> dataList;

    @BindView(R.id.update_lr)
    LRecyclerView recyclerView;

    UpdateAdapter updateAdapter=null;
    LRecyclerViewAdapter mLRecyclerAdapter=null;


    public UpdateFragment() {
        // Required empty public constructor
    }


    public static UpdateFragment newInstance(ArrayList<UpdateMagazine> data) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_PARAM,data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataList = getArguments().getParcelableArrayList(ARG_PARAM);
            System.out.println("123");

        }
    }

    private Unbinder unbinder=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_update, container, false);
        unbinder=ButterKnife.bind(this,rootView);
        setRecyclerView();
        return  rootView;
    }

    public void setRecyclerView(){
        updateAdapter=new UpdateAdapter(dataList,context);
        mLRecyclerAdapter=new LRecyclerViewAdapter(updateAdapter);
        recyclerView.setAdapter(mLRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //  mLRecycleView.setPullRefreshEnabled(true);
        //禁用自动加载更多功能
        recyclerView.setLoadMoreEnabled(false);
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(context)
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        recyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //添加foot
        SampleFooter footerView = new SampleFooter(context);
        TextView tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);
        tvFoot.setText("没有更多数据了");
        mLRecyclerAdapter.addFooterView(footerView);
        //设置item click事件  查看目录
        mLRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(activity, MagazineDirectoryActivity.class);
                intent.putExtra("href",DOMIAN+dataList.get(position).getHref());
                startActivity(intent);
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



}
