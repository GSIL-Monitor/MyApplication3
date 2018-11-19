package com.cxy.magazine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.activity.MagazineContentActivity;
import com.cxy.magazine.activity.MagazineDirectoryActivity;
import com.cxy.magazine.adapter.UpdateAdapter;
import com.cxy.magazine.entity.RankEntity;
import com.cxy.magazine.entity.UpdateMagazine;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.Utils;
import com.cxy.magazine.view.SampleFooter;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends BaseFragment {

    private static final String DOMIAN = "http://www.fx361.com";

    private List<UpdateMagazine> dataList;

    @BindView(R.id.update_lr)
    LRecyclerView recyclerView;

    @BindView(R.id.emptyView)
    QMUIEmptyView emptyView;

    UpdateAdapter updateAdapter=null;
    LRecyclerViewAdapter mLRecyclerAdapter=null;

    private List<NativeExpressADView> mAdViewList;
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();

    public UpdateFragment() {
        // Required empty public constructor
    }


    public static UpdateFragment newInstance() {
        UpdateFragment fragment = new UpdateFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    if (getArguments() != null) {
            dataList = getArguments().getParcelableArrayList(ARG_PARAM);
            System.out.println("123");

        }*/
    }

    private Unbinder unbinder=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_update, container, false);
        unbinder=ButterKnife.bind(this,rootView);
        parseHtml();
        return  rootView;
    }

    public void  parseHtml(){
        emptyView.show(true);
        final String httpUrl="http://www.fx361.com/";
        dataList=new ArrayList<>();
        //解析排行榜数据和更新数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String html= OkHttpUtil.get(httpUrl);
                    Document document = Jsoup.parse(html);


                    //获取更新榜单
                    //获取右侧边栏
                    Element siderBar=document.getElementsByClass("sidebarR").first();
                    Elements lis=siderBar.getElementsByClass("list_01").first().getElementsByTag("li");
                    for (Element li : lis){
                        UpdateMagazine updateMagazine=new UpdateMagazine();
                        String tiltle=li.getElementsByTag("a").first().text();
                        String href=li.getElementsByTag("a").first().attr("href");
                        updateMagazine.setTitle(tiltle);
                        updateMagazine.setHref(href);

                        dataList.add(updateMagazine);
                    }

                    uiHandler.sendEmptyMessage(100);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Todo:解析排行榜数据失败
                    uiHandler.sendEmptyMessage(101);
                }


            }
        }).start();
    }

    private Handler uiHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what==100){
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                setRecyclerView();
            }
            if (msg.what==101){
                //Utils.showTipDialog(context,"加载数据失败，请稍后重试",QMUITipDialog.Builder.ICON_TYPE_FAIL);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.show(false, "加载失败", "请检查网络是否能正常连接", "点击重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parseHtml();
                    }
                });
            }
        }
    };
    public void setRecyclerView(){
        updateAdapter=new UpdateAdapter(dataList,context,mAdViewPositionMap);
        mLRecyclerAdapter=new LRecyclerViewAdapter(updateAdapter);
        recyclerView.setAdapter(mLRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //禁止下拉刷新
        recyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        recyclerView.setLoadMoreEnabled(false);
        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(context)
                .setHeight(R.dimen.thin_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        recyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //添加foot
        SampleFooter footerView = new SampleFooter(context);
        TextView tvFoot=(TextView)footerView.findViewById(R.id.tv_foot);
        tvFoot.setText("没有更多数据了");
        mLRecyclerAdapter.addFooterView(footerView);
        //设置item click事件  查看目录
        mLRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAdViewPositionMap.containsValue(position)){
                    return;
                }else{
                    Intent intent=new Intent(activity, MagazineDirectoryActivity.class);
                    intent.putExtra("href",DOMIAN+dataList.get(position).getHref());
                    startActivity(intent);
                }

            }
        });

       //加载广告 9条
        initAd();
    }

    private  static  final int AD_COUNT=9;
    public void initAd(){
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        AdListener adListener=new AdListener();
        NativeExpressAD  mADManager = new NativeExpressAD(context, adSize, Constants.APPID, Constants.UPDATE_AD_ID, adListener);
        mADManager.loadAD(AD_COUNT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class AdListener implements NativeExpressAD.NativeExpressADListener {

        private  int initAdPostion=9;
        private  static  final int ITEMS_PER_AD=10;
        @Override
        public void onNoAD(AdError adError) {

        }

        @Override
        public void onADLoaded(List<NativeExpressADView> adList) {
          //  Log.i(TAG, "onADLoaded: " + adList.size());
            mAdViewList = adList;
            for (int i = 0; i < mAdViewList.size(); i++) {

                if (initAdPostion < dataList.size()+i) {
                    NativeExpressADView view = mAdViewList.get(i);
                    // GDTLogger.i("ad load[" + i + "]: " + getAdInfo(view));
                    mAdViewPositionMap.put(view, initAdPostion); // 把每个广告在列表中位置记录下来
                    updateAdapter.addADViewToPosition(initAdPostion, mAdViewList.get(i));
                    initAdPostion = initAdPostion + ITEMS_PER_AD ;
                }
            }
            mLRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRenderFail(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADExposure(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADClicked(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADClosed(NativeExpressADView adView) {
            if (updateAdapter != null) {
                int removedPosition = mAdViewPositionMap.get(adView);
                updateAdapter.removeADView(removedPosition, adView);
            }
        }

        @Override
        public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

        }
    }



}
