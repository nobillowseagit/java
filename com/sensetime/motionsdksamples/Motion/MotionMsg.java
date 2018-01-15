package com.sensetime.motionsdksamples.Motion;

import com.sensetime.motionsdksamples.Common.SoundLocation;
import com.sensetime.motionsdksamples.Common.FaceLocation;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_MOTION;

/**
 * Created by lyt on 2017/10/13.
 */

public class MotionMsg extends MsgBase {
    public MOTION_CMD mCmd;
    public FaceLocation mFaceLocation;
    public SoundLocation mSoundLocation;

    public MotionMsg() {
        super();
        type = MSG_TYPE_MOTION;
    }

    public enum MOTION_CMD {
        MOTION_CMD_FACE, MOTION_CMD_SOUND
    }
}
