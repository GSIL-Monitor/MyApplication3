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
import com.xiaomi.push.mimc.MimcException;

public class LoginDialog extends Dialog {

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        setCancelable(true);
        setTitle(R.string.login);

        final EditText accountEditText = (EditText) findViewById(R.id.account);
        final SharedPreferences sp = SystemUtils.getContext()
                .getSharedPreferences("user", Context.MODE_PRIVATE);

        accountEditText.setText(sp.getString("account", null));
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String mAccount = accountEditText.getText().toString();
                sp.edit().putString("account", mAccount).commit();

                if (!NetWorkUtils.isNetwork(getContext())) {
                    Toast.makeText(getContext(), "登录失败，无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(mAccount)){
                    try {
                        UserManager.getInstance().getUser(mAccount).login();
                    } catch (MimcException e) {
                        MyLog.w("login exception:" + e.getMessage());
                    }
                    dismiss();
                }
            }
        });
    }
}
