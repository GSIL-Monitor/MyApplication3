package com.xiaomi.mimcdemo.common;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.mimc.MIMCMessage;
import com.xiaomi.push.mimc.MIMCTokenFetcher;
import com.xiaomi.push.mimc.MimcOnlineStatusListener;
import com.xiaomi.push.mimc.User;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserManager {
    /**
     * @important!!! appId/appKey/appSec：
     * 小米开放平台(https://dev.mi.com/cosole/man/)申请
     * 信息敏感，不应存储于APP端，应存储在AppProxyService
     * appAccount:
     * APP帐号系统内唯一ID
     * 此处appId/appKey/appSec为小米MIMC Demo所有，会在一定时间后失效
     * 请替换为APP方自己的appId/appKey/appSec
     **/
    private long appId = 2882303761517669588L;
    private String appKey = "5111766983588";
    private String appSecret = "b0L3IOz/9Ob809v8H2FbVg==";
    private String appAccount;

    private String url;
    private User mUser;
    private int mStatus;

    private final static UserManager instance = new UserManager();

    private UserManager() {
    }

    private OnSendMsgListener onSendMsgListener;

    public void setOnSendMsgListener(OnSendMsgListener onSendMsgListener) {
        this.onSendMsgListener = onSendMsgListener;
    }

    public interface OnSendMsgListener {
        void onSent(MIMCMessage message);
        void onStatusChanged(int status);
        void onServerAck(String packetId);
    }

    public static UserManager getInstance() {
        return instance;
    }

    public String getAccount() {
        return appAccount;
    }

    public int getStatus() {
        return mStatus;
    }

    public void addMsg(MIMCMessage mimcMessage) {
        onSendMsgListener.onSent(mimcMessage);
    }

    public void serverAck(String packetId){
        onSendMsgListener.onServerAck(packetId);
    }

    public User getUser(String account) {
        if (!account.equals(appAccount)){
            mUser = newUser(account);
        }
        if (mUser == null) {
            mUser = newUser(account);
        }
        appAccount = account;
        return mUser;
    }

    public User newUser(String appAccount){
        User user = new User(appId, appAccount);
        user.registerTokenFetcher(new TokenFetcher());
        user.registerMessageHandler(new MessageHandler());
        user.registerOnlineStatusListener(new OnlineStatusListener());
        return user;
    }

    class OnlineStatusListener implements MimcOnlineStatusListener {
        @Override
        public void onStatusChanged(int status, int code, String msg) {
            mStatus = status;
            onSendMsgListener.onStatusChanged(status);
        }
    }

    class TokenFetcher implements MIMCTokenFetcher {

        @Override
        public String fetchToken() {
            /**
             * @important!!!
             * appId/appKey/appSec：
             *     小米开放平台(https://dev.mi.com/cosole/man/)申请
             *     信息敏感，不应存储于APP端，应存储在AppProxyService
             * appAccount:
             *      APP帐号系统内唯一ID
             * AppProxyService：
             *     a) 验证appAccount合法性；
             *     b) 访问TokenService，获取Token并下发给APP；
             * !!此为Demo APP所以appId/appKey/appSec存放于APP本地!!
             **/
            url = "https://mimc.chat.xiaomi.net/api/account/token";
            String json = "{\"appId\":" + appId + ",\"appKey\":\"" + appKey + "\",\"appSecret\":\"" + appSecret + "\",\"appAccount\":\"" + appAccount + "\"}";
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request
                    .Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, json))
                    .build();
            Call call = client.newCall(request);
            JSONObject data = null;
            try {
                Response response = call.execute();
                JSONObject object = new JSONObject(response.body().string());
                if (!object.getString("message").equals("success")) {
                    MyLog.w("data failure");
                }
                data = object.getJSONObject("data");
            } catch (Exception e) {
                MyLog.w("http request exception: " + e.getMessage());
            }
            MyLog.w("token:"+data);
            return data.toString();
        }
    }
}

