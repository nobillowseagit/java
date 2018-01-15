package com.sensetime.motionsdksamples.Speech.AISpeech.commander;

import android.content.Context;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IWakeUpInterface;

/**
 * Created by xuhao8 on 2017/11/15.
 */

public class WakeUpCommander{

    IWakeUpInterface mWakeupInterface;

    public WakeUpCommander(IWakeUpInterface mWakeupInterface)
    {
        this.mWakeupInterface = mWakeupInterface;
    }

    public void InitWakeUp(Context context, IAISpeechActionsCallback callback) {
        mWakeupInterface.InitWakeUp(context,callback);
    }

    public void StartWakeup() {
        mWakeupInterface.StartWakeup();
    }

    public void StopWakeup() {
        mWakeupInterface.StopWakeup();
    }

    public void WakeUpDestroy() {
        mWakeupInterface.WakeUpDestroy();
    }
}
