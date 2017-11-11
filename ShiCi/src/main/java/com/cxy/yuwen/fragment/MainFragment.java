package com.cxy.yuwen.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxy.yuwen.activity.ChengyuActivity;
import com.cxy.yuwen.activity.CiYuActivity;
import com.cxy.yuwen.activity.ShiciActivity;
import com.cxy.yuwen.activity.ZidianActivity;
import com.cxy.yuwen.entity.Article;
import com.cxy.yuwen.entity.Chengyu;
import com.cxy.yuwen.entity.CiYu;
import com.cxy.yuwen.R;
import com.cxy.yuwen.tool.Adapter;
import com.cxy.yuwen.tool.NetWorkUtils;
import com.cxy.yuwen.tool.NetworkConnection;
import com.cxy.yuwen.tool.OkHttpUtil;
import com.cxy.yuwen.tool.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment implements View.OnClickListener{

    private TextView textView;
    private ListView listView;
    private Button btnZi,btnChengyu,btnPoem,btnCi;

    private String text = "";
    private List list = new ArrayList();
    private Adapter adapter;
    public static int mark;//查询的标志
    public  String httpUrl = "";
    public static final String tag="INFO";
    public static String httpUrl1 = "http://api.avatardata.cn/TangShiSongCi/LookUp?key=9b42454896f54202be3767fd55930654";
    private static final int MSG_LOAD_SUCCESS = 100;   //指示数据已获取
    private static final int MSG_LOADED_fail = 0;   //指示数据未获取
    private static final int MSG_QQ_Name = 1;   //指示数据未获取
    private static final int MSG_QQ_Image = 2;   //指示数据未获取
    public static final int FLAG_CI=1,FLAG_IDIOM=2,FLAG_POEM=3;
    private int pageNum = 1;    //要查询的页数   pageSize=50
    private int pages=0;
    private boolean isBottom;
    private LinearLayout layout;
    private View layoutView;
   // private ImageView mUserLogo;


    //String json = "";
  //  String from = "";

    Article    article;
    Chengyu chengYu;
    CiYu ciYu;
   // User user;

    //底部加载更多布局
    View footer;
    private String title;
    DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_main, null);

       /* title=(String) getTitle();
        Toolbar toolbar = (Toolbar)layoutView .findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/



        textView = (TextView)layoutView.findViewById(R.id.tv);
        listView = (ListView)layoutView.findViewById(R.id.lv);
        btnZi = (Button)layoutView.findViewById(R.id.btnZi);
        btnCi=(Button)layoutView.findViewById(R.id.btnCi);
        btnChengyu=(Button)layoutView.findViewById(R.id.btnIdiom);
        btnPoem=(Button)layoutView.findViewById(R.id.btnPoem);
        adapter = new Adapter(getActivity(), list);
        listView.setAdapter(adapter);

        btnZi.setOnClickListener(this);
        btnCi.setOnClickListener(this);
        btnChengyu.setOnClickListener(this);
        btnPoem.setOnClickListener(this);
        // btnToLogin.setOnClickListener(this);

        //将底部加载一个加载更多的布局
        footer = LayoutInflater.from(getActivity()).inflate(R.layout.foot_boot, null);
        //初始状态为隐藏
        footer.setVisibility(View.GONE);
        //加入到ListView的底部
        listView.addFooterView(footer);

        //setUser();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mark==FLAG_IDIOM){             //查询成语

                    chengYu = (Chengyu) list.get(position);
                    final String nameStr =chengYu.getName();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map map=new HashMap<String,String>();
                            map.put("key",NetworkConnection.APPKEY_CHENGYU);
                            map.put("word",nameStr);

                            try {
                                String dataChengyu= NetworkConnection.net(NetworkConnection.URL_CHENGYU,map,"GET");
                                chengYu = getChengYuContent(dataChengyu,chengYu);
                                Intent intent=new Intent(getActivity(),ChengyuActivity.class);
                                intent.putExtra("chengyu",chengYu);
                                startActivity(intent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();



                }
                if (mark== FLAG_POEM) {
                    article = (Article) list.get(position);
                    final String idStr = article.getId();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String contentJson = OkHttpUtil.get(httpUrl1 + "&id=" + idStr);
                                // Log.i(tag, contentJson);
                                article = getContent(contentJson, article);
                                // System.out.println(contentJson);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //  Log.i(tag,"内容为"+article.getContent());
                            Intent intent = new Intent(getActivity(), ShiciActivity.class);
                            intent.putExtra("article", article);

                            startActivity(intent);

                        }
                    }).start();


                }
                if (mark==FLAG_CI){
                    ciYu =(CiYu)list.get(position);
                    Intent intent = new Intent(getActivity(), CiYuActivity.class);
                    intent.putExtra("ciYu", ciYu);

                    startActivity(intent);
                }
            }

        });//

        //listview滑动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isBottom) {
                        footer.setVisibility(View.VISIBLE);
                        if(pageNum<=pages){
                            Connection connection = new Connection();
                            connection.start();

                        }else{
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setMessage("没有更多内容了!");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    footer.setVisibility(View.GONE);
                                }
                            });

                            AlertDialog alertDialog=builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();


                        }

                    }
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount&&firstVisibleItem!=0) {
                    // 说明:
                    // fistVisibleItem:表示划出屏幕的ListView子项个数
                    // visibleItemCount:表示屏幕中正在显示的ListView子项个数
                    // totalItemCount:表示适配器ListView子项的总数
                    // 前两个相加==最后一个说明ListView滑到底部
                    isBottom = true;
                }else{
                    isBottom = false;
                }
            }




        });//

        return layoutView;
    }



    @Override
    public void onClick(View v) {


            text = textView.getText().toString();
            footer.setVisibility(View.GONE);
            list.clear();
            adapter.notifyDataSetChanged();


            if (text == null || text.length() <= 0) {
                Util.showResultDialog(getActivity(), "请输入要查询的内容!", "提示");

             //   new AlertDialog.Builder(MainActivity.this).setMessage("请输入要查询的内容!").setPositiveButton("确定", null).create().show();

            } else {
                //   Log.i(tag,listviewState+"");

                boolean networkSate= NetWorkUtils.isNetworkConnected(this.getActivity());
                if (!networkSate){
                    Util.toastMessage(this.getActivity(),"网络连接不可用，请检查网络状态");
                }else{
                    pageNum = 1;
                    switch (v.getId()) {
                        case R.id.btnZi:    //字典
                            Intent intent=new Intent(getActivity(),ZidianActivity.class);

                            intent.putExtra("queryText",text);

                            startActivity(intent);
                            break;
                        case R.id.btnIdiom:    //成语
                            mark = FLAG_IDIOM;
                            Connection conn = new Connection();
                            conn.start();

                            break;
                        case R.id.btnPoem:     //诗词

                            mark = FLAG_POEM;
                            Connection connection = new Connection();
                            connection.start();
                            break;
                        case R.id.btnCi:    //词语
                            mark = FLAG_CI;
                            Connection connect = new Connection();
                            connect.start();
                            break;
                   }



                }
            }
        }





    //设置成语内容
    public Chengyu getChengYuContent(String str,Chengyu chengyu) throws JSONException{
        String pinyin,jieshi,from,example,yufa,yinzheng;
        StringBuffer tongyi=new StringBuffer();
        StringBuffer fanyi=new StringBuffer();
        JSONObject object=new JSONObject(str);
        if (object.getInt("error_code")==0){
            JSONObject result=object.getJSONObject("result");
            pinyin=result.getString("pinyin");
            jieshi=result.getString("chengyujs");
            from=result.getString("from_");
            example=result.getString("example");
            yufa=result.getString( "yufa");
            yinzheng=result.getString("yinzhengjs");

            if (!result.get("tongyi").toString().equals("null")) {
                JSONArray tongyiArray = result.getJSONArray("tongyi");

                for (int i = 0; i < tongyiArray.length(); i++) {
                    tongyi.append(tongyiArray.optString(i) + "  ");
                }
            }

            if (!result.get("fanyi").toString().equals("null")) {
                JSONArray fanyiArray = result.getJSONArray("fanyi");
                for (int i = 0; i < fanyiArray.length(); i++) {
                    fanyi.append(fanyiArray.optString(i) + "  ");
                }

            }
            chengyu.setPinyin(pinyin);
            chengyu.setJieshi(jieshi);
            if (!from.equals("null")){
                chengyu.setFrom(from);
            }
            chengyu.setFrom(from);
            if (!example.equals("null")){
                chengyu.setExample(example);
            }
            if (!yufa.equals("null")){
                chengyu.setYufa(yufa);
            }
            if (!yinzheng.equals("null")){
                chengyu.setYinzheng(yinzheng);
            }

            chengyu.setTongyi(tongyi.toString());
            chengyu.setFanyi(fanyi.toString());


        }
          return chengyu;

    }
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_SUCCESS:
                     footer.setVisibility(View.GONE);
                    //更新ListView显示
                    adapter.notifyDataSetChanged();


                    break;
                case MSG_LOADED_fail:
                    Util.showResultDialog(getActivity(), "查询不到相关内容，请重新输入！", "提示");
                   // new AlertDialog.Builder(MainActivity.this).setMessage("查询不到相关内容，请重新输入！").setPositiveButton("确定", null).create().show();
                    break;

            }
        }
    };


    public class Connection extends Thread {
       // String url="";

        @Override
        public void run() {

            if (mark==FLAG_POEM){           //诗词
                httpUrl="http://api.avatardata.cn/TangShiSongCi/Search?key=9b42454896f54202be3767fd55930654&keyWord=";
                int value= 0;
                try {
                    value = getList(httpUrl+URLEncoder.encode(text, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (value==1){
                    mUIHandler.sendEmptyMessage(MSG_LOAD_SUCCESS );
                }else if (value==0){
                    mUIHandler.sendEmptyMessage(MSG_LOADED_fail);
                }
            }
            else if (mark==FLAG_IDIOM){                      //成语
                httpUrl=" http://api.avatardata.cn/ChengYu/Search?key=24132c25d63c45ecbe9b6d3fe15d3cc1&keyWord=";
                int value= 0;
                try {
                    value = getList(httpUrl+URLEncoder.encode(text, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (value==1){
                    mUIHandler.sendEmptyMessage(MSG_LOAD_SUCCESS );
                }else if (value==0){
                    mUIHandler.sendEmptyMessage(MSG_LOADED_fail);
                }
            }
            else if (mark==FLAG_CI){                      //词语
                httpUrl="http://api.avatardata.cn/CiHai/query?key=1d07f366f1f64551bac9fe80b63b2e28&keyword=";
                int value= 0;
                try {

                    value = getList(httpUrl + URLEncoder.encode(text, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (value==1){
                    mUIHandler.sendEmptyMessage(MSG_LOAD_SUCCESS );
                }else if (value==0){
                    mUIHandler.sendEmptyMessage(MSG_LOADED_fail);
                }
            }




        }
    }






    public int getList(String url) throws Exception {
       // Log.i(tag,pageNum+"");
        int flag=1;    //0:没有查到相关数据
        JSONObject jsonObject;
        if(pageNum==1){                //第一次查询
            String jsonData= OkHttpUtil.get(url);
            jsonObject = new JSONObject(jsonData);
            int errorCode = jsonObject.getInt("error_code");
            int total=jsonObject.getInt("total");     //总记录数
            if (errorCode == 0&&total>0) {      //说明查到了数据
                //计算一共有多少页
                pages=total%20==0?(total/20):(total/20+1);
                pageNum++;


            }else {
                Log.i(tag,"没有查到相关数据");
                flag=0;
                return flag;

            }

        }else{
            String jsonData= OkHttpUtil.get(url+"&page="+pageNum++);
            jsonObject = new JSONObject(jsonData);

        }
        jsonObjectToList(jsonObject);
        return flag;
    }

    public void  jsonObjectToList(JSONObject jsonObject)  throws JSONException{

        JSONArray result = jsonObject.getJSONArray("result");
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            if(mark==FLAG_IDIOM) {
                String name = item.getString("name");
                Chengyu chengyu = new Chengyu();
                chengyu.setName(name);
                list.add(chengyu);
            }
            if(mark==FLAG_POEM){
                String name = item.getString("name");
                String id = item.getString("id");
                Article article = new Article();
                article.setId(id);
                article.setTitle(name);
                list.add(article);
            }
            if(mark==FLAG_CI){   //词语
                String name = item.getString("words");
                String content = item.getString("content");
                CiYu ciYu = new CiYu();
                ciYu.setName(name);
                ciYu.setContent(content);
                list.add(ciYu);
            }
        }
    }


    public Article getContent(String data,Article article) throws JSONException {
       // Log.i(tag, "传来的数据为" + data);
       // System.out.println("传来的数据为" + data);
        JSONObject jsonObject = new JSONObject(data);
        int errorCode = jsonObject.getInt("error_code");
        if (errorCode == 0) {

            Log.i(tag, "获取数据成功！！！");
            JSONObject result = jsonObject.getJSONObject("result");
          //  Log.i(tag, result.toString());
            String temp = (result.toString()).replace("\\r\\n","\\\\r\\\\n");
         //   Log.i(tag,temp);

             JSONObject result2 = new JSONObject(temp);
            String neirong = result2.optString("neirong");
            String jieshao=result2.optString("jieshao");
            String zuozhe=result2.optString("zuozhe");

           article.setZuoZhe(zuozhe);
           article.setJieShao(jieshao);
            article.setContent(neirong);


        }

        return article;

    }

}
