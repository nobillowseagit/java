package com.sensetime.motionsdksamples;

import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_UI;

/**
 * Created by lyt on 2017/11/18.
 */

public class UiMsg extends MsgBase{
    public enum UI_CMD {
        UI_CMD_SOUND_TRIGGER, UI_CMD_VISION_TRIGGER,
        UI_CMD_LISTEN,
        UI_CMD_LISTENED, UI_CMD_WATCHED,
        UI_CMD_THINKED,
        UI_CMD_SPEAK_COMPLETED,
        UI_CMD_TIMEOUT, UI_CMD_RUN_FAILED,
        UI_CMD_STRING, UI_CMD_EMOTION
    }
    public UI_CMD cmd;
    public String string;
    public int emotion;

    public UiMsg() {
        type = MSG_TYPE_UI;
    }
}
