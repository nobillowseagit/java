package com.sensetime.motionsdksamples.frameAnimation;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by wangnannan on 2017/11/1.
 */

public class ImageUtils {
    /**
     * 获取需要播放的动画资源
     */
    public static int[] getImageRes(Context context, int rid) {
        TypedArray typedArray = context.getResources().obtainTypedArray(rid);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

}
