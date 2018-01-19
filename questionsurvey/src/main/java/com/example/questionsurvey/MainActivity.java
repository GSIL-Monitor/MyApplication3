package com.example.questionsurvey;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import Util.ACache;
import Util.PermissionHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BasicActivity {

    @BindView(R.id.btn_sysnc) Button btnSysnc;
    @BindView(R.id.btn_upload) Button btnUpload;
    @BindView(R.id.btn_fill)  Button btnFill;

    protected PermissionHelper mPermissionHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
      //  mCache=ACache.get(this);
        checkPermmion(this);


    }

    public void checkPermmion(Activity activity){
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(activity);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(LOG_TAG, "All of requested permissions has been granted, so run app logic.");

            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(LOG_TAG, "The api level of system is lower than 23, so run app logic directly.");

        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(LOG_TAG, "All of requested permissions has been granted, so run app logic directly.");

            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(LOG_TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    //同步
    @OnClick(R.id.btn_sysnc)
    public void sysnc(){
        Intent intent=new Intent(MainActivity.this,SyncActivity.class);
        startActivity(intent);
    }

    //上传
    @OnClick(R.id.btn_upload)
    public void upload(){
        Intent intent=new Intent(MainActivity.this,UploadActivity.class);
        startActivity(intent);
    }

    //填写问卷
    @OnClick(R.id.btn_fill)
    public void fillQuestion(){
        Intent intent=new Intent(MainActivity.this,FillActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_logout){
         //   Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show();
            mCache.put("AutoLogin","0");
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.menu_exit){
            MyApplication.getInstance().exit();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        MyApplication.getInstance().exit();

    }
}
