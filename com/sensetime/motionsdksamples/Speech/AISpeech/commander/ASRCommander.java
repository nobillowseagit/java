package com.sensetime.motionsdksamples.Speech.AISpeech.commander;

import android.content.Context;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.listeners.AIASRListener;

import java.io.File;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudASRInterface;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class ASRCommander implements ICloudASRInterface{

    ICloudASRInterface mASRInterface;

    public ASRCommander(ICloudASRInterface mASRInterface)
    {
        this.mASRInterface = mASRInterface;
    }

    @Override
    public void InitASR(Context context, IAISpeechActionsCallback callback) {
        mASRInterface.InitASR(context,callback);
    }

    @Override
    public void StartRecording() {
        mASRInterface.StartRecording();
    }

    @Override
    public void StopRecording() {
        mASRInterface.StopRecording();
    }

    @Override
    public void ASRCancel() {
        mASRInterface.ASRCancel();
    }

    @Override
    public void ASRDestroy() {
        mASRInterface.ASRDestroy();
    }
}
