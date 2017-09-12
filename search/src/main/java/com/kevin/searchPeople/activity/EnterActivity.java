package com.kevin.searchPeople.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kevin.searchPeople.R;
import com.kevin.searchPeople.util.Constant;
import com.kevin.searchPeople.util.OkHttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.kevin.searchPeople.util.Constant.TAG;


public class EnterActivity extends AppCompatActivity {
    private String imageUrl=null;
    private TextView tvName,tvNum,tvCollege,tvGrade,tvJob;
    private Button btnSave;
    private String name=null,num=null,college=null,grade=null,job=null;
    // 实例化AlertDailog.Builder对象
    private AlertDialog.Builder builder;

    /*@Bind(R.id.name)
    TextView tvName;

    @Bind(R.id.num)
    TextView tvNum;

    @Bind(R.id.college)
    TextView tvCollege;

    @Bind(R.id.grade)
    TextView tvGrade;

    @Bind(R.id.job)
    TextView tvJob;

    @Bind(R.id.btnSave)
    Button btnSave;*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Intent intent=this.getIntent();
        imageUrl= intent.getStringExtra("imageUrl");
        Log.i(TAG,"发送的path:"+imageUrl);

        tvName=(TextView)findViewById(R.id.name);
        tvNum=(TextView)findViewById(R.id.num);
        tvCollege=(TextView)findViewById(R.id.college);
        tvGrade=(TextView)findViewById(R.id.grade);
        tvJob=(TextView)findViewById(R.id.job);

        btnSave=(Button)findViewById(R.id.btnSave);
        builder = new AlertDialog.Builder(EnterActivity.this);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=tvName.getText().toString();
                num=tvNum.getText().toString();
                college=tvCollege.getText().toString();
                grade=tvCollege.getText().toString();
                job=tvJob.getText().toString();
                if (name==null||name.length()<=0){
                    // 设置提示信息
                    builder.setIcon(R.mipmap.ic_warning_black_24dp).setTitle("警告").setMessage("姓名不能为空！").setPositiveButton("确定",null);
                    builder.show();
                }else if (imageUrl==null||imageUrl.length()<=0){
                    // 设置提示信息
                    builder.setTitle("警告").setIcon(R.mipmap.ic_warning_black_24dp).setMessage("请先选择图片！").setPositiveButton("确定",null);
                    builder.show();
                }else{
                    new NetworkTask().execute();
                }

            }
        });
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
           super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            Map<String,String> map = new HashMap<String,String>();
            map.put("name",name);
            map.put("num",num);
            map.put("college",college);
            map.put("grade",grade);
            map.put("job",job);
            map.put("imageUrl",imageUrl);
            Gson gson =  new Gson();
            String jsonStr=gson.toJson(map);
            Log.i(TAG,"json数据"+jsonStr);

            try {
                result= OkHttpUtil.post(Constant.BASE_URL + "/uploadDataServlet",jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG,result);
            if ("success".equals(result)){
                // 设置提示信息
                builder.setTitle("提示").setMessage("数据上传成功！").setPositiveButton("确定",null);
                builder.show();
            }else{
                // 设置提示信息
                builder.setTitle("警告").setMessage("上传数据发生错误！").setPositiveButton("确定",null);
                builder.show();
            }

        }
    }

}
