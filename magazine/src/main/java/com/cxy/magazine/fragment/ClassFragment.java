package com.cxy.magazine.fragment;


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
import com.cxy.magazine.util.Utils;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

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
public class ClassFragment extends Fragment {
    public static final String MAGAZIENE_URL="http://www.fx361.com";
    private List<HashMap> magazineList;
    private static final int LOAD_FINISHED=100;
    private MagazineAdapter adapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
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
        magazineList =new ArrayList<HashMap>();
        mAcache=ACache.get(getContext());
        setLRecyclerview();
        Thread thread=new getHtml();
        thread.start();
        return  view;
    }
    public void setLRecyclerview(){
        GridLayoutManager manager=new GridLayoutManager(this.getContext(),3);
        mLRecyclerview.setLayoutManager(manager);
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
                Document docHtml=null;
                String magazineClass=mAcache.getAsString("magazineClass");
                if (!TextUtils.isEmpty(magazineClass)){
                    docHtml= Jsoup.parse(magazineClass);
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
                Utils.toastMessage(getActivity(),e.toString());
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
            final HashMap dataMap=magazineList.get(position);
            holder.tvClassName.setText(dataMap.get("text").toString());

            //设置点击事件
            holder.tvClassName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), ClassDetailActivity.class);
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
