package com.cxy.childstory.base;

import android.support.v4.app.Fragment;

import com.cxy.childstory.R;

import butterknife.Unbinder;

public class BaseFragment extends Fragment {

    protected Unbinder unbinder;


    protected  void startFragment(int viewId,Fragment fragment){
        String tagName = fragment.getClass().getSimpleName();
        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(viewId, fragment,tagName)
                .addToBackStack(tagName)
                .commit();
    }

    protected void popBackStack(){
        this.popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder!=null){
            unbinder.unbind();
        }

    }
}
