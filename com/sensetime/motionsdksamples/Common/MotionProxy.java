package com.sensetime.motionsdksamples.Common;

/**
 * Created by lyt on 2017/10/24.
 */

public class MotionProxy implements IMotion {
    private IMotion mIMotion = null;

    public MotionProxy(IMotion iMotion){
        this.mIMotion = iMotion;
    }
}
