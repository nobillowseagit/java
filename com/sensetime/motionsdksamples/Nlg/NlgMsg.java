package com.sensetime.motionsdksamples.Nlg;

import com.sensetime.motionsdksamples.Dialog.DialogContext;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

/**
 * Created by lyt on 2017/10/13.
 */

public class NlgMsg extends MsgBase {
    public NLG_CMD cmd;
    public DialogContext context;

    public NlgMsg() {
        super();
        type = MSG_TYPE.MSG_TYPE_NLG;
    }

    public enum NLG_CMD {
        NLG_CMD_REQ, NLG_CMD_RES
    }
}
