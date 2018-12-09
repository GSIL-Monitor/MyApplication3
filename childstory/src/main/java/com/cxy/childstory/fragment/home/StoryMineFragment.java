package com.cxy.childstory.fragment.home;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxy.childstory.R;
import com.cxy.childstory.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryMineFragment extends BaseFragment {


    public StoryMineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QMUIStatusBarHelper.translucent(this.getActivity());
        View rootView= inflater.inflate(R.layout.fragment_story_mine, container, false);
        ButterKnife.bind(this,rootView);
        return  rootView;
    }



}
