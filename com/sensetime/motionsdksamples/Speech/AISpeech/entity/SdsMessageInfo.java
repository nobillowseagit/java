package com.sensetime.motionsdksamples.Speech.AISpeech.entity;

/**
 * Created by xuhao8 on 2017/11/10.
 */

public class SdsMessageInfo {
    public String getRecordId() {
        return RecordId;
    }

    public void setRecordId(String recordId) {
        RecordId = recordId;
    }

    public String getInput() {
        return Input;
    }

    public void setInput(String input) {
        Input = input;
    }

    public int getSlotCount() {
        return SlotCount;
    }

    public void setSlotCount(int slotCount) {
        SlotCount = slotCount;
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getNLG() {
        return NLG;
    }

    public void setNLG(String NLG) {
        this.NLG = NLG;
    }

    String RecordId;
    String Input;
    int SlotCount;
    String Domain;
    String Action;
    String NLG;
}
