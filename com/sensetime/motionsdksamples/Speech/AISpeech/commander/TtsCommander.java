package com.sensetime.motionsdksamples.Speech.AISpeech.commander;

import android.content.Context;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudTtsInterface;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class TtsCommander implements ICloudTtsInterface {

    ICloudTtsInterface mTtsInterface;

    public TtsCommander(ICloudTtsInterface mTtsInterface)
    {
        this.mTtsInterface = mTtsInterface;
    }

    @Override
    public void InitTts(Context context, IAISpeechActionsCallback callback) {
        mTtsInterface.InitTts(context,callback);
    }

    @Override
    public void StartSpeaking(String string) {
        mTtsInterface.StartSpeaking(string);
    }

    @Override
    public void StopSpeaking() {
        mTtsInterface.StopSpeaking();
    }

    @Override
    public void TtsCancel() {
        mTtsInterface.TtsCancel();
    }

    @Override
    public void TtsDestroy() {
        mTtsInterface.TtsDestroy();
    }
}
