package com.example.questionsurvey;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveQuestionActivity extends BasicActivity {

    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.edit_remark)
    EditText editReamrk;

    JSONObject jsonObject=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_question);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("保存问卷");
        Bundle bundle=this.getIntent().getExtras();
        int spareTime=bundle.getInt("spareTime");
        String finishDegree=bundle.getString("finishDegree");

        String strTime=spareTime/60+"分钟"+spareTime%60+"秒";
        tvSummary.setText("此次调查，你共花费了"+strTime+"时间，试卷完成度 "+finishDegree+"%");
        try {
            jsonObject=new JSONObject(bundle.getString("jsonData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //将问卷答案保存到本地
    @OnClick(R.id.btn_save)
    public void  saveData(){
        try {
            jsonObject.put("remark",editReamrk.getText().toString());
            JSONArray questionArray=mCache.getAsJSONArray("questionArray");

            if (questionArray==null){
               questionArray=new JSONArray();
            }
            questionArray.put(jsonObject);
            mCache.put("questionArray",questionArray);

            Util.showConfirmCancelDialog(SaveQuestionActivity.this, null, "数据保存成功！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent=new Intent(SaveQuestionActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Util.toastMessage(SaveQuestionActivity.this,"异常错误："+e.toString());
        }
    }

}
