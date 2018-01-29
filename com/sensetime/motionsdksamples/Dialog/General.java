package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.FaceAttr;
import com.sensetime.motionsdksamples.Common.FaceProxy;
import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.UiMsg;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_GENEREL;

/**
 * Created by lyt on 2017/11/6.
 */

public class General extends Domain {
    private static General instance = new General();
    public static General getInstance() {
        return instance;
    }

    private DialogServer mDialogServer;

    public General() {
        super(DOMAIN_GENEREL);
        DomainOperation operation = new DefaultOperation();
        setDomainOperation(operation);

        mDialogServer = DialogServer.getInstance();
    }

    public void start(DialogContext context) {
        Domain domain = this;
        Person person = getCurrentPerson();

        //test
        FaceServer mFaceServer = FaceServer.getInstance();
        FaceProxy mFaceProxy = new FaceProxy(mFaceServer);
        FaceAttr faceAttr = mFaceProxy.getFaceAttrSync();
        /*
        if (person.isRegistered()) {  //registered user, say hello
            domain = this;
        } else {  //unregistered user, ask info
            domain = UserConfig.getInstance();
        }
        */
        context.domain = domain;
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void nlu(DialogContext context) {
        //DialogContext context = new DialogContext();

        Domain domain = this;
        Person person = getCurrentPerson();

        /*
        if (person.isRegistered()) {  //registered user, say hello
            domain = this;
        } else {  //unregistered user, ask info
            domain = UserConfig.getInstance();
        }
        */

        context.domain = domain;
        context.person = person;

        context.nlgReqStr = context.nluResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void nlg(DialogContext context) {

        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "复位":
                    mDialogServer.reset();
                    break;
            }
        }

        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        EventBus.getDefault().post(msg);
    }

    public void tts(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

    public class DefaultOperation implements DomainOperation {

        @Override
        public void preprocess(DialogContext context) {

        }

        @Override
        public void process(DialogContext context) {
            switch (context.step) {
                case DIALOG_START:
                    start(context);
                    break;
                case DIALOG_NLU_RES:
                    nlu(context);
                    break;
                case DIALOG_NLG_RES:
                    nlg(context);
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
