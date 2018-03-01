package com.cxy.magazine.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.MsgNotification;
import com.cxy.magazine.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MsgDetailActivity extends BasicActivity {

    @BindView(R.id.tv_msg_detail)
    TextView textViewMsg;

    String objectId="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("消息详情");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MsgNotification msgNotification=(MsgNotification)this.getIntent().getSerializableExtra("msg");
        textViewMsg.setText(msgNotification.getDetail());
        objectId=msgNotification.getObjectId();

        updateMsg();


    }

    public void updateMsg(){
        MsgNotification msgNotification=new MsgNotification();
        msgNotification.setObjectId(objectId);
        msgNotification.setRead(true);
        msgNotification.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    Log.i("bmob","更新成功");

                }else{
                    Utils.toastMessage(MsgDetailActivity.this,"更新信息失败："+e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.button_msg_delete)
    public void deleteMsg(){

        MsgNotification msgNotification=new MsgNotification();
        msgNotification.setObjectId(objectId);
        msgNotification.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //删除成功，返回上一页
                    finish();

                }else{
                    Utils.toastMessage(MsgDetailActivity.this,"删除失败：" + e.getMessage());
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
