package com.kevin.searchPeople.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kevin.searchPeople.R;
import com.kevin.searchPeople.activity.EnterActivity;
import com.kevin.searchPeople.activity.IdentifyActivity;
import com.kevin.searchPeople.fragment.basic.PictureSelectFragment;
import com.kevin.searchPeople.util.Constant;
import com.kevin.searchPeople.util.OkHttpUtil;

import java.io.IOException;

import butterknife.Bind;

import static com.kevin.searchPeople.util.Constant.TAG;

/**
 * 版权所有：XXX有限公司
 *
 * MainFragment
 *
 * @author zhou.wenkai ,Created on 2016-5-5 10:25:49
 * 		   Major Function：<b>MainFragment</b>
 *
 *         注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * @author mender，Modified Date Modify Content:
 */
public class MainFragment extends PictureSelectFragment {
  
    String imageUrl="";

   /* *//** Toolbar *//*
    @Bind(R.id.toolbar)
    Toolbar toolbar;*/

    @Bind(R.id.main_frag_picture_iv)
    ImageView mPictureIv;

    @Bind(R.id.enterButton)
    Button entenrButton;

    @Bind(R.id.identifyButton)
    Button identifyButton;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initViews(View view) {
        //initToolbar(toolbar);
    }

    @Override
    public void initEvents() {
        // 设置图片点击监听
        mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });
        // 设置裁剪图片结果监听
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
//                mPictureIv.setImageBitmap(bitmap);

                String filePath = fileUri.getEncodedPath();
                final String imagePath = Uri.decode(filePath);

                uploadImage(imagePath);
              //  Log.i(TAG,"裁剪");
                Log.i(TAG,"手机地址："+imagePath);

            }
        });

        //录入按钮监听事件
        entenrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), EnterActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                startActivity(intent);
            }
        });

        //识别按钮监听事件
        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent=new Intent(getContext(), IdentifyActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 上传图片
     * @param imagePath
     */
    private void uploadImage(String imagePath) {
        Log.i(TAG,"start upload");
        new NetworkTask().execute(imagePath);
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            if(!"error".equals(result)) {
                Log.i(TAG, "图片地址 " + Constant.BASE_URL + result);
                imageUrl=Constant.BASE_URL + result;
                Glide.with(mContext)
                        .load(imageUrl)
                        .into(mPictureIv);
            }
        }
    }

    /**
     * 上传事件
     * @param imagePath
     * @return
     */
    private String doPost(String imagePath) {



        String result ="";
   /*     MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID
//        builder.addFormDataPart("userId", "20160519142605");
        builder.addFormDataPart("image", imagePath,
                RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/uploadimage")
                .post(requestBody)
                .build();

        Log.d(TAG, "请求地址 " + Constant.BASE_URL + "/uploadimage");
        try{
            Response response = client.newCall(request).execute();
            Log.d(TAG, "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d(TAG, "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            result=OkHttpUtil.post(imagePath);
            Log.d(TAG, "返回结果" + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
