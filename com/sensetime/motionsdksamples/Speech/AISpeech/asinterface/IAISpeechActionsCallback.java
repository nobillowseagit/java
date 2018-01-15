package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AISpeechBaseEntity;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public interface IAISpeechActionsCallback {
    void SendActionMessage(Actions action,boolean isSuccess,String data);
}
