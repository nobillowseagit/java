package com.sensetime.motionsdksamples.Dialog;

import android.os.Handler;
import android.os.HandlerThread;

import com.sensetime.motionsdksamples.Common.MyTimer;

import java.util.List;

/**
 * Created by lyt on 2017/11/23.
 */

public class DialogBase extends Domain implements MyTimer.TimerCallback{
    private DialogManager mDialogManager;
    private MyTimer mTimer;
    private final DialogBaseTimeoutCallback mTimeoutCallback;
    private Handler mDialogHandler;
    public List<String> mNluResKeyWords;
    public List<String> mNlgResKeyWords;

    public DialogBase(DOMAIN_TPYE type) {
        super(type);

        DomainOperation operation = new DefaultOperation();
        setDomainOperation(operation);

        mDialogManager = DialogManager.getInstance();
        mDialogManager.registerDialog(this);

        mTimer = new MyTimer(300);
        mTimeoutCallback = new DialogBaseTimeoutCallback();
        mTimer.setCallback(mTimeoutCallback);
    }

    public void start(DialogContext context) {
        mTimer.start();
    }

    public void nluRes(DialogContext context) {
        mTimer.start();
    };

    public void nlgRes(DialogContext context) {
        mTimer.start();
    };

    public void ttsRes(DialogContext context){
        mTimer.start();
    };

    @Override
    public void onTimeout() {
        mTimer.stop();
    }

    public class DefaultOperation implements DomainOperation {
        @Override
        public void preprocess(DialogContext context) {

        }

        @Override
        public void process(DialogContext context) {
            switch (context.step) {
                case DIALOG_START:
                    start(context);
                    break;
                case DIALOG_NLU_RES:
                    nluRes(context);
                    break;
                case DIALOG_NLG_RES:
                    nlgRes(context);
                    break;
                case DIALOG_TTS_RES:
                    ttsRes(context);
                    break;
            }
        }

        @Override
        public void postprocess(DialogContext context) {

        }
    }

    public interface DialogCallback {
        public void onDialogChanged();
        public void onDialogError();
    }

    public class DialogThread extends HandlerThread {

        public DialogThread(String name) {
            super(name);
        }

    }

    public class DialogHandler extends Handler {

    }

    public class DialogBaseTimeoutCallback implements MyTimer.TimerCallback {
        @Override
        public void onTimeout() {
            mTimer.reset();
        }
    }
}
