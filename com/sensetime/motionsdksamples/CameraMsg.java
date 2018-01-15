package com.sensetime.motionsdksamples;

import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_CAMERA;
import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_UI;

/**
 * Created by lyt on 2017/11/18.
 */

public class CameraMsg extends MsgBase{
    public int cmd;
    public String str;

    public CameraMsg() {
        type = MSG_TYPE_CAMERA;
    }
}
