package com.sensetime.motionsdksamples.Common;

import android.graphics.Point;

/**
 * Created by lyt on 2017/10/24.
 */

public class FaceLocation {
    public Rectangle mRecFace;
    public Rectangle mRecScene;
    public Point mLeftEye;
    public Point mRightEye;
    public float mEysDist;
    public Rectangle mLeftFaceRec;
    public Rectangle mRightFaceRec;


    public FaceLocation() {
        mRecScene = new Rectangle(2,1, 2,1);
        mRecFace = new Rectangle(2,1, 2,1);
        mLeftEye = new Point(1, 1);
        mRightEye = new Point(2, 2);
    }
}
