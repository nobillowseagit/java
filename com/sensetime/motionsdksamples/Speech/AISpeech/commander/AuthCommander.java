package com.sensetime.motionsdksamples.Speech.AISpeech.commander;

import android.content.Context;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAuthInterface;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class AuthCommander implements IAuthInterface{

    IAuthInterface mAuthInterface;

    public AuthCommander(IAuthInterface mAuthInterface)
    {
        this.mAuthInterface = mAuthInterface;
    }

    @Override
    public void InitAuth(Context context,IAISpeechActionsCallback callback) {
        mAuthInterface.InitAuth(context,callback);
    }

    @Override
    public boolean GetAuth() {
        return mAuthInterface.GetAuth();
    }

    @Override
    public void DoAuth() {
        mAuthInterface.DoAuth();
    }

    @Override
    public void DestroyAuth() {
        mAuthInterface.DestroyAuth();
    }
}
