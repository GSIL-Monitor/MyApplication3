package com.yuwen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuwen.activity.CollectActivity;
import com.yuwen.activity.LoginActivity;
import com.yuwen.bmobBean.Collect;
import com.yuwen.bmobBean.User;
import com.yuwen.entity.Zi;
import com.yuwen.myapplication.R;
import com.yuwen.tool.Adapter;
import com.yuwen.tool.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cxy on 2017/9/27.
 */

public class ZidianAdapter extends BaseAdapter implements View.OnClickListener{
    private List<Zi> Items;
    private LayoutInflater mInflater;
    private Context context;
    TranslateAnimation mShowAction;
    TranslateAnimation mHiddenAction;
    ZidianAdapter.ViewHolder holder=null;
    private Map<Integer,Boolean> isShowMap;

    public ZidianAdapter(Context context, List<Zi> newsItems) {
        this.Items = newsItems;
        this.context=context;
        isShowMap=new HashMap<Integer,Boolean>();
        mInflater = LayoutInflater.from(context);

        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHiddenAction.setDuration(500);

        for (int i=0;i<newsItems.size();i++){
          isShowMap.put(i,true);

        }
}

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //判断是否缓存
        if (convertView==null){
            holder=new ZidianAdapter.ViewHolder();
            //通过LayoutInflater实例化布局
            convertView=mInflater.inflate(R.layout.zidian_item,null);
            holder.hanzi=(TextView)convertView.findViewById(R.id.hanzi);   //下次不用再findViewById()
            holder.pinyin=(TextView)convertView.findViewById(R.id.pinyin);
            holder.duyin=(TextView)convertView.findViewById(R.id.duyin);
            holder.bushou=(TextView)convertView.findViewById(R.id.bushou);
            holder.bihua=(TextView)convertView.findViewById(R.id.bihua);
            holder.jianjie=(TextView)convertView.findViewById(R.id.jianjie);
            holder.xiangjie=(TextView)convertView.findViewById(R.id.xiangjie);
            holder.constraintLayout=(ConstraintLayout)convertView.findViewById(R.id.containerLayout);
            holder.xjTextView=(TextView)convertView.findViewById(R.id.xj);
            holder.fab=(FloatingActionButton)convertView.findViewById(R.id.zidianFb);



            convertView.setTag(holder);

        }else{
            //通过tag找到缓存的布局
            holder=(ZidianAdapter.ViewHolder)convertView.getTag();
        }
        Zi zi=Items.get(position);
        holder.hanzi.setText(zi.getHanzi());
        holder.pinyin.setText(zi.getPinyin());
        holder.duyin.setText(zi.getDuyin());
        holder.bushou.setText(zi.getBushou());
        holder.bihua.setText(zi.getBihua());
        holder.jianjie.setText(zi.getJianjie());
        holder.xiangjie.setText(zi.getXiangjie());

        holder.fab.setOnClickListener(this);
        holder.xjTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isShowMap.get(position)==true){
                   holder.xiangjie.startAnimation(mHiddenAction);
                   holder.xiangjie.setVisibility(View.GONE);
                   isShowMap.put(position,false);
               }else{
                   holder.xiangjie.startAnimation(mShowAction);
                   holder.xiangjie.setVisibility(View.VISIBLE);
                   isShowMap.put(position,true);
               }
            }
        });



        return  convertView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.zidianFb){   //添加收藏到数据库
            User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
            if (user==null){   //未登录
                Util.showConfirmCancelDialog(context, "提示", "请先登录！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1=new Intent(context,LoginActivity.class);
                        context.startActivity(intent1);
                    }
                });
            }
            else{

                //添加收藏action
                //  User user= BmobUser.getCurrentUser(User.class);
                Collect collect=new Collect();
                collect.setName(holder.hanzi.getText().toString());
                collect.setUser(user);
                collect.setType(Collect.ZI);


                collect.save(new SaveListener<String>(){
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.i("bmob","收藏保存成功");
                            Snackbar.make(holder.constraintLayout, "已收藏该生字", Snackbar.LENGTH_LONG).setAction("查看我的收藏", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(context,CollectActivity.class);
                                    context.startActivity(intent);
                                }
                            }).show();

                        }else{
                            Log.i("bmob","收藏保存失败："+e.getMessage());
                        }
                    }
                });

            }
        }

        /*if (v.getId()==R.id.xj){
            int a=3;


            Util.toastMessage((Activity) context,holder.xiangjie.getVisibility()+"");

            if (holder.xiangjie.getVisibility()==View.GONE){
                holder.xiangjie.startAnimation(mShowAction);
                holder.xiangjie.setVisibility(View.VISIBLE);
            }else if (holder.xiangjie.getVisibility()==View.VISIBLE){
                holder.xiangjie.startAnimation(mHiddenAction);
                holder.xiangjie.setVisibility(View.GONE);
            }
        }
*/
    }

    public final class ViewHolder{

        TextView hanzi,pinyin,duyin,bushou,bihua,jianjie,xiangjie,xjTextView;
        ConstraintLayout constraintLayout;
        FloatingActionButton fab;

    }
}
