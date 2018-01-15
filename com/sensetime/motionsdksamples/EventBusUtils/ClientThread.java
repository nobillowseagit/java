package com.sensetime.motionsdksamples.EventBusUtils;

import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sensetime.motionsdksamples.Utils.UniqueId.getUid;

/**
 * Created by lyt on 2017/10/12.
 */

public class ClientThread extends Thread {
    public long mUid;
    public boolean isRunning = false;
    //public static class MessageEvent { /* Additional fields if needed */ }

    public ClientThread(String name) {
        super(name);
        mUid = getUid();
        isRunning = true;
    }

    @Override
    public void run() {
        KLog.trace();
        //执行耗时操作
        while (isRunning) {
            //count();
            MsgBase msg = new MsgBase();
            //msg.mUid = 10;
            EventBus.getDefault().post(msg);
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //EventBus.getDefault().post(new MsgBase());
    }

    public void sendSyncMsg(MsgBase msg) {

    }

    public void sendAsyncMsg(MsgBase msg) {

    }
}
