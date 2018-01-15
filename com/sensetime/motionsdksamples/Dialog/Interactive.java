package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.MotionServer;
import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.UiMsg;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_CONTROL;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_GENEREL;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_HELLO;

/**
 * Created by lyt on 2017/11/6.
 */

public class Interactive extends DialogBase {
    private static Interactive instance = new Interactive();
    public static Interactive getInstance() {
        return instance;
    }

    private MotionServer mMotionServer;

    public Interactive() {
        super(DOMAIN_CONTROL);
        mMotionServer = MotionServer.getInstance();
    }

    public void init() {
    }

    @Override
    public void start(DialogContext context) {
        super.start(context);
    }

    @Override
    public void nluRes(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void nlgRes(DialogContext context) {
        UiMsg uiMsg = new UiMsg();

        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "交互":
                    if (null != context.nlgActParamStr) {
                        switch (context.nlgActParamStr) {
                            case "微笑":
                                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                uiMsg.emotion = 1;
                                EventBus.getDefault().post(uiMsg);
                                break;
                            case "睡觉":
                                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                uiMsg.emotion = 2;
                                EventBus.getDefault().post(uiMsg);
                                break;
                            case "摇头":
                                //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                //uiMsg.emotion = 2;
                                //EventBus.getDefault().post(uiMsg);
                                mMotionServer.shakeHead();
                                break;
                            case "点头":
                                //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                //uiMsg.emotion = 2;
                                //EventBus.getDefault().post(uiMsg);
                                mMotionServer.nodHead();
                                break;
                            case "左转":
                                //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                //uiMsg.emotion = 2;
                                //EventBus.getDefault().post(uiMsg);
                                mMotionServer.turnLeft();
                                break;
                            case "右转":
                                //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                //uiMsg.emotion = 2;
                                //EventBus.getDefault().post(uiMsg);
                                mMotionServer.turnRight();
                                break;
                        }
                    }
                    break;
            }
        }

        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void ttsRes(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

}
