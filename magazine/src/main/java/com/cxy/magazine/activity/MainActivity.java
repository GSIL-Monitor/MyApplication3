package com.cxy.magazine.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.cxy.magazine.BuildConfig;
import com.cxy.magazine.MyApplication;
import com.cxy.magazine.R;
import com.cxy.magazine.bmobBean.MsgNotification;
import com.cxy.magazine.bmobBean.MsgReadRecord;
import com.cxy.magazine.bmobBean.PatchBean;
import com.cxy.magazine.bmobBean.User;
import com.cxy.magazine.fragment.ClassFragment;
import com.cxy.magazine.fragment.FirstFragment;
import com.cxy.magazine.fragment.MyFragment;
import com.cxy.magazine.fragment.RecommFragment;
import com.cxy.magazine.fragment.ShelfFragment;
import com.cxy.magazine.util.Constants;
import com.cxy.magazine.util.NetWorkUtils;
import com.cxy.magazine.util.OkHttpUtil;
import com.cxy.magazine.util.PermissionHelper;
import com.cxy.magazine.util.Utils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
//import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends BasicActivity {

    private String tabs[]={"first","class","shelf","mine"};
    private static final String BmobApplicationId ="be69c91d46af21288d5b855ee9fe158e";
    protected PermissionHelper mPermissionHelper;
    private  FragmentManager manager=null;
    private static final int REQUEST_INSTALL = 125;
    private   BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_class:
                   // switchFragmentSupport(R.id.content,tabs[1]);
                    showFragment(1);
                    return true;
                case R.id.navigation_shelf:
                   // switchFragmentSupport(R.id.content,tabs[2]);
                    showFragment(2);
                    return true;
                case R.id.navigation_mine:
                   // switchFragmentSupport(R.id.content,tabs[3]);
                    showFragment(3);
                    return true;
                case R.id.navigation_recomm:
                   // switchFragmentSupport(R.id.content,tabs[0]);
                    showFragment(0);
                    return true;

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查连接性
        checkNetworkConnected();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //初始化Bmob
        Bmob.initialize(this,BmobApplicationId,"bmob");

        manager=getSupportFragmentManager();

       // navigation.setSelectedItemId(R.id.content);  //设置默认选中项
      //  switchFragmentSupport(R.id.content,tabs[0]);

        Fragment fragment=manager.findFragmentByTag(tabs[0]);
        if (fragment==null){
            showFragment(0);
        }
        selectNoReadMsg();


        //检查权限
        checkPermmion(this);

    }
    public  void  selectNoReadMsg() {
        final User user = BmobUser.getCurrentUser(User.class);//获取自定义用户信息
        if (null != user) {
            BmobQuery<MsgNotification> msgQuery = new BmobQuery<>();
            msgQuery.addWhereEqualTo("user", user);
            msgQuery.addWhereEqualTo("isRead", false);
            msgQuery.count(MsgNotification.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        if (integer > 0) {   //有未读消息
                            setBadge();
                        } else {
                            selectSystemMsg(user);
                        }
                    } else {
                        Utils.toastMessage(MainActivity.this, e.getMessage());
                        Log.i("bmob", e.toString());
                    }

                }
            });


        }
    }

    public void selectSystemMsg( final User user){
        BmobQuery<MsgNotification> msgQuery = new BmobQuery<>();
        msgQuery.addWhereEqualTo("msgType",2);
        msgQuery.count(MsgNotification.class, new CountListener() {  //统计系统消息的总数
            @Override
            public void done(final Integer count1, BmobException e) {
                //查询系统信息阅读记录
                if (e==null){
                    BmobQuery<MsgReadRecord>  recordQuery=new BmobQuery<>();
                    recordQuery.addWhereEqualTo("user",user);
                    recordQuery.count(MsgReadRecord.class, new CountListener() {  //统计该用户已读的系统消息数量
                        @Override
                        public void done(Integer count2, BmobException exception) {

                            if (exception==null && count1>count2){   //有未读的系统消息
                                setBadge();
                            }
                        }
                    });
                }

            }
        });
    }

    public void setBadge(){
        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;

        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.badge, menuView, false);
        ImageView msgImg = (ImageView) badge.findViewById(R.id.msg_notify_img);
        msgImg.setVisibility(View.VISIBLE);
        //添加到Tab上
        itemView.addView(badge);
    }


    @Override
    protected void onStart() {
        super.onStart();
       /* boolean netConnect=NetWorkUtils.isNetworkConnected(this);
        if (netConnect){
            //小米更新
         //  XiaomiUpdateAgent.update(this);//这种情况下, 若本地版本是debug版本则使用沙盒环境，否则使用线上环境

        }*/




    }

    //检查更新
    public void checkUpdate(){

        BmobQuery<PatchBean> query=new BmobQuery<PatchBean>();
        query.findObjects(new FindListener<PatchBean>() {
            @Override
            public void done(List<PatchBean> list, BmobException e) {
                  if (list!=null&&list.size()==1){
                      final PatchBean patchBean=list.get(0);
                      Integer remoteVersion=patchBean.getPatchVersion();
                      int currentVersion = BuildConfig.VERSION_CODE;
                      if (remoteVersion>currentVersion){   //服务器有新版本
                          //TODO:有新版本
                          Utils.showConfirmCancelDialog(MainActivity.this, "提示", "检查到新版本(约6M)，是否下载？", new QMUIDialogAction.ActionListener() {
                              @Override
                              public void onClick(QMUIDialog dialog, int index) {
                                  //下载新版本
                                  dowLoadFile(patchBean.getPatchFile());
                              }
                          });
                      }
                  }
            }
        });
    }
    String filePath="";
    public void dowLoadFile(BmobFile file){
      //  Utils.toastMessage(MainActivity.this,"正在下载更新");

        Utils.showTipDialog(MainActivity.this,"正在下载...", QMUITipDialog.Builder.ICON_TYPE_LOADING);

        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        final File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());  //Environment.getExternalStorageDirectory()

    //    Log.i(LOG_TAG,"下载路径2:"+saveFile.getPath());
        file.download( saveFile,new DownloadFileListener() {
            @Override
            public void onStart() {
              //  Utils.toastMessage(MainActivity.this,"开始下载...");
            }

            @Override
            public void done(String savePath, BmobException e) {
                filePath=savePath;
                if(e==null){
                    Utils.dismissDialog();
                    if (Build.VERSION.SDK_INT >= 26) {
                        boolean b = getPackageManager().canRequestPackageInstalls();
                        if (b) {
                            //安装应用的逻辑(写自己的就可以)
                            installApk(savePath);
                        } else {
                            //请求安装未知应用来源的权限
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_INSTALL);
                        }
                    } else {  //小于26，直接安装
                        installApk(savePath);
                    }

                }else{
                    Log.e(LOG_TAG,"下载失败："+e.getErrorCode()+","+e.getMessage());
                    Utils.toastMessage(MainActivity.this,"下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
              //  Log.i(LOG_TAG,"下载进度："+value+","+newworkSpeed);
            }
        });
    }

    public void  installApk(String filePath){
        File file=new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = dealUri_N(getApplicationContext(), intent, file );
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
    /**
     * 处理安卓版本7.0以上，读取文件的版本
     * @param context   context
     * @param intent    intent
     * @param file  待读取的文件
     * @return  格式化后的文件读取路径
     */
    public  Uri dealUri_N(Context context, Intent intent, File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            if (intent != null)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            return FileProvider.getUriForFile(context, context.getPackageName() +".fileprovider", file);
        }else {
            return Uri.fromFile(file);
        }
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
            //检查更新
            boolean netConnect=NetWorkUtils.isNetworkConnected(this);
            if (netConnect){
                checkUpdate();

            }
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(LOG_TAG, "All of requested permissions has been granted, so run app logic directly.");
                //检查更新
                boolean netConnect=NetWorkUtils.isNetworkConnected(this);
                if (netConnect){
                    checkUpdate();

                }



            } else {
                // 如果还有权限未申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(LOG_TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();

            }
        }
    }

    private void checkNetworkConnected(){
        final String domain="http://www.fx361.com/";
        new Thread(new Runnable() {
            @Override
            public void run() {
               OkHttpUtil.checkConnected(domain);

            }
        }).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INSTALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk(filePath);
            } else {
                //启动授权页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_INSTALL);
            }
        }
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL) {
            if (resultCode==RESULT_OK ) {
                installApk(filePath);
            } else {
               Utils.toastMessage(MainActivity.this,"由于缺少权限，无法安装应用");
            }
        }
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    private FirstFragment firstFragment;
    private ClassFragment classFragment;
    private ShelfFragment shelfFragment;
    private MyFragment myFragment;

    public void showFragment(int index) {
        FragmentTransaction ft = manager.beginTransaction();
        firstFragment=(FirstFragment) manager.findFragmentByTag(tabs[0]);
        classFragment=(ClassFragment)manager.findFragmentByTag(tabs[1]);
        shelfFragment=(ShelfFragment)manager.findFragmentByTag(tabs[2]);
        myFragment=(MyFragment)manager.findFragmentByTag(tabs[3]);
        hideFragment(ft);
        //注意这里设置位置
       // position = index;
        switch (index) {
            case 0:
                /**
                 * 如果Fragment为空，就新建一个实例
                 * 如果不为空，就将它从栈中显示出来
                 */
                if (firstFragment == null) {
                    FirstFragment recommFragment1 = new FirstFragment();
                    ft.add(R.id.content, recommFragment1,tabs[0]);

                } else {
                    ft.show(firstFragment);
                }
                break;
            case 1:
                if (classFragment == null) {
                    ClassFragment classFragment1 = new ClassFragment();
                    ft.add(R.id.content, classFragment1,tabs[1]);
                } else {
                    ft.show(classFragment);
                }
                break;
            case 2:
                if (shelfFragment == null) {
                   ShelfFragment shelfFragment1 = new ShelfFragment();
                    ft.add(R.id.content, shelfFragment1,tabs[2] );
                } else {
                    ft.show(shelfFragment);
                }
                break;
            case 3:
                if (myFragment == null) {
                    MyFragment myFragment1 = new MyFragment();
                    ft.add(R.id.content, myFragment1,tabs[3]);
                } else {
                    ft.show(myFragment);
                }
                break;
        }
        ft.commitAllowingStateLoss();
    }

    public void hideFragment(FragmentTransaction ft) {
        //如果不为空，就先隐藏起来
        if (firstFragment != null) {
            ft.hide(firstFragment);
        }
        if (classFragment != null) {
            ft.hide(classFragment);
        }
        if (shelfFragment != null) {
            ft.hide(shelfFragment);
        }
        if (myFragment != null) {
            ft.hide(myFragment);
        }
    }

    /**
     * 解决屏幕旋转时：重复添加fragment。
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
