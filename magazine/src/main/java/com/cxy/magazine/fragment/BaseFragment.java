package com.cxy.magazine.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    protected Context context = null;
    protected ACache mAcache;
    protected Activity activity;

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";


    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }*/

        context = this.getContext();
        mAcache = ACache.get(context);
        activity = this.getActivity();
        if (!NetWorkUtils.isNetworkConnected(context)) {
            Utils.toastMessage(this.getActivity(), "网络已断开，请检查网络连接");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

       // outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }



}
