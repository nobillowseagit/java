package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.InfoServer;
import com.sensetime.motionsdksamples.Common.Person;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_USER;

/**
 * Created by lyt on 2017/11/6.
 */

public class UserConfig extends Domain {
    private static UserConfig instance = new UserConfig();
    public static UserConfig getInstance() {
        return instance;
    }

    public static final int USER_DOMAIN_SET_NAME = 1;
    public static final int USER_DOMAIN_SET_GENDER = 1;
    public static final int USER_DOMAIN_SET_AGE = 1;

    private InfoServer mInfoServer;
    private FaceServer mFaceServer;

    public UserConfig() {
        super(DOMAIN_USER);
        DomainOperation operation = new UserConfigOperation();
        setDomainOperation(operation);
    }

    private void start(DialogContext context) {
    }

    public void nlu(DialogContext context) {
        //DialogContext context = new DialogContext();
        context.domain = this;
        context.person = getCurrentPerson();
        context.nlgReqStr = context.nluResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void nlg(DialogContext context) {
        //DialogContext context = new DialogContext();
        int index;
        String name_string = null;
        String tts_req_string = null;
        Person person = null;

        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "设置姓名":
                    if (null != context.nluResStr) {
                        index = context.nluResStr.indexOf("我叫");
                        if (index >= 0) {
                            name_string = context.nluResStr.substring(index + 2);
                            person = getCurrentPerson();
                            person.mName = name_string;
                            person.mRegistered = 1;
                        }

                        index = context.nluResStr.indexOf("叫我");
                        if (index >= 0) {
                            name_string = context.nluResStr.substring(index + 2);
                            person = getCurrentPerson();
                            person.mName = name_string;
                            person.mRegistered = 1;
                        }
                        mFaceServer = FaceServer.getInstance();
                        mFaceServer.setCurrentPerson(person);

                        mInfoServer = InfoServer.getInstance();
                        mInfoServer.updatePerson(person);
                    }
                    if (null != name_string) {
                        tts_req_string = name_string + context.nlgResStr;
                        context.ttsReqStr = tts_req_string;
                    } else {
                        context.ttsReqStr = context.nlgResStr;
                    }
                    break;
                case "设置性别":
                    if (null != context.nlgActParamStr) {
                        person = context.person;
                        if (1 == person.mRegistered) {
                            person.mGender = context.nlgActParamStr;

                            mFaceServer = FaceServer.getInstance();
                            mFaceServer.setCurrentPerson(person);

                            mInfoServer = InfoServer.getInstance();
                            mInfoServer.updatePerson(person);                        }
                    }
                    context.ttsReqStr = context.nlgResStr;
                    break;
                case "设置年龄":
                    context.ttsReqStr = context.nlgResStr;
                    break;
                case "设置用户信息":
                    context.ttsReqStr = context.nlgResStr;
                    break;
            }
        } else {
            context.ttsReqStr = context.nlgResStr;
        }

        //context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void tts(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void finish() {

    }

    public class UserConfigOperation implements DomainOperation {

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
