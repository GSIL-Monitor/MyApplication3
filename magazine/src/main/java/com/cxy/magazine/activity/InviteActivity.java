package com.cxy.magazine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.InviteCode;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class InviteActivity extends AppCompatActivity {

    @BindView(R.id.ed_invite_code)
    EditText editCode;
    @BindView(R.id.btn_activate)
    Button btnActivate;
    @BindView(R.id.tv_share_code)
    TextView tvShareCode;
    private User user;
    private String inviteCode;
    private final String APP_URL="http://app.xiaomi.com/detail/563172";
    private String objectId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("邀请好友");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user= BmobUser.getCurrentUser(User.class);//获取自定义用户信息

        BmobQuery<InviteCode> bmobQuery=new BmobQuery<InviteCode>();
        bmobQuery.addWhereEqualTo("user",user);
        bmobQuery.findObjects(new FindListener<InviteCode>() {
            @Override
            public void done(List<InviteCode> list, BmobException e) {
              if (e==null){
                   if (list.size()>0){
                       inviteCode=list.get(0).getInviteCode();
                       objectId=list.get(0).getObjectId();
                   }
                   else{
                       inviteCode=Utils.createRandomCharData(5);  //生成5位随机码
                       //保存数据库
                       InviteCode inviteCodeBmob=new InviteCode();
                       inviteCodeBmob.setUser(user);
                       inviteCodeBmob.setInviteCode(inviteCode);
                       inviteCodeBmob.setActivate(false);   //默认没有激活
                       inviteCodeBmob.save(new SaveListener<String>() {
                           @Override
                           public void done(String id, BmobException e) {
                              if (e!=null){
                                  Utils.toastMessage(InviteActivity.this,"保存数据失败");
                              }else{
                                  objectId=id;
                              }
                           }
                       });
                   }
                   tvShareCode.setText("分享我的邀请码 "+inviteCode);
              }else{
                  Utils.toastMessage(InviteActivity.this,"查询数据失败");
              }
            }
        });
    }

@OnClick(R.id.btn_activate)
public void activate(){
        final String code=editCode.getText().toString();
        if (TextUtils.isEmpty(code)||code.length()!=5){
            Utils.toastMessage(InviteActivity.this,"请输入5位数的邀请码");
        }else{
            //查询该用户是否已被激活
            BmobQuery<InviteCode> bmobQuery=new BmobQuery<InviteCode>();
            bmobQuery.getObject(objectId, new QueryListener<InviteCode>() {
                @Override
                public void done(InviteCode inviteCode, BmobException e) {
                    if (e==null){
                        Boolean isActivate=inviteCode.getActivate();
                        if (isActivate){
                            Utils.showResultDialog(InviteActivity.this,"每个新用户只能使用一次邀请码，快去分享你的邀请码吧，同样可以获得奖励！","提示");
                        }else{
                            //没有激活
                            //查询是否有该邀请码
                            BmobQuery<InviteCode> query=new BmobQuery<InviteCode>();
                            query.addWhereEqualTo("inviteCode",code);
                            query.findObjects(new FindListener<InviteCode>() {
                                @Override
                                public void done(List<InviteCode> list, BmobException e) {
                                    if (e==null){
                                       if (list.size()<=0){
                                           Utils.toastMessage(InviteActivity.this,"请输入有效的邀请码");
                                       }else{
                                           //当前用户和发送邀请码的用户各获得3天会员
                                       }
                                    }else{
                                        toastError(e);
                                    }
                                }
                            });
                        }
                    }else{
                        Utils.toastMessage(InviteActivity.this,"查询数据失败"+e.getMessage());
                    }
                }
            });


        }
}

public void toastError(BmobException e){
    Utils.toastMessage(InviteActivity.this,"查询数据失败:"+e.getMessage());
}

@OnClick(R.id.tv_share_code)
public void shareCode(){
            Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "使用我的邀请码下载杂志天下，免费读杂志+3天！\n下载地址：\n"
                                +APP_URL+"\n我的邀请码："+ inviteCode);

            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"share"));
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
