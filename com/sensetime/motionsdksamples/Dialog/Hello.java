package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.Person;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_HELLO;

/**
 * Created by lyt on 2017/11/6.
 */

public class Hello extends DialogBase {
    private static Hello instance = new Hello();
    public static Hello getInstance() {
        return instance;
    }

    public Hello() {
        super(DOMAIN_HELLO);
    }

    @Override
    public void start(DialogContext context) {

    }

    @Override
    public void nluRes(DialogContext context) {
        Domain domain = this;
        Person person = getCurrentPerson();
        if (person.isRegistered()) {  //registered user, say hello
            domain = this;
        } else {  //unregistered user, ask info
            domain = UserConfig.getInstance();
        }
        context.domain = domain;
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void nlgRes(DialogContext context) {
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

    @Override
    public void ttsRes(DialogContext context) {
        //DialogContext context = new DialogContext();
        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

}
