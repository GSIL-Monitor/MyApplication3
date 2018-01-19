package com.example.questionsurvey;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Util.OkHttpUtil;
import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadActivity extends BasicActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lRecycleView)
    LRecyclerView mRecyclerView;
    JSONArray questionArray=null;
    List<JSONObject> saveList=null;
    List<JSONObject> uploadList=null;
    List<File> uploadFileList=null;
    String errorMessage="";
    private UploadAdpter uploadAdpter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("上传问卷");
        questionArray=mCache.getAsJSONArray("questionArray");
        saveList=new ArrayList<JSONObject>();
        uploadList=new ArrayList<JSONObject>();
        uploadFileList=new ArrayList<File>();
        if (questionArray==null||questionArray.length()<=0){
            tvTitle.setText("你没有需要上传的问卷，请先去填写问卷，再来上传！");
        }else{
            for (int i=0;i<questionArray.length();i++){
                try {
                    saveList.add(questionArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setmRecyclerView();
        }
    }


    public void setmRecyclerView(){

        uploadAdpter=new UploadAdpter();
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(uploadAdpter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mRecyclerView.addItemDecoration(divider);

        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.setAdapter(mLRecyclerViewAdapter);


    }
    //上传事件
    @OnClick(R.id.btn_upload)
    public  void  uploadData(){
        final JSONArray uploadArray=new JSONArray();
        final JSONArray saveArray=new JSONArray();
        final List<String> filePaths=new ArrayList<String>();
        if(uploadList.size()<=0){
            Util.showResultDialog(UploadActivity.this,"请选择你要上传的问卷",null);
        }else{
            for (JSONObject jsonObject : uploadList){
                uploadArray.put(jsonObject);

            }
            for (JSONObject jsonObject : saveList){
                saveArray.put(jsonObject);
            }
            for (File file : uploadFileList){
                filePaths.add(file.getAbsolutePath());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response=OkHttpUtil.postJson(Util.SERVER_URL+"submitQuestionnaire",uploadArray.toString());   //上传json数据
                        Log.i(LOG_TAG,response);
                        JSONObject responseObject=new JSONObject(response);
                        String responseCode=responseObject.getString("responseCode");
                        if ("00000".equals(responseCode)){                   //json数据上传成功

                            String responseAudio=OkHttpUtil.postFile(Util.SERVER_URL+"submitAudio",filePaths,"audio/mpeg");   //上传音频文件
                            JSONObject responseAudioObject=new JSONObject(responseAudio);

                            if ("00000".equals(responseAudioObject.getString("responseCode"))){ //音频文件上传成功
                                //保存未上传的json数据
                                mCache.put("questionArray",saveArray);

                                //删除已上传成功的文件
                                for (File file : uploadFileList){
                                    if (file.exists()){
                                        file.delete();
                                    }
                                }

                                handler.sendEmptyMessage(100);
                            }else{
                                errorMessage=responseAudioObject.getString("errorMessage");
                                handler.sendEmptyMessage(101);
                            }


                        }else{
                            errorMessage=responseObject.getString("errorMessage");
                            handler.sendEmptyMessage(101);
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        errorMessage=e.toString();
                        handler.sendEmptyMessage(101);
                    }
                }
            }).start();

        }

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==100){
                questionArray=mCache.getAsJSONArray("questionArray");
                mLRecyclerViewAdapter.notifyDataSetChanged();
                uploadList.clear();
                saveList.clear();
                uploadFileList.clear();
                Util.showResultDialog(UploadActivity.this,"上传成功！",null);

            }
            if (msg.what==101){
                Util.showResultDialog(UploadActivity.this,"上传失败!"+errorMessage,null);
            }
        }
    };

    class UploadAdpter extends RecyclerView.Adapter<UploadAdpter.MyViewHolder>{
      /*  JSONArray jsonArray;

        public UploadAdpter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }*/

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(UploadActivity.this).inflate(R.layout.item_upload, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                JSONObject questionnaaireObject=questionArray.getJSONObject(position);
                String title=questionnaaireObject.getString("title")+questionnaaireObject.getString("version");
                holder.checkBox.setText(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    try {
                        JSONObject jsonObject=questionArray.getJSONObject(position);
                        File audioFile=new File(Environment.getExternalStorageDirectory(),jsonObject.getString("audioName"));
                        if (checked){
                            uploadList.add(jsonObject);
                            saveList.remove(jsonObject);
                            if (audioFile.exists()){
                                uploadFileList.add(audioFile);
                            }


                        }else{
                            if (uploadList.contains(jsonObject)){
                                uploadList.remove(jsonObject);
                                saveList.add(jsonObject);
                            }
                            if (uploadFileList.contains(audioFile)){
                                uploadFileList.remove(audioFile);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return questionArray.length();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            @BindView(R.id.checkbox)
            CheckBox checkBox;

            public MyViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);

            }
        }
    }
}

