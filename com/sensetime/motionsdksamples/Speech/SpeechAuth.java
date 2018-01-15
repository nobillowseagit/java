package com.sensetime.motionsdksamples.Speech;

import android.content.Context;

import com.aispeech.speech.AIAuthEngine;
import com.aispeech.export.listeners.AIAuthListener;
import com.socks.library.KLog;

import java.io.FileNotFoundException;

/**
 * Created by lyt on 2017/10/16.
 */

public class SpeechAuth {
    Context mContext;
    AIAuthEngine mEngine;

    public SpeechAuth(Context context) {
        mContext = context;
    }

    public void runAuth() {
        mEngine = AIAuthEngine.getInstance(mContext);
        try {
            mEngine.init(AppKey.APPKEY, AppKey.SECRETKEY,"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }// TODO 换成您的s/n码

        mEngine.setOnAuthListener(new AIAuthListener() {
            @Override
            public void onAuthSuccess() {
                KLog.trace();
            }

            @Override
            public void onAuthFailed(final String result) {
                KLog.trace();
            }
        });

        boolean authRet = mEngine.doAuth();

        if (mEngine.isAuthed()) {
            //mInfoTv.setText("已授权，您可以自由的使用其它功能");
            KLog.d("aaa");
        } else {
            //mInfoTv.setText("抱歉，您需要授权才能自由使用其它功能");
            KLog.d("bbb");
        }
    }
}

