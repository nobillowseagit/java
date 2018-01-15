package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import android.content.Context;

import com.aispeech.export.listeners.AISdsListener;

/**
 * Created by xuhao8 on 2017/10/27.
 */

public interface ICloudSdsInterface {
    void InitSds(Context context, IAISpeechActionsCallback callback);
    void StartRecording();
    void StopRecording();
    void SdsCancel();
    void SdsDestroy();
}
