package com.sensetime.motionsdksamples.Sound;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * Created by lyt on 2017/11/12.
 */

public class SoundServer {
    private static final int MSG_TIME_OUT = 1;

    static {
        //System.loadLibrary("sai_preprocess");
        System.loadLibrary("native-lib");
    }

    private Handler mTimerHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public native int initWakeup();

    public native int getStatus();

    public native float getAngle();

    private Thread mThread;
    private Handler mHandler;


    public SoundServer() {

    }

    private static SoundServer instance = new SoundServer();

    public static SoundServer getInstance() {
        return instance;
    }

    public void init() {
        //int ret = initWakeup();
        //int status = getStatus();
        //float angle = getAngle();

        initTimer();
        startTimer();
    }

    private void initTimer() {
        mTimerHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_TIME_OUT:
                        //int status = getStatus();
                        int a = 0;
                        //mCurrentDialogContext.setIdle(true);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        mTimer = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message( );
                message.what = MSG_TIME_OUT;
                mTimerHandler.sendMessage(message);
            }
        };
    }

    private void startTimer() {
        mTimer.schedule(mTimerTask, 0, 100);
    }

    private void stopTimer() {
        if (null != mTimerTask) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (null != mTimer) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void resetTimer() {
        mTimer.purge();
    }

}
