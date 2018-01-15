package com.sensetime.motionsdksamples.Face;

import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

/**
 * Created by lyt on 2017/10/13.
 */

public class FaceMsg extends MsgBase {
    public FACE_CMD mCmd;

    public FaceMsg() {
        super();
        type = MSG_TYPE.MSG_TYPE_FACE;
    }

    public enum FACE_CMD {
    }
}
