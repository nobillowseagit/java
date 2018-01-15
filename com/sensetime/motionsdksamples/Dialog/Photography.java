package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Photography.Photo;

import org.greenrobot.eventbus.EventBus;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_PHOTOGRAPHY;

/**
 * Created by lyt on 2017/11/6.
 */

public class Photography extends Domain {
    private static Photography instance = new Photography();
    public static Photography getInstance() {
        return instance;
    }

    private DialogServer mDialogServer;
    private Photo mPhoto;
    private PhotoDialogCallback mPhotoDialogCallback;

    public Photography() {
        super(DOMAIN_PHOTOGRAPHY);
        DomainOperation operation = new PhotographyOperation();
        setDomainOperation(operation);
        mDialogServer = DialogServer.getInstance();

        mPhotoDialogCallback = new PhotoDialogCallback();
        mPhoto = Photo.getInstance();
        mPhoto.setPhotoCompletedCallback(mPhotoDialogCallback);
    }

    public void start(DialogContext context) {
        context.domain = this;
        context.person = getCurrentPerson();
        context.nlgReqStr = context.nluResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void ok(DialogContext context) {
        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

    public void tts(DialogContext context) {
        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "全景拍照":
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mDialogServer.enterPauseSync();
                    mPhoto.startFull();
                    break;
            }
        }

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public class PhotographyOperation implements DomainOperation {

        @Override
        public void preprocess(DialogContext context) {

        }

        @Override
        public void process(DialogContext context) {
            switch (context.step) {
                case DIALOG_NLU_RES:
                    start(context);
                    break;
                case DIALOG_NLG_RES:
                    ok(context);
                    break;
                case DIALOG_TTS_RES:
                    tts(context);
                    break;
            }
        }

        @Override
        public void postprocess(DialogContext context) {

        }
    }

    public class PhotoDialogCallback implements Photo.PhotoCompletedCallback {
        @Override
        public void onPhotoCompleted() {
            mDialogServer.exitPauseSync();
        }
    }
}
