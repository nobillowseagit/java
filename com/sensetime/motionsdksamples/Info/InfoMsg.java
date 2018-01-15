package com.sensetime.motionsdksamples.Info;

import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;

/**
 * Created by lyt on 2017/10/13.
 */

public class InfoMsg extends MsgBase {
    public INFO_CMD mCmd;
    public Person mPerson;
    public enum INFO_CMD {
        INFO_CMD_DETECTED, INFO_CMD_DEL, INFO_CMD_GETALL, INFO_CMD_INSERT, INFO_CMD_OLD, INFO_CMD_KNOWN, INFO_CMD_TEST, INFO_CMD_MODIFY,
        INFO_CMD_NEW_PERSON, INFO_CMD_SET_PERSON
    }

    public InfoMsg() {
        super();
        type = MSG_TYPE.MSG_TYPE_INFO;
    }
}
