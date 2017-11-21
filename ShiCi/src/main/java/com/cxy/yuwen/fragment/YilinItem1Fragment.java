package com.cxy.yuwen.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Divider;
import com.cxy.yuwen.tool.ParcelableMap;
import com.cxy.yuwen.tool.YilinAdapter;

import java.util.ArrayList;


public class YilinItem1Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<ParcelableMap> dataList;
    private RecyclerView recyclerView;

    public YilinItem1Fragment() {

    }


    public static YilinItem1Fragment newInstance(ArrayList<ParcelableMap> list) {
        YilinItem1Fragment fragment = new YilinItem1Fragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1,list);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataList = getArguments().getParcelableArrayList(ARG_PARAM1);
            Log.i("yilin",dataList.toString());
            //  Util.toastMessage(getActivity(),dataList.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutview = inflater.inflate(R.layout.fragment_yilin_item1, container, false);
        recyclerView = (RecyclerView) layoutview.findViewById(R.id.yilin_rv);

        //设置RecycleView
        //设置固定大小
        recyclerView.setHasFixedSize(true);
        //创建线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //垂直方向
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //添加间隔线
        Divider divider = new Divider(new ColorDrawable(0xffcccccc), OrientationHelper.VERTICAL);
        //单位:px
        divider.setMargin(8, 8, 8, 0);
        divider.setHeight(3);
        recyclerView.addItemDecoration(divider);
        YilinAdapter adapter=new YilinAdapter(getContext(),dataList,YilinAdapter.DIRECTORY_FLAG);
        recyclerView.setAdapter(adapter);


        return layoutview;
    }



}

