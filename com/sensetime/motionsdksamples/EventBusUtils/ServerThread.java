package com.sensetime.motionsdksamples.EventBusUtils;

import android.os.Bundle;
import android.os.Message;

import com.sensetime.motionsdksamples.Common.MyTimer;
import com.socks.library.KLog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sensetime.motionsdksamples.Utils.UniqueId.getUid;

/**
 * Created by lyt on 2017/10/12.
 */

public class ServerThread extends Thread implements MyTimer.TimerCallback{
    public long mUid;
    public boolean isRunning = false;
    private boolean mRunnable;
    private long mTick = 100;
    public String mServerName = null;
    private ServerCallback mServerCallback;
    private MyTimer mTimer;

    public EventBus mEventBus;

    public ServerThread(String name) {
        super(name);
        mUid = getUid();
        isRunning = true;
        mServerName = name;
        mRunnable = true;
        mTick = 100;

        mTimer = new MyTimer(1800);
        mTimer.start();

        EventBus.getDefault().register(this);
    }

    public void setServerCallback(ServerCallback callback) {
        mServerCallback = callback;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(MsgBase msg) {
        /* Do something */
        KLog.trace();
    };

    public void setCallback(ServerCallback callback) {
        mServerCallback = callback;
    }

    public void run() {
        while (mRunnable) {
            try {
                Thread.sleep(mTick);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTimeout() {
        mTimer.reset();
    }

    public interface ServerCallback {
        public void onTimeout();
        public void onComplete();
    }

}
