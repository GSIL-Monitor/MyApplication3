package com.yuwen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuwen.BmobBean.User;
import com.yuwen.Entity.Article;
import com.yuwen.Entity.Chengyu;
import com.yuwen.Entity.CiYu;
import com.yuwen.Entity.Zi;
import com.yuwen.MyApplication;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Adapter;
import com.yuwen.tool.NetworkConnection;
import com.yuwen.tool.OkHttpUtil;
import com.yuwen.tool.Util;
import com.yuwen.tool.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MainActivity extends BasicActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{

    private TextView textView,btnToLogin;
    private ListView listView;
    private Button btnZi,btnChengyu,btnPoem,btnCi;
    private NavigationView navigationView;
    private String text = "";
    private List list = new ArrayList();
    private Adapter adapter;
    public static int mark;//查询的标志
    public  String httpUrl = "";
    public static final String tag="INFO";
    public  static final String appUrl="http://a.app.qq.com/o/simple.jsp?pkgname=com.cxy.yuwen";
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
   // private ImageView mUserLogo;

    public static List<String> logList = new CopyOnWriteArrayList<String>();

    //String json = "";
  //  String from = "";

    Article    article;
    Chengyu chengYu;
    CiYu ciYu;
    User user;

    //底部加载更多布局
    View footer;
    private String title;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        MyApplication.getInstance().addActivity(this);

      //  json = getIntent().getStringExtra("json");
      //  from = getIntent().getStringExtra("from");

        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        Bmob.initialize(this, Utils.BmobApplicationId,"bmob");

        user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息

        title=(String) getTitle();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       //s checkPermmion(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.setDrawerListener(toggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        btnToLogin=(TextView) headerLayout.findViewById(R.id.btn_to_login);
      //  mUserLogo=(ImageView)headerLayout.findViewById(R.id.imageView);
        layout=(LinearLayout) headerLayout.findViewById(R.id.clickLayout);
        layout.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        textView = (TextView) findViewById(R.id.tv);
        listView = (ListView) findViewById(R.id.lv);
        btnZi = (Button) findViewById(R.id.btnZi);
        btnCi=(Button)findViewById(R.id.btnCi);
        btnChengyu=(Button)findViewById(R.id.btnIdiom);
        btnPoem=(Button)findViewById(R.id.btnPoem);
        adapter = new Adapter(this, list);
        listView.setAdapter(adapter);

        btnZi.setOnClickListener(this);
        btnCi.setOnClickListener(this);
        btnChengyu.setOnClickListener(this);
        btnPoem.setOnClickListener(this);
       // btnToLogin.setOnClickListener(this);

        //将底部加载一个加载更多的布局
        footer = LayoutInflater.from(this).inflate(R.layout.foot_boot, null);
        //初始状态为隐藏
        footer.setVisibility(View.GONE);
        //加入到ListView的底部
        listView.addFooterView(footer);

        setUser();
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
                                String dataChengyu= NetworkConnection.net(NetworkConnection.URL_CHENGYU,map,null);
                                chengYu = getChengYuContent(dataChengyu,chengYu);
                                Intent intent=new Intent(MainActivity.this,ChengyuActivity.class);
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
                            Intent intent = new Intent(MainActivity.this, ShiciActivity.class);
                            intent.putExtra("article", article);

                            startActivity(intent);

                        }
                    }).start();


                }
                if (mark==FLAG_CI){
                    ciYu =(CiYu)list.get(position);
                    Intent intent = new Intent(MainActivity.this, CiYuActivity.class);
                    intent.putExtra("ciYu", ciYu);

                    startActivity(intent);
                }
            }

        });

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
                            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
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




      });
    }

    /*Runnable zidian=new Runnable() {
        @Override
        public void run() {
            Map map=new HashMap<String,String>();
            map.put("key",NetworkConnection.APPKEY_ZI);
            map.put("content",text);


            try {
                String dataZidian= NetworkConnection.net(NetworkConnection.URL_ZI,map,null);
                Zi zi= getDataZidian(dataZidian);
                Intent intent=new Intent(MainActivity.this,ZidianActivity.class);

                zi.setName(text);

                intent.putExtra("zi",zi);

                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

public void setUser(){
    if (user!=null) {

            btnToLogin.setText(user.getUsername());

    }else{   //user为null

    }

}

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.clickLayout){
            if (user!=null){  //用户详情
                Intent intent=new Intent(MainActivity.this,SettingInfomationActivity.class);
                startActivity(intent);
            }else{   //跳转到注册界面
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }

        }
        else {
            text = textView.getText().toString();
            footer.setVisibility(View.GONE);
            list.clear();
            adapter.notifyDataSetChanged();


            if (text == null || text.length() <= 0) {
                Util.showResultDialog(MainActivity.this, "请输入要查询的内容!", "提示");

             //   new AlertDialog.Builder(MainActivity.this).setMessage("请输入要查询的内容!").setPositiveButton("确定", null).create().show();

            } else {
                //   Log.i(tag,listviewState+"");

                pageNum = 1;
                switch (v.getId()) {
                    case R.id.btnZi:    //字典
                        Intent intent=new Intent(MainActivity.this,ZidianActivity.class);

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


    /**
     * 解析获取到的字典数据
     * @param dataZidian
     * @throws JSONException
     */
    public Zi getDataZidian(String dataZidian)throws JSONException{

         // Log.i(AdApplication.TAG,dataZidian);
          StringBuffer xiangjie=new StringBuffer();
          JSONObject object = new JSONObject(dataZidian);
           Zi zi=new Zi();
           if(object.getInt("error_code")==0){
               /*//System.out.println(object.get("result"));
               JSONObject result=object.getJSONObject("result");
               JSONArray xiangJie=result.getJSONArray("jijie");
               for (int i=1;i<xiangJie.length();i++){
                    xiangjie.append(xiangJie.getString(i)+"\r\n\r\n");
               }
               zi.setContent(xiangjie.toString());
               String id=result.getString("id");
               Log.i(MyApplication.TAG,id);
               zi.setId(id);*/



           }else{
               System.out.println(object.get("error_code")+":"+object.getInt("reason"));
           }
        return zi;
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
                    Util.showResultDialog(MainActivity.this, "查询不到相关内容，请重新输入！", "提示");
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
                pages=total/20==0?(total/20):(total/20+1);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        /*if (id == R.id.setting) {
            // Handle the setting action
            // Fragment contentFragment=new Fragment1();
            //  FragmentManager fm=getSupportFragmentManager();
            //   fm.beginTransaction().replace(R.layout.content_main,contentFragment).commit();

            Log.i(tag,"点击了设置");
        }
       */
        if (id == R.id.collect) {
            if (user!=null){
                Intent intent=new Intent(MainActivity.this,CollectActivity.class);
                startActivity(intent);
            }else{
                Util.showConfirmCancelDialog(MainActivity.this, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    }
                });
            }


        }
        if (id == R.id.feedback) {
           // Toast.makeText(MainActivity.this, "你点击了反馈", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,FeedbackActivity.class);
            startActivity(intent);

        }

        if(id==R.id.share){

            Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "语文助手这个App真不错，快来下载\n"+appUrl);
            sendIntent.setType("text/plain");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));
        }
        if (id==R.id.composition) {  //作文大全
            Intent intent = new Intent(MainActivity.this, CompositionActivity.class);
            startActivity(intent);
        }
        if (id==R.id.exit){   //退出
            MyApplication.getInstance().exit();
        }
        if (id==R.id.member_recharge){  //会员充值
            Intent intent=new Intent(MainActivity.this,MemberActivity.class);
            startActivity(intent);
        }



     //   drawer.closeDrawer(GravityCompat.START);


        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }


   /* //返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 退出程序
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
           //System.exit(0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/
}
