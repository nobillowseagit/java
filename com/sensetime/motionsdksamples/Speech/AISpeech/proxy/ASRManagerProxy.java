package com.sensetime.motionsdksamples.Speech.AISpeech.proxy;

import android.content.Context;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;

import org.json.JSONException;

import java.io.File;

import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudASRInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AISpeechBaseEntity;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AppKey;
import com.sensetime.motionsdksamples.Speech.AISpeech.util.SampleConstants;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class ASRManagerProxy implements ICloudASRInterface {

    AICloudASREngine mASREngine;
    IAISpeechActionsCallback mCallback;

    @Override
    public void InitASR(Context context, IAISpeechActionsCallback callback) {
        mCallback = callback;
        mASREngine = AICloudASREngine.createInstance();
        mASREngine.setVadResource(SampleConstants.RES_VAD);
        mASREngine.setHttpTransferTimeout(10);
        mASREngine.init(context, new AICloudASRListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mASREngine.setNoSpeechTimeOut(0);
        mASREngine.setRes(SampleConstants.RES_AIHOME);
        mASREngine.setUseTxtPost(true);
    }

    @Override
    public void StartRecording() {
        mASREngine.start();
    }

    @Override
    public void StopRecording() {
        mASREngine.stopRecording();
    }

    @Override
    public void ASRCancel() {
        if (mASREngine != null) {
            mASREngine.cancel();
        }
    }

    @Override
    public void ASRDestroy() {
        if (mASREngine != null) {
            mASREngine.destroy();
            mASREngine = null;
        }
    }



    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
            //Speech引擎初始化完成开始录音
            mCallback.SendActionMessage(Actions.ASRReady,true,"");
        }

        @Override
        public void onBeginningOfSpeech() {
            //检测到说话
            mCallback.SendActionMessage(Actions.ASRBeginSpeech,true,"");
        }

        @Override
        public void onEndOfSpeech() {
            //检测到语音停止，开始识别
            mCallback.SendActionMessage(Actions.ASREndSpeech,true,"");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            mCallback.SendActionMessage(Actions.ASRRmsChange,true,String.valueOf(rmsdB));
        }

        @Override
        public void onError(AIError error) {
            mCallback.SendActionMessage(Actions.ASRError,true,error.toString());
        }

        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());
                    String recordId = parser.getRecordId();
                    String response = parser.getRec();
                    if(response == null || response == ""){
                        mCallback.SendActionMessage(Actions.ASRResult,false,"");
                    } else {
                        mCallback.SendActionMessage(Actions.ASRResult,true,response);
                    }
                }
            }
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                //初始化成功
                mCallback.SendActionMessage(Actions.ASRInit,true,"");
            } else {
                //初始化失败
                mCallback.SendActionMessage(Actions.ASRInit,false,"");
            }
        }

        @Override
        public void onRecorderReleased() {
            //检测到录音机停止

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNotOneShot() {
            // TODO Auto-generated method stub

        }

    }
}
