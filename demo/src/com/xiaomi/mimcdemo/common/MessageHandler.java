package com.xiaomi.mimcdemo.common;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.mimc.MIMCGroupMessage;
import com.xiaomi.push.mimc.MIMCMessage;
import com.xiaomi.push.mimc.MimcMessageHandler;

import java.util.List;

class MessageHandler implements MimcMessageHandler {

    @Override
    public void handleMessage(List<MIMCMessage> packets) {
        for (int i = 0; i < packets.size(); ++i) {
            UserManager.getInstance().addMsg(packets.get(i));
            MyLog.w("receive msg from:" + packets.get(i).getFromAccount());
        }
    }

    @Override
    public void handleGroupMessage(List<MIMCGroupMessage> packets) {}

    @Override
    public void handleServerAck(String packetId) {
        UserManager.getInstance().serverAck(packetId);
    }
}
