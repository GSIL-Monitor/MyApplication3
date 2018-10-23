package com.cxy.magazine.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.cxy.magazine.R;
import com.cxy.magazine.activity.ClassDetailActivity;
import com.cxy.magazine.activity.MainActivity;
import com.cxy.magazine.activity.SearchActivity;
import com.cxy.magazine.util.ACache;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.ClassFooter;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
    private  GridLayoutManager manager=null;
    private Context context=null;
    private List<Integer> localImages=new ArrayList<>();
    private ConvenientBanner convenientBanner=null;
    @BindView(R.id.magazineRv)
    LRecyclerView mLRecyclerview;



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
        Utils.showTipDialog(getActivity(),"加载中...", QMUITipDialog.Builder.ICON_TYPE_LOADING);
        Thread thread=new getHtml();
        thread.start();

        return  view;
    }
    public void setLRecyclerview(){
        manager=new GridLayoutManager(this.getContext(),3);
        mLRecyclerview.setLayoutManager(manager);
        mLRecyclerview.setPullRefreshEnabled(false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_14);
        mLRecyclerview.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(),android.R.color.white));

    //    mLRecyclerview.setHasFixedSize(true);


        adapter=new MagazineAdapter();
        mLRecyclerViewAdapter=new LRecyclerViewAdapter(adapter);
        mLRecyclerview.setAdapter(mLRecyclerViewAdapter);
        //添加footer
        ClassFooter footer=new ClassFooter(getActivity());
        //设置Banner
        convenientBanner= (ConvenientBanner)footer.findViewById(R.id.convenientBanner);
        //加载轮播图本地图片
        loadImages();
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public LocalImageHolderView  createHolder(View itemView) {
                return new LocalImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_banner;
            }
        }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{ R.drawable.ic_indicator_noseleceted,R.drawable.ic_indicator_seleceted})
//                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String miniappid="";
                String path="";
                if (position==0){
                    //跳转诗词歌赋小程序
                    miniappid=Constants.SHICI_APPID;
                    path="pages/shiwen/shiwen";
                }
                if (position==1){
                    //跳转语文助手小程序
                   miniappid=Constants.YUWEN_APPID;
                   path="pages/index/index";
                }
                //跳转小程序
                navigateTominiApp(miniappid,path);
            }
        });
        mLRecyclerViewAdapter.addFooterView(footer);

    }

    public void navigateTominiApp(String miniappId,String pagePath){

        String appId = Constants.WX_APPID; // 填应用AppId
        IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), appId);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = miniappId; // 填小程序原始id    贼坑
        req.path =pagePath;                   //拉起小程序页面的可带参路径，不填默认拉起小程序首页   "pages/index/index";
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        boolean result=api.sendReq(req);
      //  Log.i("com.cxy.magazine","跳转结果"+result);
    }

    public void loadImages(){
        //TODO:添加诗词歌赋 banner
        localImages.add(R.drawable.shicibanner);
        //TODO:添加语文助手 banner
        localImages.add(R.drawable.yuwenbanner);
        //localImages.add(R.drawable.cover_nongye);
    }
    @OnClick(R.id.rl_search)
    public void searchClick(){
        //跳转Fragment
       /* getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, SearchFragment.newInstance())
                .addToBackStack(null)
                .commit();*/
       Intent intent=new Intent(getContext(), SearchActivity.class);
       startActivity(intent);


    }


    class  getHtml extends Thread{
        @Override
        public void run() {
            try {
             //   Document docHtml=null;
              //  String magazineClass=mAcache.getAsString("magazineClass");
                JSONArray magazineArrayCache=mAcache.getAsJSONArray("magazineArrayCache");
                if (magazineArrayCache!=null && magazineArrayCache.length()>=18){
                    magazineArray=magazineArrayCache;
                    handler.sendEmptyMessage(LOAD_FINISHED);
                }else{
                    if (NetWorkUtils.isNetworkConnected(context)) {
                        String html= OkHttpUtil.get(MAGAZIENE_URL);
                        Document docHtml = Jsoup.parse(html);
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

                        handler.sendEmptyMessage(LOAD_FINISHED);
                    }else{
                        errorMessage="网络已断开，请检查网络连接";
                        handler.sendEmptyMessage(LOAD_ERROR);
                    }
                }


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
                    Utils.dismissDialog();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case LOAD_ERROR:
                    Utils.dismissDialog();
                   Utils.toastMessage(getActivity(),errorMessage);
                   break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //banner开始自动翻页
        convenientBanner.startTurning(3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        //banner停止翻页
        convenientBanner.stopTurning();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //adapter
    class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.MyViewHolder>{
        int[] backgroundRecources={R.drawable.cover_shizheng,R.drawable.cover_shangye,R.drawable.cover_wenxue,R.drawable.cover_sheying,
                                     R.drawable.cover_xuesheng,R.drawable.cover_jiating,R.drawable.cover_lvyou,R.drawable.cover_renwen,
                                     R.drawable.cover_wenzhai,R.drawable.cover_yishu,R.drawable.cover_nongye,R.drawable.cover_wenhua,
                                     R.drawable.cover_zhichang, R.drawable.cover_yule, R.drawable.cover_xueshu, R.drawable.cover_junshi,
                                     R.drawable.cover_qiche, R.drawable.cover_huanshi};
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
                //设置TextView背景图片
                holder.tvClassName.setBackgroundResource(backgroundRecources[position]);
                //设置TextView字体加粗，中文需这样设置
                TextPaint tp = holder.tvClassName.getPaint();
                tp.setFakeBoldText(true);
                //动态设置TextView的高度,高度是宽度的4/5
                int height=((manager.getWidth()-28))/3*4/5;
                ViewGroup.LayoutParams param=holder.tvClassName.getLayoutParams();
                param.height = height;
                holder.tvClassName.setLayoutParams(param);

                //设置点击事件
                holder.tvClassName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(), ClassDetailActivity.class);
                        intent.putExtra("url",MAGAZIENE_URL+href);
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

    public class LocalImageHolderView extends Holder<Integer>{


        ImageView bannerImage;

        public LocalImageHolderView(View itemView) {
            super(itemView);
            bannerImage=(ImageView)itemView.findViewById(R.id.image_banner);
            bannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        protected void initView(View itemView) {

        }

        @Override
        public void updateUI(Integer data) {
           bannerImage.setImageResource(data);
        }
    }


}



