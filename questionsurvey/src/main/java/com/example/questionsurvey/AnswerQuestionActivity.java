package com.example.questionsurvey;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.Answer;
import entity.Question;
import entity.Questionnaire;

public class AnswerQuestionActivity extends BasicActivity {

    @BindView(R.id.questionTitle) TextView tv_title;
    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.container) LinearLayout linearLayout;
    @BindView(R.id.edit_remark) EditText editRemark;
    @BindView(R.id.time_minute) TextView timeMinute;
    @BindView(R.id.time_secondes) TextView timeSeconds;
    @BindView(R.id.questionSum)  TextView questionSum;
    private  List<Question> questions;
    private  Questionnaire questionnaire;
    private int sum=0,counter=0;   //已做题目数量 题目遍历迭代器
    private JSONObject questionnaireObject;
    private JSONArray questionArray;
    private int seconds=0,minutes=0;
    private boolean timeFlag=true;
    private Question currentQuestion;   //当前页面回答的问题
    private JSONObject questionObject;
    private JSONArray answerArray;
    private ArrayList<String> answerList;
    private MP3Recorder mRecorder ;
    private String audioName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("填写问卷");
        questionnaire=(Questionnaire) getIntent().getSerializableExtra("questionnaire");
        questionArray=new JSONArray();
        answerList=new ArrayList<String>();
        questions= questionnaire.getQuestions();

        questionSum.setText("共"+questions.size()+"道题");

        currentQuestion=questions.get(counter++);
        setOptions(currentQuestion);
        setTime();
        setFileName();
        questionnaireObject=new JSONObject();

        startRecord();   //启动录音
        try {
            questionnaireObject.put("id",questionnaire.getId());
            questionnaireObject.put("userId",mCache.getAsString("userId"));
            questionnaireObject.put("title",questionnaire.getTitle());
            questionnaireObject.put("version",questionnaire.getVersion());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 设置文件名 日期字符串+3位随机数
     */
    public void setFileName(){
        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        audioName=sdf.format(date)+Util.getFixLenthString(3);
    }

    public void setTime(){
        final  Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==100) {
                    int sec=seconds%60;
                    int min=seconds/60;
                    String strSec=sec+"";
                    String strMin=min+"";
                    if (strSec.length()<2){
                        strSec="0"+strSec;
                    }
                    if (strMin.length()<2){
                      strMin="0"+strMin;
                    }
                    timeMinute.setText(strMin);
                    timeSeconds.setText(strSec);
                    seconds++;
                }
            }
        };
       TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (timeFlag){
                    handler.sendEmptyMessage(100);
                }

            }
        };

        Timer timer = new Timer();
        // 参数：
        // 1000，延时1秒后执行。
        // 1000，每隔1秒执行1次task。
        timer.schedule(task, 0, 1000);
    }

    /**
     * 开始录音
     */
    public void startRecord(){
      mRecorder = new MP3Recorder(new File(Environment.getExternalStorageDirectory(),audioName+".mp3"));
        try {
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void  stopRecord(){
        mRecorder.stop();
    }

    /**
     * 下一题
     */

    @OnClick(R.id.next_question)
    public void nextQuestion(){
        if (answerList.size()<=0){
            Util.toastMessage(AnswerQuestionActivity.this,"请先回答本题");
        }else{

            if (counter<questions.size()){  //下一题

                //保存当前数据
                try {
                    answerArray=new JSONArray();
                    for (String answer : answerList){
                        answerArray.put(answer);
                    }
                    answerList.clear();   //添加完答案之后清空
                    questionObject.put("questionId",currentQuestion.getId());
                    questionObject.put("answer",answerArray);
                    questionObject.put("remark",editRemark.getText().toString());
                    questionArray.put(questionObject);
                    setOptions(questions.get(counter));

                    counter+=1; //迭代器加1
                    sum+=1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else{   //答完了所有题
                //  Util.toastMessage(AnswerQuestionActivity.this,"题目答完了");
                Util.showConfirmCancelDialog(AnswerQuestionActivity.this, "提示", "你已完成了所有问卷内容，确定提交吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //保存当前数据
                        saveJsonData();
                        submmitQustion();
                    }
                });
            }
        }



    }
    //结束答卷
    @OnClick(R.id.finish_question)
    public void finishAnswer()
    {
        int temp=questions.size()-sum;

        if (answerList.size()>0){//回答了当前题
             temp=questions.size()-sum-1;
        }
        if (counter<questions.size()){

            Util.showConfirmCancelDialog(AnswerQuestionActivity.this, "提示", "你还有"+temp+"道问题没有回答，你确定要结束作答吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    saveJsonData();
                    submmitQustion();
                }
            });
        }else{
            Util.showConfirmCancelDialog(AnswerQuestionActivity.this, "提示", "你已完成了所有问卷内容，确定提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    saveJsonData();
                    submmitQustion();
                }
            });
        }

    }

    //保存当前数据
    public void  saveJsonData(){

        try {
            answerArray=new JSONArray();
            for (String answer : answerList){
                answerArray.put(answer);
            }
            questionObject.put("questionId",currentQuestion.getId());
            questionObject.put("answer",answerArray);
            questionObject.put("remark",editRemark.getText().toString());
            questionArray.put(questionObject);

            sum=sum+1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submmitQustion(){
        timeFlag=false;
        stopRecord();  //停止录音
        try {
            questionnaireObject.put("audioName",audioName+".mp3");   //录音名称
            questionnaireObject.put("spareTime",seconds);
            questionnaireObject.put("questionList",questionArray);
            Intent intent=new Intent(AnswerQuestionActivity.this,SaveQuestionActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("spareTime",seconds);   //花费时间

            double degree=(double) sum/questions.size()*100;
            DecimalFormat df   = new DecimalFormat("######0.00");
            bundle.putString("finishDegree",df.format(degree)); //完成度*100,保留两位小数

            bundle.putString("jsonData",questionnaireObject.toString());    //json数据
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void setOptions(Question question){
        questionObject=new JSONObject();
        currentQuestion=question;

        linearLayout.removeAllViews();
        radioGroup.removeAllViews();
        tv_title.setText(question.getQuestionNo()+"、  "+question.getQuestionContent());
        editRemark.setText("");
        String type=question.getType();    //type 1:单选 2：多选
        List<Answer> answers=question.getAnswers();
        if ("1".equals(type)){     //单选 动态添加RadioButton
            for (final Answer answer: answers){
                final RadioButton tempButton = new RadioButton(this);
               // tempButton.setBackgroundResource(R.drawable.xxx);   // 设置RadioButton的背景图片
               // tempButton.setButtonDrawable(R.drawable.xxx);           // 设置按钮的样式

                tempButton.setPadding(10, 0, 0, 0);                 // 设置文字距离按钮四周的距离
                tempButton.setText(answer.getItemNo()+"  "+answer.getItemContent());
                //设置radioButton的选中事件
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Util.toastMessage(AnswerQuestionActivity.this,answer.getItemNo());
                        String number=answer.getItemNo();
                        answerList.clear();   //单选题必须清空list
                        answerList.add(number);
                    }
                });
                radioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

        }
        if ("2".equals(type)){   //多选  动态添加checkBox

            for (final Answer answer : answers){

                CheckBox checkBox=new CheckBox(this);
                checkBox.setPadding(10,0,0,0);
                checkBox.setText(answer.getItemNo()+"  "+answer.getItemContent());

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                          if (b==true){

                              answerList.add(answer.getItemNo());
                          }else{
                              if (answerList.contains(answer.getItemNo())){
                                  answerList.remove(answer.getItemNo());
                              }
                          }
                    }
                });
                linearLayout.addView(checkBox, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }


        }  //

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
    }
}
