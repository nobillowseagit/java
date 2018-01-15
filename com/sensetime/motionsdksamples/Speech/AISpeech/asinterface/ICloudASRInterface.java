package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import android.content.Context;

import com.aispeech.export.listeners.AIASRListener;

/**
 * Created by xuhao8 on 2017/10/27.
 */

public interface ICloudASRInterface {
    void InitASR(Context context, IAISpeechActionsCallback callback);
    void StartRecording();
    void StopRecording();
    void ASRCancel();
    void ASRDestroy();
}
