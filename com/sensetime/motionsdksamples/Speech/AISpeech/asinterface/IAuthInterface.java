package com.sensetime.motionsdksamples.Speech.AISpeech.asinterface;

import android.content.Context;

/**
 * Created by xuhao8 on 2017/10/26.
 */

public interface IAuthInterface {
    void InitAuth(Context context,IAISpeechActionsCallback callback);
    boolean GetAuth();
    void DoAuth();
    void DestroyAuth();
}
