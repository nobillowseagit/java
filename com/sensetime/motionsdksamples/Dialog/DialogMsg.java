package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_DIALOG;

/**
 * Created by lyt on 2017/10/13.
 */

public class DialogMsg extends MsgBase {
    public DIALOG_CMD cmd;
    public DialogContext context;

    public DialogMsg() {
        super();
        type = MSG_TYPE_DIALOG;
    }

    public enum DIALOG_CMD {
        DIALOG_CMD_IDLE,
        DIALOG_CMD_SOUND_TRIGGER, DIALOG_CMD_VISION_TRIGGER,
        DIALOG_CMD_NLU_RES, DIALOG_CMD_NLG_RES, DIALOG_CMD_TTS_RES,
        DIALOG_CMD_NEW, DIALOG_CMD_REGISTERED,
        DIALOG_CMD_GUESS, DIALOG_CMD_PHOTOGRAPH,
        DIALOG_CMD_NEW_PERSON_RES, DIALOG_CMD_SET_PERSON,
        DIALOG_CMD_NEW_PERSON, DIALOG_CMD_START, DIALOG_CMD_NLU_REQ, DIALOG_CMD_NLG_REQ, DIALOG_CMD_TTS_REQ, DIALOG_CMD_REGISTERED_PERSON
    }
}
