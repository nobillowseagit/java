package com.sensetime.motionsdksamples.Motion;

import com.sensetime.motionsdksamples.EventBusUtils.ClientThread;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_MOTION;
import static com.sensetime.motionsdksamples.Motion.MotionMsg.MOTION_CMD.MOTION_CMD_FACE;

/**
 * Created by lyt on 2017/10/13.
 */

public class MotionClient extends ClientThread {
    public MotionClient(String name) {
        super("motion-client");
    }

    @Override
    public void run() {
        KLog.trace();
        //执行耗时操作
        while (isRunning) {
            //count();
            /*
            MotionMsg msg = new MotionMsg();
            Rectangle face = new Rectangle(30, 30, 100, 100);
            Rectangle scene = new Rectangle(0, 0, 1000, 1000);
            msg.mRecFace = face;
            msg.mRecScene = scene;
            msg.type = MSG_TYPE_MOTION;
            msg.cmd = MOTION_CMD_FACE;
            EventBus.getDefault().post(msg);
            */
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //EventBus.getDefault().post(new MessageEvent());
    }
}
