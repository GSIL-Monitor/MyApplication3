package com.example.cxy.myapplication;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;




public class MainActivity extends Activity {


    //用于输出调试信息的TAG
    public static final String TAG="PT_GAME";
    //设定存储名
    public  static final String PREFS_STRING="PT_PROGRESS";
    //游戏进度数据要传递给GameView
    private GameView myView;
    protected GestureDetector mGestureDetector;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //隐去Android顶部状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        //隐去程序标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        myView=new GameView(this);

        loadGameProgress();

        //设置显示GameView界面
       setContentView(myView);
        Log.i("Life","onCreate()");
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                if(e1.getRawX() - e2.getRawX() > 200){
//                    showNext();//向左滑动，显示图片列表
//                    return true;
//                }

                if(e2.getRawX() - e1.getRawX() > 200){
                    showNext();//向右滑动，显示图片列表
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public void showNext() {
        Intent intent = new Intent(this, MapListActivity.class);
        startActivity(intent);
        //finish();
        //调用此方法让动画效果生效
        //overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {

        super.onPause();
        //Activity结束之前保存游戏进度
        saveGameProgress();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadGameProgress();
        Log.i("Life","onRestart");
        if (GameView.isFinish){
            myView=new GameView(this);
            setContentView(myView);
            Log.i("Life","更新了界面");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings=getSharedPreferences(PREFS_STRING,MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        String progress="";
        editor.putString("PROGRESS",progress);

        editor.commit();
    }

    /**
     * 保存当前游戏进度
     */
    private void saveGameProgress(){
        SharedPreferences settings=getSharedPreferences(PREFS_STRING,MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        String progress="";
        for(PuzzleCell cell:myView.puzzleCells){
            String s=String.format("%d|%d|%d|%d|%s",cell.imgId,cell.x0,cell.y0,cell.zorder,Boolean.toString(cell.fixed));

            progress=progress+s+"#";
        }

        //将所有拼图块的状态字符串保存起来
        editor.putString("PROGRESS",progress);

        editor.commit();
    }

    /**
     * 加载以前保存的游戏进度
     */
    private void loadGameProgress(){
        myView.cellStates.clear();
        try {
            SharedPreferences settings=getSharedPreferences(PREFS_STRING,MODE_PRIVATE);
            String progress=settings.getString("PROGRESS","");

            String[] states=progress.split("[#]");
            for (String one:states){
                String[] props=one.split("[|]");

                PuzzCellState pcs=new PuzzCellState();
                pcs.imgId=Integer.parseInt(props[0]);
                pcs.posx=Integer.parseInt(props[1]);
                pcs.posy=Integer.parseInt(props[2]);
                pcs.zOrder=Integer.parseInt(props[3]);
                pcs.fixed=Boolean.parseBoolean(props[4]);

                myView.cellStates.add(pcs);  //加入到myView类的cellStates数组中

            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }
}
