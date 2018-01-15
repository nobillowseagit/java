package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyt on 2017/10/30.
 */

public class DialogContext {
    public enum DialogStep {
        DIALOG_IDLE,
        DIALOG_START,
        DIALOG_NLU_REQ, DIALOG_NLU_RES,
        DIALOG_NLG_REQ, DIALOG_NLG_RES,
        DIALOG_TTS_REQ, DIALOG_TTS_RES
    }

    public Domain domain;
    public Person person;
    public String nluReqStr;
    public String nluResStr;
    public String nluResDomain;
    public String nluResNlg;
    public String nlgReqStr;
    public String nlgResStr;
    public String nlgActCmdStr;
    public String nlgActParamStr;
    public String ttsReqStr;
    public DialogStep step;
    public boolean idle;
    public DialogSession session;

    public synchronized void setStep(DialogStep step) {
        this.step = step;
    }

    public synchronized DialogStep getStep() {
        return this.step;
    }

    public synchronized void setIdle(boolean b) {
        idle = b;
    }

    public synchronized void setPerson(Person person) {
        this.person = person;
    }

    public synchronized Person getPerson() {
        return this.person;
    }

    public synchronized Domain getDomain() {
        return this.domain;
    }

    public synchronized void setDomain(Domain domain) {
        this.domain = domain;
    }

    public synchronized void clear() {
    }

    public synchronized boolean isDialogIdle() {
        if (idle) {
            return true;
        }
        return false;
    }
}
