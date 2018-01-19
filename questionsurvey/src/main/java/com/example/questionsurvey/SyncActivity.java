package com.example.questionsurvey;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Adapter.SyncAdapter;
import Util.OkHttpUtil;
import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.Answer;
import entity.Question;
import entity.Questionnaire;

public class SyncActivity extends BasicActivity {

    @BindView(R.id.tv_sync)
    TextView tvSync;

    @BindView(R.id.tv_syncData)
    TextView tvSyncData;

    @BindView(R.id.rv_questions)
    LRecyclerView mRecyclerView;

    private SyncDataTask mSyncDataTask;

    private List<Questionnaire> questionnaireList;
    private SyncAdapter syncAdapter=null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        getSupportActionBar().setTitle("同步问卷");

        ButterKnife.bind(this);
        questionnaireList=new ArrayList<Questionnaire>();
        setReclerView();
        JSONArray questionArray=mCache.getAsJSONArray("questionList");
        if (questionArray!=null){
            setData(questionArray);
        }

    }

    public void setReclerView(){
        syncAdapter=new SyncAdapter(questionnaireList,this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(syncAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setPullRefreshEnabled(false);

        //设置间隔线
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.layoutBackground)
                .build();
        mRecyclerView.addItemDecoration(divider);
        //如果确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
    }

    //同步数据
    @OnClick(R.id.tv_sync)
    public void  syncData(){
        questionnaireList.clear();
        mSyncDataTask=new SyncDataTask();
        mSyncDataTask.execute((Void)null);
    }


    public class SyncDataTask extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute() {
            //显示一个进度框
            Util.showProgressDialog(SyncActivity.this,"请稍候","同步中");
        }

        @Override
        protected String doInBackground(Void... voids) {
            //网络加载数据
            try {

                String data=OkHttpUtil.get(Util.SERVER_URL+"queryQuestionnaire");
                return data;

            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String data) {
           //关闭进度框，更新RecycleView

          //  Toast.makeText(SyncActivity.this, data, Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG,data);
          //  setData(data);
            Util.dismissDialog();
            try {
                JSONObject jsonData=new JSONObject(data);
                String  responseCode=jsonData.getString("responseCode");
                if ("00000".equals(responseCode)){
                    JSONArray jsonArray=jsonData.getJSONArray("responseBody");
                    setData(jsonArray);
                    mCache.put("questionList",jsonArray);   //缓存数据
                }else{    //返回错误
                    String errorMsg=jsonData.getString("errorMessage");
                    Toast.makeText(SyncActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    private void setData(JSONArray jsonArray) {

        if (jsonArray.length() > 0) {
            try {

                for (int i = 0; i < jsonArray.length(); i++) {   //问卷数组
                    Questionnaire questionnaire = new Questionnaire();
                    List<Question> questionList = new ArrayList<Question>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    questionnaire.setId(jsonObject.getString("id"));
                    questionnaire.setTitle(jsonObject.getString("title"));
                    questionnaire.setVersion(jsonObject.getString("version"));
                    questionnaire.setRemark("备注");    //jsonObject.getString("remark")

                    JSONArray questionArray = jsonObject.getJSONArray("subjects");
                    for (int j = 0; j < questionArray.length(); j++) {    //题目数组
                        JSONObject questionObject = questionArray.getJSONObject(j);
                        Question question = new Question();
                        List<Answer> answerList = new ArrayList<Answer>();

                        question.setId(questionObject.getString("id"));
                        question.setQuestionNo(questionObject.getString("subjectNo"));
                        question.setType(questionObject.getString("type"));
                        question.setQuestionContent(questionObject.getString("context"));

                        JSONArray answerArray = questionObject.getJSONArray("answers");  //答案数组
                        for (int k = 0; k < answerArray.length(); k++) {
                            JSONObject answerObject = answerArray.getJSONObject(k);
                            Answer answer = new Answer(answerObject.getString("item"), answerObject.getString("itemContext"));
                            answerList.add(answer);
                        }

                        //给每道题目设置答案数组
                        question.setAnswers(answerList);

                        questionList.add(question);
                    }

                    //给每套问卷设置题目数组
                    questionnaire.setQuestions(questionList);

                    questionnaireList.add(questionnaire);

                }

                mLRecyclerViewAdapter.notifyDataSetChanged();
                tvSync.setText("重新同步");
                tvSyncData.setText("已同步下列数据:");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
