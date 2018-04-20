package com.cxy.magazine.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.util.ACache;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    protected Context context=null;
    protected ACache mAcache;
    protected Activity activity;



    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this.getContext();
        mAcache=ACache.get(context);
        activity=this.getActivity();
        if (!NetWorkUtils.isNetworkConnected(context)){
            Utils.toastMessage(this.getActivity(),"网络已断开，请检查网络连接");
        }
    }






}
