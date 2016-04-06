package com.example.cxy.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Created by cxy on 2016/3/23.
 */
public class PuzzleCell {

    public Bitmap image;
    public int imgId;
    public int width;
    public int height;
    public  int x0; //拼图块左上角在屏幕上显示的位置
    public int y0;
    public int zorder;
    public Point touchedPoint;//拼图块被触摸或移动时的触摸点
    public int homeX0;
    public int homeY0;
    public boolean fixed;

    /**
     * 将当前拼图块绘制出来
     */
    public void draw(Canvas canvas){

        canvas.drawBitmap(image,x0,y0,null);
    }

    /** 判断当前拼图块是否被触摸到
     *如果触摸点在当前拼图块区域内，返回true
     */
    public boolean isTouched(int x,int y){
        if (x>=x0&&x<=x0+width&&y>=y0&&y<=y0+height)
            return true;
        else
            return false;
    }

    /**
     * 记录拼图块被触摸时的触摸点坐标位置
     * @param x
     * @param y
     */
    public void setTouchedPoint(int x,int y){
        if (touchedPoint==null){
            touchedPoint=new Point(x,y);
        }
        touchedPoint.set(x,y);
    }

    public void moveTo(int x,int y){
        int dx=x-touchedPoint.x;
        int dy=y-touchedPoint.y;

        x0=x0+dx;
        y0=y0+dy;

        setTouchedPoint(x,y);
    }
}
