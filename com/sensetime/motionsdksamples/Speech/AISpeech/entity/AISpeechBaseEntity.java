package com.sensetime.motionsdksamples.Speech.AISpeech.entity;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class AISpeechBaseEntity {
    public Actions getAction() {
        return Action;
    }

    public void setAction(Actions action) {
        Action = action;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    Actions Action;
    String Data;
}
