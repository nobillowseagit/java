package com.sensetime.motionsdksamples.Utils;

/**
 * Created by lyt on 2017/10/19.
 */

public class UniqueId {
    public static long getUid() {
        return System.currentTimeMillis();
    }

    public static String getStrUid() {
        String string = "a";
        string += String.valueOf(System.currentTimeMillis());
        return string;
    }
}
