package com.sensetime.motionsdksamples.Speech.AISpeech.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AISdsListener;

import org.json.JSONException;

import java.io.Console;
import java.io.File;

import com.sensetime.motionsdksamples.Speech.AISpeech.commander.ASRCommander;
import com.sensetime.motionsdksamples.Speech.AISpeech.commander.AuthCommander;
import com.sensetime.motionsdksamples.Speech.AISpeech.commander.SdsCommander;
import com.sensetime.motionsdksamples.Speech.AISpeech.commander.TtsCommander;
import com.sensetime.motionsdksamples.Speech.AISpeech.commander.WakeUpCommander;
import com.sensetime.motionsdksamples.Speech.AISpeech.proxy.ASRManagerProxy;
import com.sensetime.motionsdksamples.Speech.AISpeech.proxy.AuthManagerProxy;
import com.sensetime.motionsdksamples.Speech.AISpeech.proxy.SdsManagerProxy;
import com.sensetime.motionsdksamples.Speech.AISpeech.proxy.TtsManagerProxy;
import com.sensetime.motionsdksamples.Speech.AISpeech.proxy.WakeUpManagerProxy;

/**
 * Created by xuhao8 on 2017/10/26.
 */

public class AISpeechService extends Service{

    AuthCommander authCommander;
    ASRCommander asrCommander;
    SdsCommander sdsCommander;
    TtsCommander ttsCommander;
    WakeUpCommander wakeUpCommander;
    AISpeechBinder aiSpeechBinder = new AISpeechBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return aiSpeechBinder;
    }

    public AuthCommander GetAuthCommander()
    {
        if(authCommander == null) {
            authCommander = new AuthCommander(new AuthManagerProxy());
        }
        return authCommander;
    }

    public ASRCommander GetASRCommander()
    {
        if(asrCommander == null) {
            asrCommander = new ASRCommander(new ASRManagerProxy());
        }
        return asrCommander;
    }

    public SdsCommander GetSdsCommander()
    {
        if(sdsCommander == null) {
            sdsCommander = new SdsCommander(new SdsManagerProxy());
        }
        return sdsCommander;
    }

    public TtsCommander GetTtsCommander()
    {
        if(ttsCommander == null) {
            ttsCommander = new TtsCommander(new TtsManagerProxy());
        }
        return ttsCommander;
    }
	
    public WakeUpCommander GetWakeUpCommander()
    {
        if(wakeUpCommander == null) {
            wakeUpCommander = new WakeUpCommander(new WakeUpManagerProxy());
        }
        return wakeUpCommander;
    }


    public class AISpeechBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public AISpeechService getService(){
            return AISpeechService.this;
        }
    }
}
