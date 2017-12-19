package com.xiaomi.mimcdemo.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomi.mimcdemo.R;
import com.xiaomi.push.mimc.MIMCMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<MIMCMessage> mDatas;
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    public ChatAdapter(Context context, List<MIMCMessage> datas) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_SEND) {
            View view = mLayoutInflater.inflate(R.layout.item_chat_send, parent, false);
            return new ChatSendViewHolder(view);
        }
        View view = mLayoutInflater.inflate(R.layout.item_chat_receive, parent, false);
        return new ChatReceiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MIMCMessage msg = mDatas.get(position);
        String content = new String(msg.getPayload());
        String fromAccount = msg.getFromAccount();
        String mimcAccount = UserManager.getInstance().getAccount();
        if (holder instanceof ChatSendViewHolder) {
            ((ChatSendViewHolder) holder).tv_send.setText(content);
            ((ChatSendViewHolder) holder).send_account.setText(mimcAccount);
        }
        if (holder instanceof ChatReceiveViewHolder) {
            ((ChatReceiveViewHolder) holder).tv_receive.setText(content);
            ((ChatReceiveViewHolder) holder).receive_account.setText(fromAccount);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).getFromAccount().equals(UserManager.getInstance().getAccount())) {
            return TYPE_SEND;
        }
        return TYPE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ChatSendViewHolder extends RecyclerView.ViewHolder {
        TextView tv_send;
        TextView send_account;

        ChatSendViewHolder(View view) {
            super(view);
            tv_send = (TextView) view.findViewById(R.id.tv_chat);
            send_account = (TextView) view.findViewById(R.id.send_account);
        }
    }

    static class ChatReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView tv_receive;
        TextView receive_account;

        ChatReceiveViewHolder(View view) {
            super(view);
            tv_receive = (TextView) view.findViewById(R.id.tv_chat);
            receive_account = (TextView) view.findViewById(R.id.receive_account);
        }
    }
}


