package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import android.content.Context;

/**
 * Created by xuhao8 on 2017/10/27.
 */

public interface ICloudTtsInterface {
    void InitTts(Context context, IAISpeechActionsCallback callback);
    void StartSpeaking(String string);
    void StopSpeaking();
    void TtsCancel();
    void TtsDestroy();
}
