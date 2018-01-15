package com.sensetime.motionsdksamples.Common;

import static java.lang.Thread.sleep;

/**
 * Created by lyt on 2017/10/24.
 */

public class FaceProxy implements IFace {
    private IFace mIFace = null;
    public FaceLocation mFaceLocation;
    public FaceAttr mFaceAttr;

    public FaceProxy(IFace iFace){
        this.mIFace = iFace;
    }

    @Override
    public FaceLocation getFaceLocation() {
        return mIFace.getFaceLocation();
    }

    @Override
    public FaceAttr getFaceAttr() {
        return mIFace.getFaceAttr();
    }

    public FaceAttr getFaceAttrSync() {
        FaceAttr faceAttr;
        while (true) {
            faceAttr = mIFace.getFaceAttr();
            if (faceAttr.mStrId != null) {
                break;
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return faceAttr;
    }

}
