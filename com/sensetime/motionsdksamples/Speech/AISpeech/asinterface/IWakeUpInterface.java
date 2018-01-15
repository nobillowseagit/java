package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import android.content.Context;

/**
 * Created by xuhao8 on 2017/10/27.
 */

public interface IWakeUpInterface {
    void InitWakeUp(Context context, IAISpeechActionsCallback callback);
    void StartWakeup();
    void StopWakeup();
    void WakeUpDestroy();
}
