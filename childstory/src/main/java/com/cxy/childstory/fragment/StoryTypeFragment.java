package com.cxy.childstory.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cxy.childstory.R;
import com.cxy.childstory.base.BaseFragment;
import com.cxy.childstory.decorator.GridDividerItemDecoration;
import com.cxy.childstory.model.ReturnBody;
import com.cxy.childstory.model.StoryType;
import com.cxy.childstory.utils.Constants;
import com.cxy.childstory.utils.HttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryTypeFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private static final String LOG_TAG="STORY_TYPE";
    private TypeAdapter typeAdapter;
    private List<StoryType> typeList;

    public StoryTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_story_type, container,false);
        unbinder=ButterKnife.bind(this,rootView);
        initTopBar();
        initRecyclerView();

        ObtainData obtainData=new ObtainData();
        obtainData.execute();

        return rootView;

    }

    private void initTopBar() {
        mTopBar.setTitle("故事分类");
    }

    private void initRecyclerView(){
        typeList=new ArrayList<>();
        typeAdapter=new TypeAdapter(R.layout.recycler_story_type_item,typeList);

        typeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(getContext(), "onItemClick" + position, Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.setAdapter(typeAdapter);
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), spanCount));

    }

    private  class ObtainData extends AsyncTask<Void, Integer, String>{

        private String obtainUrl= Constants.DOMAIN+"/type/selectall";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String data= HttpUtil.get(obtainUrl);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s!=null){
                Type type = new TypeReference<ReturnBody<List<StoryType>>>() {}.getType();
                ReturnBody<List<StoryType>> typeReturnBody=JSON.parseObject(s, type);

                if (typeReturnBody.getErrorCode().equals("0000")){
                    List<StoryType> list=typeReturnBody.getData();
                    typeList.clear();
                    typeList.addAll(list);

                    //更新UI
                    typeAdapter.notifyDataSetChanged();


                }else{
                    String errorMsg=typeReturnBody.getErrorMsg();
                    Log.e("LOG_TAG",errorMsg);
                }
            }
        }

    }


  private static class TypeAdapter extends BaseQuickAdapter<StoryType,BaseViewHolder>{

      public TypeAdapter(int layoutResId, @Nullable List<StoryType> data) {
          super(layoutResId, data);
      }

      @Override
      protected void convert(BaseViewHolder helper, StoryType item) {
          helper.setText(R.id.tv_type,item.getName());
         //Glide.with(mContext)
      }
  }


}
