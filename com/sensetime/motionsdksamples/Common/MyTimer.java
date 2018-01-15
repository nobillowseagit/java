package com.sensetime.motionsdksamples.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lyt on 2017/11/22.
 */

public class MyTimer {
    public TimerHandler mTimerHandler;
    public TimerThread mTimerThread;
    private int mTick = 100;
    private int mCount;
    private int mCountMask;
    private TimerCallback mCallback;
    private boolean mStarted;

    public MyTimer(int mCountMask) {
        init(mCountMask);
        mTimerThread = new TimerThread();
        mTimerThread.start();
        mTimerHandler = new TimerHandler();
    }

    public void init(int countMask) {
        this.mTick = 100;
        this.mCount = 0;
        this.mCountMask = countMask;
        this.mStarted = false;
    }

    public void setCallback(TimerCallback callback) {
        mCallback = callback;
    }

    public synchronized void start() {
        clearCount();
        mStarted = true;
    }

    public synchronized void pause() {
        mStarted = false;
    }

    public synchronized void stop() {
        mStarted = false;
        clearCount();
    }

    public void reset() {
        clearCount();
    }



    //internal
    private synchronized void clearCount() {
        mCount = 0;
    }

    private synchronized void incCount() {
        mCount++;
    }



    public class TimerHandler extends Handler {
        public TimerHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (null != mCallback) {
                mCallback.onTimeout();
            }
        }
    }

    public class TimerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(mTick);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (mStarted) {
                    incCount();
                    if (mCount >= mCountMask) {
                        clearCount();
                        Message msg = Message.obtain();
                        Bundle b = new Bundle();// 存放数据
                        b.putString("color", "我的");
                        msg.setData(b);
                        mTimerHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                    }
                }
            }
        }
    }

    public interface TimerCallback {
        public void onTimeout();
    }
}
