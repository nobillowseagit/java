package com.sensetime.motionsdksamples.Common;

/**
 * Created by lyt on 2017/10/13.
 */

public class Rectangle {
    public int bottom = 0;
    public int left = 0;
    public int right = 0;
    public int top = 0;
    public int area = 0;
    //MotionServer.Point center;
    public int centerX = 0;
    public int centerY = 0;

    public Rectangle(int bottom, int left, int right, int top) {
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
        if (0 != bottom && 0 != right) {
            calcArea();
            calcCenter();
        }
    }

    public void calcCenter() {
        centerX = (right + left) / 2 ;
        centerY = (bottom + top) / 2;
    }

    public void calcArea() {
        int height, width;
        height = right - left;
        width = bottom - top;
        area = height * width;
    }

}
