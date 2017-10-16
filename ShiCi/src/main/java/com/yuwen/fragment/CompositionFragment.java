package com.yuwen.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.yuwen.activity.CompositionDetailActivity;
import com.yuwen.activity.LoginActivity;
import com.yuwen.activity.MemberActivity;
import com.yuwen.bmobBean.Collect;
import com.yuwen.bmobBean.Member;
import com.yuwen.bmobBean.SelectCount;
import com.yuwen.bmobBean.User;
import com.yuwen.entity.Composition;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Divider;
import com.yuwen.tool.NetworkConnection;
import com.yuwen.tool.OkHttpUtil;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import android.support.v7.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class CompositionFragment extends Fragment
{

    private Spinner spinnerGrade,spinnerType,spinnerFontSum,spinnerLevel;
    private List<Map<String,Object>>  gradeList,typeList,fontSumList,levelList;
    private List<Composition> compositionList;
    private SimpleAdapter gradeAdapter,typeAdapter,fontSumAdapter,levelAdapter;
    private static String urlType="http://zuowen.api.juhe.cn/zuowen/typeList";
    private static String urlBase="http://zuowen.api.juhe.cn/zuowen/baseList";
    private static String urlContent="http://zuowen.api.juhe.cn/zuowen/content";
    public static final String APPKEY_COMPOSITION ="cf7b0e43bbe17e82cc632163361ccfcf";  //作文key
    private  final Integer SELECT_TOTAL_COUNT=3;
    private TextView btnConfirm;
    private Integer gradeId,typeId,wordId,level;
    private RecyclerView compositionLv;
    private Map<Integer,String> gradeMap,typeMap,fontSumMap,levelMap;
    private LayoutInflater mInflater;
    private CompositionAdapter compositionAdapter;
    private LinearLayoutManager mLayoutManager;
    private String error="出错了";
    private View layoutView;
    private int lastVisibleItem = 0;  //scrowview的最后一个可见item的position
    private int page=1,pages=0;
    boolean isLoading = false;//用来控制进入getdata()的次数
    boolean isFirst=true;
    User user=null;
    SimpleDateFormat sdf;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_composition, null);
      /*  Toolbar toolbar = (Toolbar)layoutView .findViewById(R.id.compositiontoolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
*/
        spinnerGrade=(Spinner)layoutView.findViewById(R.id.grade);
        spinnerType=(Spinner)layoutView.findViewById(R.id.comTheme);
        spinnerFontSum=(Spinner)layoutView.findViewById(R.id.fontNum);
        spinnerLevel=(Spinner)layoutView.findViewById(R.id.comLevel);
        btnConfirm=(TextView)layoutView.findViewById(R.id.btnConfirm);
        compositionLv=(RecyclerView)layoutView.findViewById(R.id.comListview);

        gradeMap=new HashMap<Integer, String>();
        typeMap=new HashMap<Integer, String>();
        fontSumMap=new HashMap<Integer, String>();
        levelMap=new HashMap<Integer, String>();

       // mInflater = LayoutInflater.from(this);
        mInflater =inflater;

        Map<String,Object> gradeMap=new HashMap<String,Object>();
        gradeMap.put("name","年级");
        gradeMap.put("id",0);

        Map<String,Object> typeMap=new HashMap<String,Object>();
        typeMap.put("name","题材");
        typeMap.put("id",0);

        Map<String,Object> fontMap=new HashMap<String,Object>();
        fontMap.put("name","字数");
        fontMap.put("id",0);

        Map<String,Object> levelMap=new HashMap<String,Object>();
        levelMap.put("name","等级");
        levelMap.put("id",0);

        gradeList=new ArrayList<Map<String,Object>>();
        typeList=new ArrayList<Map<String,Object>>();
        fontSumList=new ArrayList<Map<String,Object>>();
        levelList=new ArrayList<Map<String,Object>>();

        gradeList.add(gradeMap);
        typeList.add(typeMap);
        fontSumList.add(fontMap);
        levelList.add(levelMap);


        gradeAdapter=new SimpleAdapter(getActivity(),gradeList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        typeAdapter=new SimpleAdapter(getActivity(),typeList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        fontSumAdapter=new SimpleAdapter(getActivity(),fontSumList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});
        levelAdapter=new SimpleAdapter(getActivity(),levelList,android.R.layout.simple_spinner_item,new String[]{"name"},new int[]{android.R.id.text1});


        //设置样式
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //加载适配器
        spinnerGrade.setAdapter(gradeAdapter);
        spinnerType.setAdapter(typeAdapter);
        spinnerFontSum.setAdapter(fontSumAdapter);
        spinnerLevel.setAdapter(levelAdapter);

        spinnerGrade.setOnItemSelectedListener(itemSelectedListener);
        spinnerType.setOnItemSelectedListener(itemSelectedListener);
        spinnerFontSum.setOnItemSelectedListener(itemSelectedListener);
        spinnerLevel.setOnItemSelectedListener(itemSelectedListener);

        //设置固定大小
        compositionLv.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(getActivity());
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        compositionLv.setLayoutManager(mLayoutManager);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        compositionLv.setHasFixedSize(true);
       /* //添加间隔线
        Divider divider = new Divider(new ColorDrawable(0xffcccccc), OrientationHelper.VERTICAL);
        //单位:px
        divider.setMargin(8, 8, 8, 0);
        divider.setHeight(2);
        compositionLv.addItemDecoration(divider);*/


        sdf = new SimpleDateFormat("yyyy-MM-dd");



        compositionList=new ArrayList<Composition>();

        compositionAdapter=new CompositionAdapter(compositionList,CompositionAdapter.STATE_LOAD_FINISH);
        compositionLv.setAdapter(compositionAdapter);

        compositionLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                    if (lastVisibleItem + 1 == compositionAdapter.getItemCount() && !isLoading && lastVisibleItem != 0) {
                        if (page > pages) {
                            isLoading = true;
                            compositionAdapter.refresh(CompositionAdapter.STATE_NO_MORE);
                        } else {
                            isLoading = true;
                            compositionAdapter.refresh(CompositionAdapter.STATE_LOADING);
                            getDataList();

                        }


                    }


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFirst=true;
                page=1;
                getDataList();

            }
        });



        Thread thread=new Thread(runnable);
        thread.start();




        return layoutView;
    }



    @Override
    public void onStart() {
        super.onStart();
        user= BmobUser.getCurrentUser(User.class);

    }

    public void getDataList(){

    final  Map mapBase=new HashMap<String,Object>();
  //  final  List<Composition> list=new ArrayList<Composition>();
    mapBase.put("key",APPKEY_COMPOSITION);
    mapBase.put("page",page++);
    if (gradeId != 0) {
        mapBase.put("gradeId",gradeId);
    }
    if (typeId!=0){
        mapBase.put("typeId",typeId);

    }
    if (wordId!=0){
        mapBase.put("wordId",wordId);
    }
    if (level!=0){
        mapBase.put("level",level);
    }

    Thread baseThread=new Thread(new Runnable() {
        @Override
        public void run() {
            {

                try {
                    //发送请求
                    //String baseData= NetworkConnection.net(urlBase,mapBase,"GET");
                     String baseData= OkHttpUtil.get(urlBase,mapBase);
                     List<Composition> list=resolveBasestr(baseData);
                    if (list.size()>0){
                        if (isFirst){
                            isFirst=false;
                            compositionList=new ArrayList<Composition>();
                            compositionList.addAll(list);
                            handler.sendEmptyMessage(105);  //点击了按钮，重新设置适配器
                        }else{
                            compositionList.addAll(list);
                            handler.sendEmptyMessage(104);  //加载更多
                        }
                    }else{
                        if (isFirst){
                            compositionList=new ArrayList<Composition>();
                            handler.sendEmptyMessage(106);  //没有查询到数据
                        }else{
                            handler.sendEmptyMessage(107);  //没有更多数据了
                        }
                    }


                } catch (Exception e) {
                    error=e.getMessage();
                    handler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        }
    });


     baseThread.start();


}


//

   Runnable runnable=new Runnable() {
       @Override
       public void run() {
           try {
               Map mapGrade=new HashMap<String,Object>();
               mapGrade.put("key",APPKEY_COMPOSITION);
               mapGrade.put("id",1);
               String gradeData= NetworkConnection.net(urlType,mapGrade,"GET");

               Map mapType=new HashMap<String,Object>();
               mapType.put("key",APPKEY_COMPOSITION);
               mapType.put("id",2);
               String typeData= NetworkConnection.net(urlType,mapType,"GET");

               Map mapFont=new HashMap<String,Object>();
               mapFont.put("key",APPKEY_COMPOSITION);
               mapFont.put("id",3);
               String fontData= NetworkConnection.net(urlType,mapFont,"GET");

               Map mapLevel=new HashMap<String,Object>();
               mapLevel.put("key",APPKEY_COMPOSITION);
               mapLevel.put("id",4);
               String levelData= NetworkConnection.net(urlType,mapLevel,"GET");

               resolveJson(gradeData,typeData,fontData,levelData);

           } catch (Exception e) {

               e.printStackTrace();
           }

       }
   };

//解析json数据
public void resolveJson(String gradeData,String typeData,String fontData, String levelData){
    try {
        //解析年级json
        JSONObject gradeJson=new JSONObject(gradeData);
        if (gradeJson.getInt("error_code")==0){   //请求成功
           // gradeList.clear() ;
            JSONArray resultArray=gradeJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                gradeMap.put(resultArray.getJSONObject(i).getInt("id"),resultArray.getJSONObject(i).getString("name"));

                gradeList.add(map);

            }

            handler.sendEmptyMessage(100);
           // gradeAdapter.notifyDataSetChanged();

        }else{
         //  Log.i(TAG,gradeJson.get("error_code")+":"+gradeJson.get("reason"));
            error=gradeJson.get("error_code")+":"+gradeJson.get("reason");
            handler.sendEmptyMessage(0);
        }

        //解析题材json
        JSONObject typeJson=new JSONObject(typeData);
        if (typeJson.getInt("error_code")==0){   //请求成功
          //  typeList.clear() ;
            JSONArray resultArray=typeJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                typeMap.put(resultArray.getJSONObject(i).getInt("id"),resultArray.getJSONObject(i).getString("name"));

                typeList.add(map);

            }

            handler.sendEmptyMessage(101);
            // gradeAdapter.notifyDataSetChanged();

        }else{
           // Log.i(TAG,typeJson.get("error_code")+":"+typeJson.get("reason"));
            error=typeJson.get("error_code")+":"+typeJson.get("reason");
            handler.sendEmptyMessage(0);
        }

        //解析字数json
        JSONObject fontJson=new JSONObject(fontData);
        if (fontJson.getInt("error_code")==0){   //请求成功
          //  fontSumList.clear() ;
            JSONArray resultArray=fontJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                fontSumMap.put(resultArray.getJSONObject(i).getInt("id"),resultArray.getJSONObject(i).getString("name"));
                fontSumList.add(map);

            }

            handler.sendEmptyMessage(102);
            // gradeAdapter.notifyDataSetChanged();

        }else{
            //Log.i(TAG,fontJson.get("error_code")+":"+fontJson.get("reason"));
            error=fontJson.get("error_code")+":"+fontJson.get("reason");
            handler.sendEmptyMessage(0);
        }


        //解析等级json
        JSONObject levelJson=new JSONObject(levelData);
        if (levelJson.getInt("error_code")==0){   //请求成功
          //  levelList.clear() ;
            JSONArray resultArray=levelJson.getJSONArray("result");
            for (int i=0;i<resultArray.length();i++){
                Map<String,Object> map=new HashMap<String, Object>() ;

                map.put("name",resultArray.getJSONObject(i).getString("name"));
                map.put("id",resultArray.getJSONObject(i).getInt("id"));

                levelMap.put(resultArray.getJSONObject(i).getInt("id"),resultArray.getJSONObject(i).getString("name"));


                levelList.add(map);

            }

            handler.sendEmptyMessage(103);


        }else{
           // Log.i(TAG,levelJson.get("error_code")+":"+levelJson.get("reason"));
            error=levelJson.get("error_code")+":"+levelJson.get("reason");
            handler.sendEmptyMessage(0);
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


}

    public List<Composition> resolveBasestr(String baseString) {

        List<Composition> comList = new ArrayList<Composition>();
        try {
            JSONObject baseJson = new JSONObject(baseString);
            if (baseJson.getInt("error_code") == 0) {   //请求成功
                JSONObject resultObject = baseJson.getJSONObject("result");
                JSONArray itemArray = resultObject.getJSONArray("list");
            if (isFirst){  //如果是第一次请求计算总页数,清空List

                int count=resultObject.getInt("totalCount"); //总记录数
                pages=count%50==0?count/50:count/50+1;


            }

                for (int i = 0; i < itemArray.length(); i++) {
                    Composition composition = new Composition();
                    JSONObject itemObject = itemArray.getJSONObject(i);
                    composition.setId(itemObject.getInt("id"));
                    composition.setName(itemObject.getString("name"));
                    composition.setGrade(gradeMap.get(itemObject.getInt("gradeId")));
                    composition.setLevel(levelMap.get(itemObject.getInt("level")));
                    composition.setType(typeMap.get(itemObject.getInt("typeId")));
                    composition.setWord(fontSumMap.get(itemObject.getInt("wordId")));
                    composition.setTime(itemObject.getString("time"));
                    composition.setWriter(itemObject.getString("writer"));
                    comList.add(composition);
                }
            } else {
                //  Log.i(TAG,baseJson.get("error_code")+":"+baseJson.get("reason"));
                error = baseJson.get("error_code") + ":" + baseJson.get("reason");
                handler.sendEmptyMessage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comList;
    }






   Handler handler=new Handler() {
       @Override
       public void handleMessage(Message msg) {

          switch (msg.what){
              case 100:
                  sortList(gradeList);
                  gradeAdapter.notifyDataSetChanged();

                  break;
              case 101:
                  sortList(typeList);
                  typeAdapter.notifyDataSetChanged();
                  break;
              case 102:
                  sortList(fontSumList);
                  fontSumAdapter.notifyDataSetChanged();
                  break;
              case 103:
                  sortList(levelList);
                  levelAdapter.notifyDataSetChanged();
                  break;
              case 104:  //加载完成
                  isLoading=false;
                  compositionAdapter.refresh(CompositionAdapter.STATE_LOAD_FINISH);

                   break;
              case 105:  //重新设置adapter
                  compositionAdapter=new CompositionAdapter(compositionList,CompositionAdapter.STATE_LOAD_FINISH);
                  compositionLv.setAdapter(compositionAdapter);

                  break;
              case 106:  //没有查询到相关数据
                  compositionAdapter=new CompositionAdapter(compositionList,CompositionAdapter.STATE_NO_SELECT);
                  compositionLv.setAdapter(compositionAdapter);

                  break;
              case 107:  //没有加载到更多数据
                  isLoading=true;
                  compositionAdapter.refresh(CompositionAdapter.STATE_NO_MORE);

                  break;

              case 0:
                  Util.showResultDialog(getActivity(),error,"出错了");
                  break;

          }
       }
   };


   public void sortList(List<Map<String,Object>> list){
       Collections.sort(list, new Comparator<Map<String, Object>>() {
           @Override
           public int compare(Map<String, Object> map1, Map<String, Object> map2) {
               Integer id1=(Integer) map1.get("id");
               Integer id2=(Integer) map2.get("id");
               return id1.compareTo(id2);
           }
       });
   }


    AdapterView.OnItemSelectedListener itemSelectedListener=new  AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId()==R.id.grade){
                  // Util.toastMessage(CompositionActivity.this,gradeList.get(i).get("name").toString());
                   gradeId=(Integer)gradeList.get(i).get("id");

               }
            if (adapterView.getId()==R.id.comTheme){
                //Util.toastMessage(CompositionActivity.this,typeList.get(i).get("name").toString());
                typeId=(Integer)typeList.get(i).get("id");
            }
            if (adapterView.getId()==R.id.fontNum){
                //Util.toastMessage(CompositionActivity.this,fontSumList.get(i).get("name").toString());
                wordId=(Integer)fontSumList.get(i).get("id");
            }

            if (adapterView.getId()==R.id.comLevel){
                //Util.toastMessage(CompositionActivity.this,levelList.get(i).get("name").toString());
                level=(Integer)levelList.get(i).get("id");
            }




        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };




    class CompositionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_FOOTER = 0;//两种状态 footview显示的时候
        private static final int TYPE_ITEM = 1;   //和不显示的一般item

        private static final int STATE_LOADING=1;
        private static final int STATE_LOAD_FINISH=2;
        private static final int STATE_NO_MORE=3;
        private static final int STATE_NO_SELECT=4;
        private int state=0;  //默认没有数据
        private List<Composition> list;

        public CompositionAdapter( List<Composition> compositionList,int state) {
            this.list=compositionList;
            this.state=state;

        }

        @Override
        public int getItemCount() {
            return list.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;

            }

        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View view = mInflater.inflate(R.layout.composition_item, parent, false);
                CompositionViewHolder viewHolder = new CompositionViewHolder(view);
                return viewHolder;
            } else if (viewType == TYPE_FOOTER) {
                View footerView=mInflater.inflate(R.layout.foot_boot,parent,false);
                return new CompostionFooterHolder(footerView);
            }

            return null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CompostionFooterHolder) {
                CompostionFooterHolder footerHolder = (CompostionFooterHolder) holder;

                if (state == STATE_NO_MORE) {

                    footerHolder.progressBar.setVisibility(View.GONE);
                    footerHolder.tvHint.setText("没有更多数据了");

                }
                if (state == STATE_LOADING) {

                    footerHolder.progressBar.setVisibility(View.VISIBLE);
                    footerHolder.tvHint.setText("加载中...");

                }
                if (state == STATE_LOAD_FINISH) {
                    footerHolder.progressBar.setVisibility(View.GONE);
                    footerHolder.tvHint.setText("");

                }
                if (state == STATE_NO_SELECT) {
                    footerHolder.progressBar.setVisibility(View.GONE);
                    footerHolder.tvHint.setText("没有查询到相关数据");
                }
            }



            else {     //正常的item
                final  Composition composition = list.get(position);

                CompositionViewHolder  compositionHolder=(CompositionViewHolder)holder;
                compositionHolder.name.setText(composition.getName());
                compositionHolder.type.setText(composition.getType());
                compositionHolder.time.setText(composition.getTime());
                compositionHolder.writer.setText(composition.getWriter());
                compositionHolder.word.setText(composition.getWord());
                compositionHolder.level.setText(composition.getLevel());

                compositionHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1、首先查看当前用户是否为会员
                        if (user != null) {
                            BmobQuery<Member> query = new BmobQuery<Member>();
                            query.addWhereEqualTo("user", user);
                            query.findObjects(new FindListener<Member>() {
                                @Override
                                public void done(List<Member> list, BmobException e) {
                                    if (e == null) {
                                        seeComposition(list, composition);


                                    } else {
                                        Util.toastMessage(getActivity(), "查询会员状态出错");

                                    }
                                }
                            });
                        } else {  //没有登录
                            AlertDialog dlg = new AlertDialog.Builder(getActivity()).setMessage("查看作文,需要先登录！")
                                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null).create();
                            dlg.setCanceledOnTouchOutside(false);
                            dlg.show();
                        }


                    }
                });
            }


        }

        public List<Member> checkMemberState(){
           final  List<Member> memberList=new ArrayList<Member>();

            return memberList;

        }
        public void seeComposition(List<Member> memeberList,final Composition composition ) {
            if (memeberList.size() > 0) {  //是会员
                //2、如果是会员查询会员是否已经过期
                Member queryMember = memeberList.get(0);
                String finishTime = queryMember.getFinishTime();  //数据库里存储的会员到期时间
                Calendar nowCal = Calendar.getInstance();  //当前日期
                Calendar finishCal = Calendar.getInstance();   //会员到期日期
                try {
                    nowCal.setTime(sdf.parse((sdf.format(new Date()))));
                    finishCal.setTime(sdf.parse(finishTime));
                    int value = finishCal.compareTo(nowCal);
                    if (value == -1) {   //会员已经过期
                        BmobQuery<SelectCount> query = new BmobQuery<SelectCount>();
                        query.addWhereEqualTo("user", user);
                        query.addWhereEqualTo("selectDate", sdf.format(new Date()));
                        query.addWhereEqualTo("selectType", SelectCount.COMPOSITION);
                        query.findObjects(new FindListener<SelectCount>() {
                            @Override
                            public void done(List<SelectCount> list, BmobException e) {
                                if (e == null) {
                                    if (list.size() == 0) {  //

                                        AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("你的会员已经过期,现在每天只能免费查看3篇作文")
                                                .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(getActivity(), MemberActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("继续查看", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        selectComposition(composition);
                                                        insertData();


                                                    }
                                                }).create();
                                        dlg.setCanceledOnTouchOutside(false);
                                        dlg.show();


                                    } else if (list.get(0).getSelectCount() < SELECT_TOTAL_COUNT) {//查询次数小于3次，正常查询，更新selectCount表的数据
                                        selectComposition(composition);

                                        SelectCount selectCount = list.get(0);
                                        updateData(selectCount);


                                    } else { //当日查询次数已经超过三次，提醒用户去充值
                                        AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("你今日的免费查询次数已使用完")
                                                .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(getActivity(), MemberActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("取消", null).create();
                                        dlg.setCanceledOnTouchOutside(false);
                                        dlg.show();

                                    }

                                } else {
                                    Log.i("bmob", "查询次数失败：" + e.getMessage() + "," + e.getErrorCode());
                                }

                            }
                        });   //


                    } else {  //会员还没过期，正常查询
                        selectComposition(composition);

                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }


            } else { //不是会员
                //3、如果不是会员，查询当天的查询次数是否大于3
                BmobQuery<SelectCount> query = new BmobQuery<SelectCount>();
                query.addWhereEqualTo("user", user);
                query.addWhereEqualTo("selectDate", sdf.format(new Date()));
                query.addWhereEqualTo("selectType", SelectCount.COMPOSITION);
                query.findObjects(new FindListener<SelectCount>() {
                    @Override
                    public void done(List<SelectCount> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {  //正常查询，向selectCount表插入一条数据

                                AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("你当前是免费用户，每天只能免费查看3篇作文")
                                        .setNegativeButton("继续查看", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                selectComposition(composition);
                                                insertData();


                                            }
                                        }).setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getActivity(), MemberActivity.class);
                                                startActivity(intent);
                                            }
                                        }).create();
                                dlg.setCanceledOnTouchOutside(false);
                                dlg.show();


                            } else if (list.get(0).getSelectCount() < SELECT_TOTAL_COUNT) {//查询次数小于3次，正常查询，更新selectCount表的数据
                                selectComposition(composition);

                                SelectCount selectCount = list.get(0);
                                updateData(selectCount);


                            } else { //当日查询次数已经超过三次，提醒用户去充值
                                AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("你今日的免费查询次数已使用完")
                                        .setPositiveButton("去充值会员", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getActivity(), MemberActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("取消", null).create();
                                dlg.setCanceledOnTouchOutside(false);
                                dlg.show();

                            }

                        } else {
                            Log.i("bmob", "查询次数失败：" + e.getMessage() + "," + e.getErrorCode());
                        }

                    }
                });   //
            }
        }

        public void insertData(){
            SelectCount selectCount=new SelectCount();
            selectCount.setUser(user);
            selectCount.setSelectDate(sdf.format(new Date()));
            selectCount.setSelectType(SelectCount.COMPOSITION);
            selectCount.setSelectCount(1);
            selectCount.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e==null){
                        Log.i("bmob","插入数据成功");
                    }
                    else{
                        Log.i("bmob","插入数据失败："+e.getMessage()+","+e.getErrorCode());
                    }

                }
            });

        }

        public void updateData(SelectCount selectCount){
            selectCount.setSelectCount(selectCount.getSelectCount()+1);
            selectCount.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        Log.i("bmob","更新数据成功");
                    }
                    else{
                        Log.i("bmob","更新数据失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }

        //查询作文详细内容
        public void selectComposition(final Composition composition){
            final Map paramMap=new HashMap();
            paramMap.put("key",APPKEY_COMPOSITION);
            paramMap.put("id",composition.getId());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String compositionDetail= OkHttpUtil.get(urlContent,paramMap);
                        JSONObject compositionJson=new JSONObject(compositionDetail);
                        if (compositionJson.getInt("error_code")==0) {   //请求成功
                            JSONObject resultObject=compositionJson.getJSONObject("result");
                            String contentStr=resultObject.getString("content");
                            String content=contentStr.replace("<p>","").replace("</p>","\n\n");
                            composition.setContent(content);
                            composition.setComment(resultObject.getString("comment"));
                            composition.setSchool(resultObject.getString("school"));
                            composition.setTeacher(resultObject.getString("teacher"));
                            //跳转Activity
                            Intent intent=new Intent(getActivity(),CompositionDetailActivity.class);
                            intent.putExtra("composition", composition);
                            startActivity(intent);


                        }
                        else{
                            //  Log.i(TAG,compositionJson.get("error_code")+":"+compositionJson.get("reason"));
                            error=compositionJson.get("error_code")+":"+compositionJson.get("reason");
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();//
        }

        public void refresh(int state){

            this.state=state;

            this.notifyDataSetChanged();




        }


        class CompositionViewHolder extends RecyclerView.ViewHolder {
            TextView name, type, word, level, writer, time;
            LinearLayout container;

            public CompositionViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                level = (TextView) view.findViewById(R.id.level);
                time = (TextView) view.findViewById(R.id.time);
                type = (TextView) view.findViewById(R.id.type);
                word = (TextView) view.findViewById(R.id.word);
                writer = (TextView) view.findViewById(R.id.writer);
                container=(LinearLayout)view.findViewById(R.id.compositionContainer);


            }

        }

        class CompostionFooterHolder extends RecyclerView.ViewHolder{
            TextView tvHint;
            ProgressBar progressBar;
            LinearLayout layout;

            public CompostionFooterHolder(View itemView) {
                super(itemView);
                this.tvHint =(TextView) itemView.findViewById(R.id.mLoad);
                this.progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar) ;
                this.layout=(LinearLayout)itemView.findViewById(R.id.foot_container);
            }
        }
    }
}
