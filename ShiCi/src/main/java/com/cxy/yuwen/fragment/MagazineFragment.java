package com.cxy.yuwen.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.activity.MagazineActivity;
import com.cxy.yuwen.tool.ACache;
import com.cxy.yuwen.tool.Utils;

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
public class MagazineFragment extends Fragment {

    public static final String MAGAZIENE_URL="http://www.fx361.com";
    private List<HashMap> magazineList;
    private static final int LOAD_FINISHED=100,LOAD_ERROR=101;
    private MagazineAdapter adapter;
    private ACache mAcache;
    Activity activity;

    @BindView(R.id.magazineRv) RecyclerView  magazineRv;
    private Unbinder unbinder;



    public MagazineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_magazine, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity=this.getActivity();
        //设置RecycleView布局为网格布局 3列
        magazineRv.setLayoutManager(new GridLayoutManager(getContext(),3));
        magazineList =new ArrayList<HashMap>();
        mAcache=ACache.get(getContext());
        adapter=new MagazineAdapter();
        magazineRv.setAdapter(adapter);
        Thread thread=new getHtml();
        thread.start();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




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
                Document docHtml=null;
                String magazineClass=mAcache.getAsString("magazineClass");
                if (!Utils.isEmpty(magazineClass)){
                    docHtml=Jsoup.parse(magazineClass);
                }else{
                    docHtml = Jsoup.connect(MAGAZIENE_URL).get();
                    mAcache.put("magazineClass",docHtml.toString(),30* ACache.TIME_DAY);
                }

                Elements kinds=docHtml.getElementsByClass("navBox");
                for (Element kind : kinds){

                    Elements alist=kind.getElementsByTag("a");
                    for (Element a : alist){
                        HashMap kindMap=new HashMap<String,String>();
                        kindMap.put("text",a.text());
                        kindMap.put("href",a.attr("href"));

                        magazineList.add(kindMap);
                    }

                }

                handler.sendEmptyMessage(LOAD_FINISHED);

            } catch (IOException e) {
                e.printStackTrace();
              //  Util.toastMessage(getActivity(),e.toString());
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
                   Utils.toastMessage(getActivity(),"出错了，请稍后再试！");
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
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.magazine_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
               final  HashMap dataMap=magazineList.get(position);
               holder.tvClassName.setText(dataMap.get("text").toString());

              //设置点击事件
               holder.tvClassName.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent=new Intent(getContext(), MagazineActivity.class);
                       intent.putExtra("url",MAGAZIENE_URL+dataMap.get("href").toString());
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
             @BindView(R.id.class_title) TextView tvClassName;


            public MyViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);

            }
        }
    }

}
