package com.example.cxy.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cxy on 2016/3/23.
 */
public class GameView extends View {

    private Bitmap background;



    private Bitmap puzzImage;  //拼图图像
    private Rect puzzRect;  //拼图区域
    private Rect thumbRect; //缩略图区域
    private Rect cellsRect; //每个被打乱的拼图块左上角所在的区域范围
    private double pw;
    private double ph;
    private Paint paint;

    public List<PuzzleCell> puzzleCells=new ArrayList<PuzzleCell>();

    //游戏进度中保存的拼图块状态对象动态数组
    public List<PuzzCellState> cellStates=new ArrayList<PuzzCellState>();
    private PuzzleCell touchedCell;  //当前被触摸到得拼图块
    private Bitmap backDrawing; //后台界面图像
    private Canvas backCanvas;  //后台界面画布
    private int screenW;
    private int screenH;
    private SoundPool soundPool;  //音效对象
    private int soundId=0;  //声音资源Id

    public GameView(Context context) {

        super(context);
        //设置画笔属性
        paint=new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);   //无锯齿平滑
        paint.setStyle(Paint.Style.STROKE);  //实心线
        //缩放动画
        ScaleAnimation aniScale=new ScaleAnimation(5,1,3,1);
        aniScale.setDuration(800);
        //将动画运用到当前GameView界面
        setAnimation(aniScale);
        initSounds();
    }

    /**
     *初始化音效
     */
    private  void initSounds(){
       soundPool=new SoundPool(4, AudioManager.STREAM_MUSIC,100);
        soundId=soundPool.load(getContext(),R.raw.ir_begin,1);
    }

    /**
     *播放指定的音效
     */
    private void playSound(int soundId){
        //获取系统声音服务
        AudioManager mgr=(AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        //获取系统当前音量和最大音量值
        float currVol=mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVol=mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume=currVol/maxVol;
        soundPool.play(soundId,volume,volume,1,0,1.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //从后台界面缓存中绘制除“当前的拼图块”之外的图像
        canvas.drawBitmap(backDrawing,0,0,null);

        //将被触摸或移动的拼图块画出来
        if (touchedCell!=null){
            touchedCell.draw(canvas);
        }

        //绘制打乱的拼图块区域
        // canvas.drawRect(cellsRect,paint);
    }

    private int dip2px(float dip){
        final  float scale= Resources.getSystem().getDisplayMetrics().density;  //像素密度
        return  (int)(dip*scale+0.5f);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

         screenW=(w>h)?w:h;
         screenH=(w>h)?h:w;
         //初始化游戏；分割拼图块；并在后台画布上绘制游戏界面
         initGame();
        if (cellStates.size()>0){
            loadPuzzCells();
        }
        else{
            makeupPuzzCells();
        }

        drawPuzzle(backCanvas);
    }

    /**
     *从保存的游戏进度中加载拼图块
     */
    private void loadPuzzCells(){
        int row,col;
        Rect puzzR;
        for (PuzzCellState cellState:cellStates){
            //根据图像编号计算原拼图分割中的行列位置
            row=cellState.imgId/4;
            col=cellState.imgId%4;
            puzzR=new Rect((int)(col*pw),(int)(row*ph),(int)((col+1)*pw),(int)((row+1)*ph));

            PuzzleCell cell=new PuzzleCell();
            cell.image=Bitmap.createBitmap(puzzImage,puzzR.left,puzzR.top,puzzR.width(),puzzR.height());
            cell.imgId=cellState.imgId;

            cell.x0=cellState.posx;
            cell.y0=cellState.posy;
            cell.width=(int)pw;
            cell.height=(int)ph;
            cell.zorder=cellState.zOrder;
            cell.fixed=cellState.fixed;

            cell.homeX0=puzzR.left+dip2px(10);
            cell.homeY0=puzzR.top+dip2px(20);
            puzzleCells.add(cell);
        }
        sortPuzzleCells();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act=event.getAction();
        int x=(int)event.getX();
        int y=(int)event.getY();
        //根据触摸动作的类型做出相应的处理
        switch (act){
            case MotionEvent.ACTION_DOWN:
                //确定哪个是被触摸到的拼图块，并将其置顶显示
                for (int i=0;i<puzzleCells.size();i++){
                    PuzzleCell cell=puzzleCells.get(i);
                    if (cell.fixed){
                        continue;
                    }
                    if (cell.isTouched(x,y)){
                        //将当前拼图块显示次序设为最大值加1，以保证拼图块的zorder不重复
                        cell.zorder=getCellMaxzOrder()+1;
                        //重新排序
                        sortPuzzleCells();
                        //在后台界面上绘制一份“干净的”界面
                        drawPuzzle(backCanvas,cell);
                        //保存被触摸拼图块，记录触摸位置点
                        touchedCell=cell;
                        touchedCell.setTouchedPoint(x,y);
                        //通知系统更新界面，会导致自动调用onDraw()方法
                        invalidate(); //会自动调用onDraw()
                        //返回true表示ACTION_DOWN消息已被消费掉
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果有拼图块被触摸滑动，则移动拼图块
                if (touchedCell!=null){
                    //拼图块原位置区域
                    Rect rect1=new Rect(touchedCell.x0,touchedCell.y0,
                            touchedCell.x0+touchedCell.width,touchedCell.y0+touchedCell.height);
                    //移动拼图块到新位置，将导致拼图块左上角（x0，y0）坐标改变
                    touchedCell.moveTo(x,y);
                    //拼图块新位置区域,x0,y0已经改变
                    Rect rect2=new Rect(touchedCell.x0,touchedCell.y0,
                            touchedCell.x0+touchedCell.width,touchedCell.y0+touchedCell.height);
                    //合并新旧两个位置的拼图块区域，构成一个局部重绘区
                    rect2.union(rect1);
                    invalidate(rect2); //会自动调用onDraw（）方法
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchedCell!=null){
                    //计算拼图块左上角与归位目标的距离
                    Point p1=new Point(touchedCell.x0,touchedCell.y0);
                    Point p2=new Point(touchedCell.homeX0,touchedCell.homeY0);
                    double ds=Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));

                    //若小于10dip则归位，同时产生自动“吸附”的效果
                    if (ds<=dip2px(10)){
                        touchedCell.x0=touchedCell.homeX0;
                        touchedCell.y0=touchedCell.homeY0;
                        touchedCell.fixed=true;
                        //播放归位音效
                        playSound(soundId);

                    }
                }
                //手指离开触摸屏，置空touchedCell
                touchedCell=null;
                //重绘整个界面，通知系统更新显示
                drawPuzzle(backCanvas);   //backCanvas将改变backDrawing
                invalidate();               //会自动调用onDraw()方法
                if (checkWin()){
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage("再来一局？");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            puzzleCells=new ArrayList<PuzzleCell>();
                            cellStates=new ArrayList<PuzzCellState>();
                            initGame();
                            makeupPuzzCells();
                            drawPuzzle(backCanvas);
                            invalidate();

                        }
                    });
                    builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                           // android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }
                    });


                    builder.create().show();

                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean checkWin(){

        for (int i=0;i<puzzleCells.size();i++){
            PuzzleCell cell=puzzleCells.get(i);
            if (!cell.fixed){
                return false;
            }
        }
        return true;
     }
    /**
     * 获取所有拼图块中zOrder的最大值
     */
    private int getCellMaxzOrder(){
        int zOrder=-1;
        for (PuzzleCell cell:puzzleCells){
            if (cell.zorder>zOrder)
                zOrder=cell.zorder;
        }
        return  zOrder;
    }

    /**
     * 根据拼图块的zorder进行倒序操作
     */
    private void sortPuzzleCells(){
        Collections.sort(puzzleCells, new Comparator<PuzzleCell>() {
            @Override
            public int compare(PuzzleCell lhs, PuzzleCell rhs) {
                return rhs.zorder-lhs.zorder;
            }
        });
    }

    /**
     * 初始化游戏：各绘图区域计算、图片资源加载、后台画布准备等
     */
    private void initGame(){
        pw=(screenW-dip2px(10)-dip2px(10)-dip2px(20))/5.5;
        ph=(screenH-dip2px(20)-dip2px(20))/3.0;
        //计算拼图区域，缩略图区域，打乱拼图块的区域
        puzzRect=new Rect(dip2px(10),dip2px(20),dip2px(10)+(int)(4*pw),dip2px(20)+(int)(3*ph));
        thumbRect=new Rect(dip2px(10)+(int)(4*pw)+dip2px(10),dip2px(20),
                screenW-dip2px(10),(int)(dip2px(20)+ph));
        cellsRect=new Rect(dip2px(10)+(int)(4*pw)+dip2px(10),
                (int)(dip2px(20)+ph+dip2px(5)),
                (int)(screenW-dip2px(10)-pw),
                (int)(screenH- dip2px(20)-ph));
        //加载背景图片
        Bitmap bg= BitmapFactory.decodeResource(getResources(),R.mipmap.wallpaper);
        background=Bitmap.createScaledBitmap(bg,screenW,screenH,false);
        //背景图经过缩放处理，原图像可要求释放掉
        bg.recycle();
        //加载拼图图片，按拼图区域大小缩放，然后释放原始拼图图像
        Bitmap pic=BitmapFactory.decodeResource(getResources(),R.mipmap.pic02);
        puzzImage=Bitmap.createScaledBitmap(pic,puzzRect.width(),puzzRect.height(),false);
        pic.recycle();
        //创建后台界面图像，大小与屏幕相同，且像素格式为32位（Alpha、R\G\B)
        backDrawing=Bitmap.createBitmap(screenW,screenH,Bitmap.Config.ARGB_8888);
        //创建后台画布,以后backCanvas上所有的绘图结果都会保存到backDrawing图像上，backDrawing随着backCanvas的改变而改变
        backCanvas=new Canvas(backDrawing);
    }

    /**
     * 将拼图按3*4的大小分割成12个拼图块
     */
    private  void makeupPuzzCells(){
        Set<Integer> zOrders=new HashSet<Integer>();
        Rect puzzR;
        for (int i=0;i<3;i++){
            for (int j=0;j<4;j++){
                //计算第（i，j)拼图块在原拼图中的区域
                puzzR=new Rect((int)(j*pw),(int)(i*ph),(int)((j+1)*pw),(int)((i+1)*ph));

                //创建PuzzleCell对象，设置它的各个属性值
                PuzzleCell cell=new PuzzleCell();
                cell.imgId=i*4+j;
                cell.image=Bitmap.createBitmap(puzzImage,puzzR.left,puzzR.top,puzzR.width(),puzzR.height());
                cell.width=(int)pw;
                cell.height=(int)ph;

                cell.x0=cellsRect.left+(int)(Math.random()*cellsRect.width());
                cell.y0=cellsRect.top+(int)(Math.random()*cellsRect.height());
                //随机产生一个不重复的拼图块堆叠显示次序
                int zOrder;
                do {
                    zOrder=(int)(12*Math.random());
                }while(zOrders.contains(zOrder));

                zOrders.add(zOrder);
                cell.zorder=zOrder;
                //确定拼图块的归位区域，同时初始状态为“未归位”
                cell.homeX0=(int)(j*pw)+dip2px(10);
                cell.homeY0=(int)(i*ph)+dip2px(20);
                cell.fixed=false;
                puzzleCells.add(cell);
            }
        }

//        Collections.sort(puzzleCells, new Comparator<PuzzleCell>() {
//            @Override
//            //拼图块c0和c1顺序由compare()返回值确定
//            public int compare(PuzzleCell c0, PuzzleCell c1) {
//                return c1.zorder-c0.zorder;
//            }
//        });
              sortPuzzleCells();

    }

    private void drawPuzzle(Canvas canvas){
        drawPuzzle(canvas,null);
    }


    /**
     * 在canvas中绘制整个拼图界面
     */
    private void drawPuzzle(Canvas canvas,PuzzleCell ignoredCell){
        //绘制背景图
        canvas.drawBitmap(background, 0, 0, null);
        //绘制拼图
        Paint p=new Paint();
        p.setAlpha(0);  //设置透明度，0为完全透明，255不透明
         canvas.drawBitmap(puzzImage,null,puzzRect,p);
        //绘制缩略图
        canvas.drawBitmap(puzzImage,null,thumbRect,null);
        //绘制打乱的拼图块区域
       // canvas.drawRect(cellsRect,paint);
        //绘制拼图区域边框
        canvas.drawRect(puzzRect, paint);
        //绘制水平格子线
        canvas.drawLine(puzzRect.left,(int)ph+puzzRect.top,puzzRect.right,(int)ph+puzzRect.top,paint);
        canvas.drawLine(puzzRect.left,(int)(ph*2)+puzzRect.top,puzzRect.right,(int)(ph*2)+puzzRect.top,paint);
        //绘制垂直格子线
        canvas.drawLine((int)pw+puzzRect.left,puzzRect.top,(int)pw+puzzRect.left,puzzRect.bottom,paint);
        canvas.drawLine((int)(pw*2)+puzzRect.left,puzzRect.top,(int)(pw*2)+puzzRect.left,puzzRect.bottom,paint);
        canvas.drawLine((int)(pw*3)+puzzRect.left,puzzRect.top,(int)(pw*3)+puzzRect.left,puzzRect.bottom,paint);




        //绘制除ignoredCell之外的11个拼图块
        for (int i=puzzleCells.size()-1;i>=0;i--){
            PuzzleCell cell=puzzleCells.get(i);
            // canvas.drawBitmap(cell.image,cellsRect.left,cellsRect.top,null);
            if (cell!=ignoredCell)
             cell.draw(canvas);
        }
    }
}