package com.example.cxy.myapplication;

import android.app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    //用于输出调试信息的TAG
    public static final String TAG="PT_GAME";
    //设定存储名
    public  static final String PREFS_STRING="PT_PROGRESS";
    //游戏进度数据要传递给GameView
    private GameView myView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐去Android顶部状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //隐去程序标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        myView=new GameView(this);
        loadGameProgress();
        //设置显示GameView界面
        setContentView(myView);

    }

    @Override
    protected void onPause() {

        super.onPause();
        //Activity结束之前保存游戏进度
        saveGameProgress();
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
