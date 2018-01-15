package com.sensetime.motionsdksamples.Speech.AISpeech.proxy;

import android.content.Context;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IWakeUpInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AppKey;
import com.sensetime.motionsdksamples.Speech.AISpeech.util.SampleConstants;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class WakeUpManagerProxy implements IWakeUpInterface {

    AILocalWakeupDnnEngine mWakeupEngine;
    IAISpeechActionsCallback mCallback;

    @Override
    public void InitWakeUp(Context context, IAISpeechActionsCallback callback) {
        mCallback = callback;
        mWakeupEngine = AILocalWakeupDnnEngine.createInstance(); //创建实例
        mWakeupEngine.setResBin(SampleConstants.RES_WAKEUP); //非自定义唤醒资源可以不用设置words和thresh，资源已经自带唤醒词
        if(context.getExternalCacheDir() != null) {
            mWakeupEngine.setUploadEnable(true);//设置上传音频使能
            mWakeupEngine.setTmpDir(context.getExternalCacheDir().getAbsolutePath());//设置上传的音频保存在本地的目录
        }
        mWakeupEngine.init(context, new AISpeechListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mWakeupEngine.setStopOnWakeupSuccess(true);//设置当检测到唤醒词后自动停止唤醒引擎
    }

    @Override
    public void StartWakeup() {
        mWakeupEngine.start();
    }

    @Override
    public void StopWakeup() {
        mWakeupEngine.stop();
    }

    @Override
    public void WakeUpDestroy() {
        if (mWakeupEngine != null) {
            mWakeupEngine.destroy();
            mWakeupEngine = null;
        }
    }

    private class AISpeechListenerImpl implements AILocalWakeupDnnListener {

        @Override
        public void onError(AIError error) {
            mCallback.SendActionMessage(Actions.WakeupError,true,error.toString());
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                mWakeupEngine.setNetWorkState("WIFI"); //上传音频
                mCallback.SendActionMessage(Actions.WakeupInit,true,status + "");
            } else {
                mCallback.SendActionMessage(Actions.WakeupInit,false,status + "");
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            mCallback.SendActionMessage(Actions.WakeupRmsChange,true,rmsdB + "");
        }

        @Override
        public void onWakeup(String recordId, double confidence, String wakeupWord) {
            mWakeupEngine.start();
            mCallback.SendActionMessage(Actions.WakeupStart,true,"wakeupWord = " + wakeupWord + "  confidence = " + confidence);
        }

        @Override
        public void onReadyForSpeech() {
            mCallback.SendActionMessage(Actions.WakeupReady,true,"");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onRecorderReleased() {
        }

        @Override
        public void onWakeupEngineStopped() {
            // TODO Auto-generated method stub

        }

    }
}
