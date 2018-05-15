package com.cxy.yuwen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxy.yuwen.R;
import com.cxy.yuwen.bmobBean.InviteCode;
import com.cxy.yuwen.bmobBean.Member;
import com.cxy.yuwen.bmobBean.MsgNotification;
import com.cxy.yuwen.bmobBean.User;
import com.cxy.yuwen.tool.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import cn.bmob.v3.listener.UpdateListener;

public class InviteActivity extends BasicActivity {

    @BindView(R.id.ed_invite_code)
    EditText editCode;
    @BindView(R.id.btn_activate)
    Button btnActivate;
    @BindView(R.id.tv_share_code)
    TextView tvShareCode;
    private User user;
    private String inviteCode;
    private final String APP_URL="http://app.xiaomi.com/detail/453068";
    private String objectId="";
    private final String memberMsg="你使用了好友的邀请码，奖励你的3天会员服务已到账，敬请查收，你还可以分享你的专属邀请码给好友，分享多多，奖励多多。";
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
                if (e==null && list!=null){
                    if (list.size()>0){
                        inviteCode=list.get(0).getInviteCode();
                        objectId=list.get(0).getObjectId();
                    }
                    else{
                        inviteCode= Utils.createRandomCharData(5);  //生成5位随机码
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

    boolean inviteResult=true;
    @OnClick(R.id.btn_activate)
    public void activate(){
        final String code=editCode.getText().toString();
        if (TextUtils.isEmpty(code)||code.length()!=5){
            Utils.toastMessage(InviteActivity.this,"请输入5位数的邀请码");
        }else{
            //查询该用户是否已被激活
            final BmobQuery<InviteCode> bmobQuery=new BmobQuery<InviteCode>();
            bmobQuery.getObject(objectId, new QueryListener<InviteCode>() {
                @Override
                public void done(InviteCode inviteCode, BmobException e) {
                    if (e==null && inviteCode!=null){
                        Boolean isActivate=inviteCode.getActivate();
                        if (isActivate){
                            Utils.showResultDialog(InviteActivity.this,"每个新用户只能使用一次邀请码，快去分享你的邀请码吧，同样可以获得奖励哦！","提示");
                        }else{
                            //没有激活
                            //查询是否有该邀请码
                            BmobQuery<InviteCode> query=new BmobQuery<InviteCode>();
                            query.addWhereEqualTo("inviteCode",code);
                            query.findObjects(new FindListener<InviteCode>() {
                                @Override
                                public void done(List<InviteCode> list, BmobException e) {
                                    if (e==null && list!=null){
                                        if (list.size()<=0){
                                            Utils.toastMessage(InviteActivity.this,"请输入有效的邀请码");
                                        }else{

                                            //当前用户和发送邀请码的用户各获得3天会员
                                            inviteResult=updateMemeber(user,memberMsg);   //当前用户 会员更新
                                            String message="你的好友"+user.getUsername()+"使用了你的专属邀请码，奖励你的3天会员已到账，敬请查收，你还可以分享你的邀请码给更多人！分享多多，奖励多多！";
                                            //发送邀请码的用户更新
                                            for (InviteCode inviteCode:list ){
                                                inviteResult=updateMemeber(inviteCode.getUser(),message);
                                                if (!inviteResult){
                                                    break;
                                                }
                                            }
                                            //更新当前用户的激活状态
                                            InviteCode codeUpdate=new InviteCode();
                                            codeUpdate.setActivate(true);
                                            codeUpdate.update(objectId, new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e!=null){
                                                        inviteResult=false;
                                                    }
                                                }
                                            });
                                            //当前用户和发送邀请码的用户会员信息都已更新
                                            if (inviteResult){
                                                Utils.showResultDialog(InviteActivity.this,"恭喜你获得了3天免费会员，你还可以继续分享你的邀请码，累加会员时间哦！",null);
                                            }else{
                                                Utils.toastMessage(InviteActivity.this,"出错了，请稍后再试！");
                                            }

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

    boolean result=true;
    /**
     * 更新会员信息
     * @param user
     */
    public  boolean updateMemeber(final User user, final String message){

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        BmobQuery<Member> memberQuery = new BmobQuery<Member>();
        memberQuery.addWhereEqualTo("user", user);
        memberQuery.findObjects(new FindListener<Member>() {
            @Override
            public void done(List<Member> list, BmobException e) {
                if (e==null && list!=null){
                    if (list.size()<=0){  //该用户没有开通会员
                        //插入一条会员信息
                        Member member=new Member();
                        member.setUser(user);
                        member.setMemberMoney(0.00);   //赠送会员 金额为0

                        Calendar calendar = Calendar.getInstance();
                        //设置会员开始时间
                        member.setStartTime(sdf.format(calendar.getTime()));

                        //设置结束时间 开始时间+3天
                        calendar.add(Calendar.DATE,3);
                        member.setFinishTime(sdf.format(calendar.getTime()));

                        member.save(new SaveListener<String>() {  //添加数据
                            @Override
                            public void done(String objectId, BmobException e) {
                                if(e==null){
                                    Log.i("bmob","添加会员数据成功" );
                                    // 发送消息通知
                                    result=sendMessage(user,message);

                                }else{
                                    toastError(e);
                                    result=false;
                                }
                            }
                        });


                    }else if (list.size()==1){  //该用户已开通会员
                        Member queryMember=list.get(0);
                        String finishTime=queryMember.getFinishTime();  //会员到期时间
                        Calendar nowCal = Calendar.getInstance();  //当前时间
                        Calendar finishCal = Calendar.getInstance();   //结束时间
                        queryMember.setMemberMoney(queryMember.getMemberMoney()+0);
                        try {
                            nowCal.setTime(sdf.parse((sdf.format(new Date()))));
                            finishCal.setTime(sdf.parse(finishTime));
                            int value=finishCal.compareTo(nowCal);
                            if (value==-1){   //已经过期，重新设置开始和结束时间

                                queryMember.setStartTime(sdf.format(nowCal.getTime()));
                                nowCal.add(Calendar.DATE,3);
                                queryMember.setFinishTime(sdf.format(nowCal.getTime()));   //结束时间等于开始时间（现在的时间）加上3天

                            }else{  //还没过期，重新设置结束日期 +3天
                                finishCal.add(Calendar.DATE,3);
                                queryMember.setFinishTime(sdf.format(finishCal.getTime()));   //结束时间等于原来的结束时间加上月份


                            }

                            queryMember.update(queryMember.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.i("bmob","更新成功");
                                        // 发送通知
                                        result=sendMessage(user,message);
                                    }else{
                                        Log.i("bmob","更新数据更新失败："+e.getMessage()+","+e.getErrorCode());
                                        toastError(e);
                                        result=false;
                                    }
                                }
                            });


                        } catch (ParseException e1) {
                            e1.printStackTrace();
                            result=false;
                        }

                    }
                }else{
                    toastError(e);
                    result=false;

                }
            }
        });

        return result;
    }

    /**
     * 发送消息通知
     * @param user
     */
    boolean msgSend=true;
    public boolean sendMessage(User user,String messageTitle){

        MsgNotification msgNotification=new MsgNotification();
        msgNotification.setUser(user);
        msgNotification.setTitle("会员奖励到账通知");
        msgNotification.setDetail(messageTitle);
        msgNotification.setRead(false);
        msgNotification.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e!=null){
                    msgSend=false;
                }
            }
        });
        return msgSend;
    }

    @OnClick(R.id.tv_share_code)
    public void shareCode(){
        Intent sendIntent=new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "使用我的邀请码下载语文助手，免费会员+3天！\n下载地址：\n"
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
