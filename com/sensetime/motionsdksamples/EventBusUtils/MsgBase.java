package com.sensetime.motionsdksamples.EventBusUtils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Utils.UniqueId.getUid;

/**
 * Created by lyt on 2017/10/13.
 */

public class MsgBase {
    public MSG_TYPE type;
    public long mUid;
    public long mSenderId;
    public long mReceiverId;
    public Runnable mRunnable;
    private HandlerThread mHandlerThread;
    private Handler mHandler;


    public enum MSG_TYPE {
        MSG_TYPE_INFO, MSG_TYPE_DIALOG, MSG_TYPE_NLG, MSG_TYPE_FACE, MSG_TYPE_MOTION,
        MSG_TYPE_REMOTE,
        MSG_TYPE_GUESS,
        MSG_TYPE_UI,
        MSG_TYPE_CAMERA
    }

    public MsgBase() {
        mUid = getUid();

        /*
        mHandlerThread = new HandlerThread("msg-handler-thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                checkForUpdate();
                if (isUpdateInfo)
                {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
        */
    }

    public void sendSyncMsg(MsgBase msg){
        EventBus.getDefault().post(msg);
    }

    public void sendAsyncMsg(MsgBase msg){
        EventBus.getDefault().post(msg);
    }

    private void checkForUpdate()
    {
        try
        {
            //模拟耗时
            Thread.sleep(100);
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    //String result = "实时更新中，当前大盘指数：<font color='red'>%d</font>";
                    //result = String.format(result, (int) (Math.random() * 3000 + 1000));
                    //mTvServiceInfo.setText(Html.fromHtml(result));

                }
            });

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}
