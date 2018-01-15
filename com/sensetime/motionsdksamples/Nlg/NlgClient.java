package com.sensetime.motionsdksamples.Nlg;

import com.sensetime.motionsdksamples.Common.PersonDb;
import com.sensetime.motionsdksamples.EventBusUtils.ClientThread;
import com.sensetime.motionsdksamples.Common.Person;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lyt on 2017/10/13.
 */

public class NlgClient extends ClientThread {
    PersonDb mPersonDb;
    public NlgClient(PersonDb personDb) {
        super("person-client");
        mPersonDb = personDb;
    }

    @Override
    public void run() {
        KLog.trace();
        //执行耗时操作
        while (isRunning) {
            //count();
        }
    }

    public void sendAsyncMsg(Person person) {
        NlgMsg msg = new NlgMsg();
        msg.mSenderId = mUid;
        //msg.mPerson = person;
        EventBus.getDefault().post(msg);
    }

    public void sendSyncMsg(Person person) {
        NlgMsg msg = new NlgMsg();
        msg.mSenderId = mUid;
        //msg.mPerson = person;
        EventBus.getDefault().post(msg);

        try {
            EventBus.getDefault().wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
