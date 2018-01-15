package com.sensetime.motionsdksamples.Speech.AISpeech.proxy;

import android.content.Context;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudSdsEngine;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.listeners.AISdsListener;
import com.aispeech.export.listeners.AITTSListener;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudSdsInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.ICloudTtsInterface;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.AppKey;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.SdsMessageInfo;
import com.sensetime.motionsdksamples.Speech.AISpeech.util.SampleConstants;
import com.socks.library.KLog;

import org.json.JSONException;

/**
 * Created by xuhao8 on 2017/10/30.
 */

public class TtsManagerProxy implements ICloudTtsInterface {
    final String Tag = this.getClass().getName();

    IAISpeechActionsCallback mCallback;
    AICloudTTSEngine mTtsEngine;

    public void InitTts(Context context, IAISpeechActionsCallback callback) {
        mCallback = callback;
        // 创建云端合成播放器
        mTtsEngine = AICloudTTSEngine.createInstance();
        mTtsEngine.setRealBack(true);
        mTtsEngine.init(context, new AITTSListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mTtsEngine.setServer(SampleConstants.SERVER_GRAY);
        // 指定默认中文合成
        mTtsEngine.setLanguage(AIConstant.CN_TTS);

        // 默认女声
        //mTtsEngine.setRes(spinner_res.getSelectedItem().toString());
        mTtsEngine.setRes("syn_chnsnt_zhilingf");

    }

    @Override
    public void StartSpeaking(String string) {
        mTtsEngine.speak(string, "1024");
    }

    @Override
    public void StopSpeaking() {
        mTtsEngine.stop();
    }

    @Override
    public void TtsCancel() {
        if (mTtsEngine != null) {
            //mTtsEngine.cancel();
        }
    }

    @Override
    public void TtsDestroy() {
        if (mTtsEngine != null) {
            mTtsEngine.destroy();
            mTtsEngine = null;
        }
    }

    private class AITTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            Log.i(Tag, "初始化完成，返回值：" + status);
            if (status == AIConstant.OPT_SUCCESS) {
                //tip.setText("初始化成功!");
            } else {
                //tip.setText("初始化失败!code:" + status);
            }
        }


        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
            KLog.trace();
            //showTip("当前:" + currentTime + "ms, 总计:" + totalTime + "ms, 可信度:" + isRefTextTTSFinished);
        }

        @Override
        public void onError(String utteranceId, AIError error) {
            KLog.trace();
            //tip.setText("检测到错误");
            //content.setText(content.getText() + "\nError:\n" + error.toString());
        }

        @Override
        public void onReady(String utteranceId) {
            // TODO Auto-generated method stub
            KLog.trace();
        }

        @Override
        public void onCompletion(String utteranceId) {
            mCallback.SendActionMessage(Actions.TtsResult,true,"");
            //tip.setText("合成完成");
        }

    }

}
