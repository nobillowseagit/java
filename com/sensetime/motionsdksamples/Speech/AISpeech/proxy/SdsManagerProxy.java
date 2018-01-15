package com.sensetime.motionsdksamples.Speech.AISpeech.proxy;

import android.content.Context;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudSdsEngine;
import com.aispeech.export.listeners.AISdsListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudSdsInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AppKey;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.SdsMessageInfo;
import com.sensetime.motionsdksamples.Speech.AISpeech.util.SampleConstants;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class SdsManagerProxy implements ICloudSdsInterface{

    AICloudSdsEngine mSdsEngine;
    IAISpeechActionsCallback mCallback;

    @Override
    public void InitSds(Context context, IAISpeechActionsCallback callback) {
        mCallback = callback;
        mSdsEngine = AICloudSdsEngine.createInstance();
        mSdsEngine.setRes(SampleConstants.RES_AIHOME);
        mSdsEngine.setServer(SampleConstants.SERVER_PRO);
        mSdsEngine.setVadResource(SampleConstants.RES_VAD);
//      mSdsEngine.setUserId("AISPEECH"); //填公司名字
        mSdsEngine.init(context, new AISdsListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
    }

    @Override
    public void StartRecording() {
        mSdsEngine.startWithRecording();
    }

    @Override
    public void StopRecording() {
        mSdsEngine.stopRecording();
    }

    @Override
    public void SdsCancel() {
        if (mSdsEngine != null) {
            mSdsEngine.cancel();
        }
    }

    @Override
    public void SdsDestroy() {
        if (mSdsEngine != null) {
            mSdsEngine.destroy();
            mSdsEngine = null;
        }
    }

    private class AISdsListenerImpl implements AISdsListener {

        @Override
        public void onReadyForSpeech() {
            //Sds引擎准备完成开始录音
            mCallback.SendActionMessage(Actions.SdsReady,true,"");
        }

        @Override
        public void onBeginningOfSpeech() {
            //检测到说话
            mCallback.SendActionMessage(Actions.SdsBeginSpeech,true,"");
        }

        @Override
        public void onEndOfSpeech() {
            //检测到语音停止，开始识别
            mCallback.SendActionMessage(Actions.SdsEndSpeech,true,"");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            mCallback.SendActionMessage(Actions.SdsRmsChange,true,String.valueOf(rmsdB));
        }

        @Override
        public void onError(AIError error) {
            mCallback.SendActionMessage(Actions.SdsError,true,error.toString());
        }

        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {

                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());
                    SdsMessageInfo sdsMessageInfo = new SdsMessageInfo();
                    try {
                        //String result = parser.getResult().getString("sds");
                        //String response = parser.getJSON().toString(4);
                        //mCallback.SendActionMessage(Actions.SdsResult,true,response);

                        JSONObject result = (JSONObject) parser.getResult().get("sds");

                        //sdsMessageInfo.setNLG(result.getString("output"));
                        //if (null != result.getString("output")) {
                        //    sdsMessageInfo.setNLG(result.getString("output"));
                        //}

                        //sdsMessageInfo.setRecordId(parser.getRecordId());
                        if (null != parser.getRecordId()) {
                            sdsMessageInfo.setRecordId(parser.getRecordId());
                        }

                        //sdsMessageInfo.setInput(parser.getResult().getString("input"));
                        if (null != parser.getResult().getString("input")) {
                            sdsMessageInfo.setInput(parser.getResult().getString("input"));
                        }

                        if(parser.getSemantics() != null) {
                            JSONObject semantics = (JSONObject) parser.getSemantics().get("request");
                            sdsMessageInfo.setSlotCount(semantics.getInt("slotcount"));
                            //sdsMessageInfo.setAction(semantics.getString("action"));
                            sdsMessageInfo.setDomain(semantics.getString("domain"));
                        }

                        String jsonStr = JSON.toJSONString(sdsMessageInfo);

                        //mCallback.SendActionMessage(Actions.SdsResult,true, new Gson().toJson(sdsMessageInfo));
                        mCallback.SendActionMessage(Actions.SdsResult,true, jsonStr);


                    } catch (JSONException e) {
                        mCallback.SendActionMessage(Actions.SdsResult,false,"");
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                //初始化成功
                mCallback.SendActionMessage(Actions.SdsInit,true,"");
            } else {
                //初始化失败
                mCallback.SendActionMessage(Actions.SdsInit,false,"");
            }
        }

        @Override
        public void onRecorderReleased() {
            // TODO Auto-generated method stub

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
