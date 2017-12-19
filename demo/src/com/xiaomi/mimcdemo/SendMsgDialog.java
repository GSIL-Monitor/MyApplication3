package com.xiaomi.mimcdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mimcdemo.common.NetWorkUtils;
import com.xiaomi.mimcdemo.common.SystemUtils;
import com.xiaomi.mimcdemo.common.UserManager;
import com.xiaomi.push.mimc.MIMCMessage;
import com.xiaomi.push.mimc.MimcConstant;
import com.xiaomi.push.mimc.MimcException;
import com.xiaomi.push.mimc.User;

public class SendMsgDialog extends Dialog {

    public SendMsgDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmsg_dialog);
        setCancelable(true);
        setTitle(R.string.button_send);
        final EditText toEditText =  (EditText) findViewById(R.id.chat_to);
        final SharedPreferences sp = SystemUtils.getContext()
                .getSharedPreferences("to_account", Context.MODE_PRIVATE);
        toEditText.setText(sp.getString("to", null));

        findViewById(R.id.chat_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String mTo = toEditText.getText().toString();
                byte mContent[] = ((EditText) findViewById(R.id.chat_content))
                        .getText().toString().getBytes();

                sp.edit().putString("to", mTo).commit();

                if (!NetWorkUtils.isNetwork(getContext())) {
                    Toast.makeText(getContext(), "发送失败，无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserManager.getInstance().getStatus() != MimcConstant.STATUS_LOGIN_SUCCESS) {
                    Toast.makeText(getContext(), "登录异常，请重新登陆", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(mTo)){
                    UserManager userManager = UserManager.getInstance();
                    User user = userManager.getUser(userManager.getAccount());
                    try {
                        user.sendMessage(mTo, mContent);
                    } catch (MimcException e) {
                        MyLog.w("send msg exception:"+e.getMessage());
                    }

                    MIMCMessage message = new MIMCMessage();
                    message.setPayload(mContent);
                    message.setFromAccount(userManager.getAccount());
                    UserManager.getInstance().addMsg(message);
                    dismiss();
                }
            }
        });
    }
}
