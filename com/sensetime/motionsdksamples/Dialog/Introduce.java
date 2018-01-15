package com.sensetime.motionsdksamples.Dialog;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_INTRODUCE;

/**
 * Created by lyt on 2017/11/6.
 */

public class Introduce extends Domain {
    private static Introduce instance = new Introduce();
    public static Introduce getInstance() {
        return instance;
    }

    public Introduce () {
        super(DOMAIN_INTRODUCE);
        DomainOperation operation = new IntroduceOperation();
        setDomainOperation(operation);
    }

    public void handleNluRes(DialogContext context) {
        //DialogContext context = new DialogContext();
        context.domain = this;
        context.person = getCurrentPerson();
        context.nlgReqStr = context.nluResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void handleNlgRes(DialogContext context) {
        //DialogContext context = new DialogContext();
        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        EventBus.getDefault().post(msg);
    }

    public void tts(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

    public class IntroduceOperation implements DomainOperation {

        @Override
        public void preprocess(DialogContext context) {

        }

        @Override
        public void process(DialogContext context) {
            switch (context.step) {
                case DIALOG_NLU_RES:
                    handleNluRes(context);
                    break;
                case DIALOG_NLG_RES:
                    handleNlgRes(context);
                    break;
                case DIALOG_TTS_RES:
                    tts(context);
                    break;
            }
        }

        @Override
        public void postprocess(DialogContext context) {

        }
    }
}
