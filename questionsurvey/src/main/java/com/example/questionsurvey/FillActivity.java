package com.example.questionsurvey;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.Answer;
import entity.Question;
import entity.Questionnaire;

public class FillActivity extends BasicActivity {

    @BindView(R.id.spinner_data)  Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private List<Questionnaire> questionnaireList;
    private Questionnaire questionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("填写问卷");
        questionnaireList=new ArrayList<Questionnaire>();
        setQuestionnaireList();
        spinnerAdapter=new ArrayAdapter<Questionnaire>(this, android.R.layout.simple_list_item_1, questionnaireList);
        //设置样式
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    AdapterView.OnItemSelectedListener itemSelectedListener=new  AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {
           // Util.toastMessage(FillActivity.this,postion+"问卷");
            questionnaire=questionnaireList.get(postion);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @OnClick(R.id.start_fill)
    public void startFill(){
       if (questionnaire!=null){
           Intent intent=new Intent(FillActivity.this,AnswerQuestionActivity.class);
           intent.putExtra("questionnaire",questionnaire);
           startActivity(intent);
       }else{
           Util.showResultDialog(this,"请选择问卷","提示");
       }
    }

    public void  setQuestionnaireList(){
        JSONArray jsonArray=mCache.getAsJSONArray("questionList");
        if (jsonArray!=null&&jsonArray.length() > 0) {
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




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            //提示先同步数据
            Util.showResultDialog(FillActivity.this,"请先同步数据，再来填写问卷！","提示");
        }

    }
}
