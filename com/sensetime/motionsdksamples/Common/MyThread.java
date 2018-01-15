package com.sensetime.motionsdksamples.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lyt on 2017/11/28.
 */

public class MyThread extends Thread {
    private MyHandler mHandler;
    public int mTick;
    public boolean mRunning;

    public MyThread() {
        mTick = 1000;
        mRunning = true;
        mHandler = new MyHandler();
    }

    public void run() {
        while (mRunning) {
            try {
                Thread.sleep(mTick);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("color", "我的");
            msg.setData(b);
            mHandler.sendMessage(msg); // 向Handler发送消息，更新UI
        }
    }


}
