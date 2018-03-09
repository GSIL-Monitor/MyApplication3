package com.cxy.magazine.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.ClassDetailActivity;
import com.cxy.magazine.util.ACache;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassFragment extends BaseFragment {
    public static final String MAGAZIENE_URL="http://www.fx361.com";
   // private List<HashMap> magazineList;
    private  JSONArray magazineArray;
    private static final int LOAD_FINISHED=100,LOAD_ERROR=101;
    private String errorMessage="出错了，请稍后重试！";
    private MagazineAdapter adapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
  //  private ACache mAcache;
    private Context context=null;


    @BindView(R.id.magazineRv)  LRecyclerView mLRecyclerview;
    private Unbinder unbinder;



    public ClassFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_class, container, false);
        unbinder = ButterKnife.bind(this, view);
        context=this.getActivity();
      //  magazineList =new ArrayList<HashMap>();
   //     mAcache=ACache.get(getContext());
        magazineArray=new JSONArray();
        setLRecyclerview();
        Thread thread=new getHtml();
        thread.start();
        return  view;
    }
    public void setLRecyclerview(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),3);
        mLRecyclerview.setLayoutManager(manager);
        mLRecyclerview.setPullRefreshEnabled(false);
       /* GridItemDecoration divider = new GridItemDecoration.Builder(this.getContext())
                .setHorizontal(R.dimen.activity_horizontal_margin)
                .setVertical(R.dimen.activity_vertical_margin)
                .setColorResource(android.R.color.white)
                .build();
           mLRecyclerview.addItemDecoration(divider);*/
        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_14);
        mLRecyclerview.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(),android.R.color.white));

        mLRecyclerview.setHasFixedSize(true);
        adapter=new MagazineAdapter();
        mLRecyclerViewAdapter=new LRecyclerViewAdapter(adapter);
        mLRecyclerview.setAdapter(mLRecyclerViewAdapter);

    }

    @OnClick(R.id.tv_search)
    public void searchClick(){
        //  Util.toastMessage(getActivity(),"searchView");
        //跳转Fragment
       getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, SearchFragment.newInstance())
                .addToBackStack(null)
                .commit();


    }


    class  getHtml extends Thread{
        @Override
        public void run() {
            try {
             //   Document docHtml=null;
              //  String magazineClass=mAcache.getAsString("magazineClass");
                JSONArray magazineArrayCache=mAcache.getAsJSONArray("magazineArrayCache");
                if (magazineArrayCache!=null){
                    magazineArray=magazineArrayCache;
                }else{
                    if (NetWorkUtils.isNetworkConnected(context)) {
                        Document docHtml = Jsoup.connect(MAGAZIENE_URL).get();
                        JSONArray magazineCache=new JSONArray();

                        Elements kinds = docHtml.getElementsByClass("navBox");
                        for (Element kind : kinds) {

                            Elements alist = kind.getElementsByTag("a");
                            for (Element a : alist) {

                                JSONObject kindObject=new JSONObject();
                                kindObject.put("text", a.text());
                                kindObject.put("href", a.attr("href"));

                                magazineCache.put(kindObject);
                            }

                        }
                        magazineArray=magazineCache;    //等于缓存
                        mAcache.put("magazineArrayCache", magazineCache, 60 * ACache.TIME_DAY);   //缓存两个月
                    }else{
                        errorMessage="网络已断开，请检查网络连接";
                        handler.sendEmptyMessage(LOAD_ERROR);
                    }
                }
                handler.sendEmptyMessage(LOAD_FINISHED);

            } catch (Exception e) {
                e.printStackTrace();
              //  Utils.toastMessage(getActivity(),e.toString());
                handler.sendEmptyMessage(LOAD_ERROR);
            }

        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case LOAD_FINISHED:
                    adapter.notifyDataSetChanged();
                    break;
                case LOAD_ERROR:
                   Utils.toastMessage(getActivity(),errorMessage);
                   break;

            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //adapter
    class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.magazine_class_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try {
                // final HashMap dataMap=magazineList.get(position);
                JSONObject dataJson=magazineArray.getJSONObject(position);
                final  String href=dataJson.getString("href");
                final  String title=dataJson.getString("text");
                holder.tvClassName.setText(title);

                //设置点击事件
                holder.tvClassName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(), ClassDetailActivity.class);
                        intent.putExtra("url",MAGAZIENE_URL+"/"+href);
                        intent.putExtra("title",title);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return magazineArray.length();
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
