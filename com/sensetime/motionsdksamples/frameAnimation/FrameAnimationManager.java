package com.sensetime.motionsdksamples.frameAnimation;

import android.content.Context;
import android.widget.ImageView;

/**
 * author: wangnannan
 * date: 2017/11/28 14:18
 * desc: TODO
 */

public class FrameAnimationManager {
    private final static String TAG = "FrameAnimationManager";
    private static FrameAnimationManager mFrameAnimationManager = new FrameAnimationManager();
    private FrameAnimationUtils mFrameAnimationUtils;

    public static FrameAnimationManager getInstance() {

        return mFrameAnimationManager;
    }

    public void setCurrencyMethod(Context mContext, ImageView mIvImage, int imageId, int duration, boolean isRepeat) {
        if (mFrameAnimationUtils != null && !mFrameAnimationUtils.isPause()) {
            mFrameAnimationUtils.pauseAnimation();
        }
        mFrameAnimationUtils = null;
        mFrameAnimationUtils = new FrameAnimationUtils(mIvImage, ImageUtils.getImageRes(mContext, imageId), duration, isRepeat);
    }
}
