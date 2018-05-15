package com.cxy.yuwen.jsInterface;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cxy.magazine.activity.PhotoBrowserActivity;

// js通信接口
public class JavascriptInterface {

    private Context context;
    private String [] imageUrls;


    public JavascriptInterface(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent();
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("curImageUrl", img);
        intent.setClass(context, PhotoBrowserActivity.class);
        context.startActivity(intent);
        for (int i = 0; i < imageUrls.length; i++) {
            Log.e("图片地址"+i,imageUrls[i].toString());
        }
    }


}
