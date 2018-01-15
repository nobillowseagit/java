package com.sensetime.motionsdksamples.Speech.AISpeech.proxy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aispeech.export.listeners.AIAuthListener;
import com.aispeech.speech.AIAuthEngine;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAuthInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AISpeechBaseEntity;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AppKey;

/**
 * Created by xuhao8 on 2017/10/26.
 */

public class AuthManagerProxy implements IAuthInterface {

    AIAuthEngine mAuthEngine;
    IAISpeechActionsCallback mCallback;

    @Override
    public void InitAuth(Context context, IAISpeechActionsCallback callback) {
        mCallback = callback;
        mAuthEngine = AIAuthEngine.getInstance(context);
        //mEngine.setResStoragePath("/system/vender/aispeech");//设置自定义路径，请将相关文件预先放到该目录下
        try {
            mAuthEngine.init(AppKey.APPKEY, AppKey.SECRETKEY,"0c8c-d47c-049d-2856");
        } catch (Exception e) {
            Log.v("AISpeechLog","onAuthError");
            mCallback.SendActionMessage(Actions.AuthInit,false,"");
            e.printStackTrace();
        }// TODO 换成您的s/n码
        mAuthEngine.setOnAuthListener(new AIAuthListener() {
            @Override
            public void onAuthSuccess() {
                //授权成功回调
                Log.v("AISpeechLog","onAuthSuccess");
                mCallback.SendActionMessage(Actions.AuthInit,true,"");
            }

            @Override
            public void onAuthFailed(final String result) {
                //授权失败回调
                Log.v("AISpeechLog","onAuthFailed");
                mCallback.SendActionMessage(Actions.AuthInit,false,"");
            }
        });
    }

    @Override
    public boolean GetAuth() {
        return mAuthEngine.isAuthed();
    }

    @Override
    public void DoAuth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean authRet = mAuthEngine.doAuth();
                Log.d("AISpeechLog","onAuthResult:" + authRet);
                mCallback.SendActionMessage(Actions.AuthDo,authRet,"");
            }
        }).start();
    }

    @Override
    public void DestroyAuth() {
        if (mAuthEngine != null) {
            mAuthEngine.destroy();
            mAuthEngine = null;
        }
    }
}
