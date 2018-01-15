package com.sensetime.motionsdksamples.Info;

import com.sensetime.motionsdksamples.Common.PersonDb;
import com.sensetime.motionsdksamples.EventBusUtils.ClientThread;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lyt on 2017/10/13.
 */

public class InfoClient extends ClientThread {
    PersonDb mPersonDb;

    public InfoClient(PersonDb personDb) {
        super("person-client");
        mPersonDb = personDb;
    }

    @Override
    public void run() {
        KLog.trace();
        InfoMsg msg = new InfoMsg();
        //执行耗时操作
        while (isRunning) {
            //count();
        }
    }

    public void sendAsyncMsg(InfoMsg msg) {
        EventBus.getDefault().post(msg);
    }

    public void sendSyncMsg(InfoMsg msg) {
        EventBus.getDefault().post(msg);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(MsgBase msgBase) {

    }


}