package com.sensetime.motionsdksamples.Speech.AISpeech.commander;

import android.content.Context;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.listeners.AISdsListener;

import org.json.JSONException;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudSdsInterface;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class SdsCommander implements ICloudSdsInterface{

    ICloudSdsInterface mSdsInterface;

    public SdsCommander(ICloudSdsInterface mSdsInterface)
    {
        this.mSdsInterface = mSdsInterface;
    }

    @Override
    public void InitSds(Context context, IAISpeechActionsCallback callback) {
        mSdsInterface.InitSds(context,callback);
    }

    @Override
    public void StartRecording() {
        mSdsInterface.StartRecording();
    }

    @Override
    public void StopRecording() {
        mSdsInterface.StopRecording();
    }

    @Override
    public void SdsCancel() {
        mSdsInterface.SdsCancel();
    }

    @Override
    public void SdsDestroy() {
        mSdsInterface.SdsDestroy();
    }
}
