package com.sensetime.motionsdksamples.Dialog;

import android.os.Looper;

import com.sensetime.motionsdksamples.Common.InfoServer;
import com.sensetime.motionsdksamples.Common.MyTimer;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Nlg.NlgMsg;
import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.UiMsg;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;

import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_IDLE;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_NLG_RES;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_NLU_RES;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogContext.DialogStep.DIALOG_TTS_RES;
import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_DIALOG;
import static com.sensetime.motionsdksamples.Nlg.NlgMsg.NLG_CMD.NLG_CMD_REQ;

/**
 * Created by lyt on 2017/10/16.
 */

public class DialogServer extends ServerThread implements IDialog{
    private static final int TIMEOUT_VAL = 150;

    private static DialogServer instance = new DialogServer();
    public static DialogServer getInstance() {
        return instance;
    }

    private DialogContext mCurrentDialogContext = null;
    private DialogFsm mDialogFsm = null;
    private DialogSession mSession = null;
    private DialogHandler mDialogHandler;
    private List<Domain> mListDomain;

    private boolean mPause = false;

    private SpeechServer mSpeechServer;

    MyTimer mTimer;
    DialogTimeoutCallback mDialogTimeoutCallback;

    public DialogServer() {
        super("person-server");
        DialogContext context = new DialogContext();
        context.setPerson(new Person());
        context.setDomain(General.getInstance());
        context.setIdle(true);
        setCurrentContext(context);

        //mDialogFsm = new DialogFsm();
        //mSession = new DialogSession();

        mSpeechServer = SpeechServer.getInstance();

        mTimer = new MyTimer(TIMEOUT_VAL);
        mDialogTimeoutCallback = new DialogTimeoutCallback();
        mTimer.setCallback(mDialogTimeoutCallback);
        //mTimer.start();
    }

    public synchronized Person getCurrentPerson() {
        FaceServer faceServer = FaceServer.getInstance();
        return faceServer.getCurrentPerson();
    }

    @Override
    public void run() {
        //Looper.prepare();
        mDialogHandler = new DialogHandler(Looper.myLooper());
        //Looper.loop();
    }

    public void registerDomain(Domain domain) {
        mListDomain.add(domain);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MsgBase msgBase) {
        KLog.trace();
        if (msgBase.type != MSG_TYPE_DIALOG) {
            return;
        }

        DialogMsg msg = (DialogMsg)msgBase;
        DialogContext context = msg.context;
        if (null == context) {
            context = getCurrentContext();
        }

        switch (msg.cmd) {
            case DIALOG_CMD_IDLE:
                handleIdle();
                break;
            case DIALOG_CMD_SOUND_TRIGGER:
                handleSoundTrigger();
                break;
            case DIALOG_CMD_VISION_TRIGGER:
                handleVisionTrigger();
                break;
            case DIALOG_CMD_NLU_REQ:
                handleNluReq(context);
                break;
            case DIALOG_CMD_NLU_RES:
                handleNluRes(context);
                break;
            case DIALOG_CMD_NLG_REQ:
                handleNlgReq(context);
                break;
            case DIALOG_CMD_NLG_RES:
                handleNlgRes(context);
                break;
            case DIALOG_CMD_TTS_REQ:
                handleTtsReq(context);
                break;
            case DIALOG_CMD_TTS_RES:
                handleTtsRes(context);
                break;
        }
    }

    private synchronized void clearCurrentContext() {

    }

    private synchronized void handleSoundTrigger() {
        if (isPaused()) {
            return;
        }

        mSpeechServer.stopNlu();
        mSpeechServer.stopTts();

        mTimer.reset();
        mTimer.start();

        clearCurrentContext();
        DialogContext context = getCurrentContext();
        context.setDomain(General.getInstance());
        context.setStep(DIALOG_NLU_RES);
        context.setIdle(false);
        context.nluResStr = "声音触发";
        setCurrentContext(context);
        context.domain.operation.process(context);
    }

    private synchronized void handleVisionTrigger() {
        if (isPaused()) {
            return;
        }

        DialogContext context = getCurrentContext();
        if (context.isDialogIdle()) {

            mSpeechServer.stopNlu();
            mSpeechServer.stopTts();

            UiMsg uiMsg = new UiMsg();
            uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_VISION_TRIGGER;
            EventBus.getDefault().post(uiMsg);

            context.setDomain(General.getInstance());
            context.setStep(DIALOG_NLU_RES);
            context.setIdle(false);
            context.nluResStr = "视觉触发";
            setCurrentContext(context);
            context.domain.operation.process(context);
        }
    }

    private synchronized void handleNluReq(DialogContext context) {
        //mTimer.reset();
        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "listenning";
        EventBus.getDefault().post(uiMsg);

        context.setStep(DIALOG_NLU_REQ);
        context.setIdle(false);
        setCurrentContext(context);
        mSpeechServer.reqNlu();
    }

    private synchronized void handleNluRes(DialogContext context) {
        //mTimer.reset();

        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "listened";
        EventBus.getDefault().post(uiMsg);

        //mCurrentDialogContext.nluResStr = context.nluResStr;
        //context = getCurrentContext();
        context = switchContext(context);

        if (context.nluResStr.equals("") || context.nluResStr.equals("无效")) {
            handleNluReq(context);
            return;
        }

        if (null != context.nluResDomain) {
            context.ttsReqStr = context.nluResNlg;
            handleTtsReq(context);
            return;
        }

        if (null == context.domain) {
            context.domain = mCurrentDialogContext.domain;
        }
        context.setStep(DIALOG_NLU_RES);
        context.setIdle(false);
        setCurrentContext(context);
        context.domain.operation.process(context);
    }

    private synchronized void handleNlgReq(DialogContext context) {
        //mTimer.reset();
        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "thinking";
        EventBus.getDefault().post(uiMsg);

        if (context.nluResStr.equals("")) {
            //Domain domain = General.getInstance();
            //context.setDomain(domain);
            handleNluReq(context);
            return;
        }

        context.nlgReqStr = context.nluResStr;

        context.setStep(DIALOG_NLG_REQ);
        context.setIdle(false);
        setCurrentContext(context);

        NlgMsg msg = new NlgMsg();
        msg.cmd = NLG_CMD_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    private synchronized boolean handleNlgResPre(DialogContext context) {
        if (null != context.nlgActCmdStr) {
            if (context.nlgActCmdStr.equals("无效")) {
                return true;
            }
        }
        return false;
    }

    private synchronized void handleNlgRes(DialogContext context) {
        //mTimer.reset();
        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "thinked";
        EventBus.getDefault().post(uiMsg);

        if (handleNlgResPre(context)) {
            handleNluReq(context);
            return;
        }

        if (null == context.nluReqStr) {
            context.nluReqStr = mCurrentDialogContext.nluReqStr;
        }
        if (null == context.nluResStr) {
            context.nluResStr = mCurrentDialogContext.nluResStr;
        }
        if (null == context.nlgReqStr) {
            context.nlgReqStr = mCurrentDialogContext.nlgReqStr;
        }
        if (null == context.person) {
            context.person = mCurrentDialogContext.person;
        }

        context.setStep(DIALOG_NLG_RES);
        context.setIdle(false);
        setCurrentContext(context);
        context.domain.operation.process(context);
    }

    private synchronized void handleTtsReq(DialogContext context) {
        mTimer.reset();
        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "speaking";
        EventBus.getDefault().post(uiMsg);

        //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
        //uiMsg.emotion = 3; //speak
        //EventBus.getDefault().post(uiMsg);

        context.setStep(DIALOG_TTS_REQ);
        context.setIdle(false);
        setCurrentContext(context);
        mSpeechServer.reqTts(context.ttsReqStr);
    }

    private synchronized void handleTtsRes(DialogContext context) {
        mTimer.reset();
        if (isPaused()) {
            return;
        }

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "spoken";
        EventBus.getDefault().post(uiMsg);

        //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
        //uiMsg.emotion = 4; //idle
        //EventBus.getDefault().post(uiMsg);

        context = getCurrentContext();
        context.setStep(DIALOG_TTS_RES);
        context.setIdle(false);
        setCurrentContext(context);
        context.domain.operation.process(context);
    }

    private synchronized DialogContext getCurrentContext() {
        if (null == mCurrentDialogContext) {
            mCurrentDialogContext = new DialogContext();
        }
        if (null == mCurrentDialogContext.domain) {
            mCurrentDialogContext.setDomain(General.getInstance());
        }
        if (null == mCurrentDialogContext.person) {
            mCurrentDialogContext.setPerson(getCurrentPerson());
        }
        return mCurrentDialogContext;
    }

    private synchronized void setCurrentContext(DialogContext context) {
        mCurrentDialogContext = context;
    }

    private synchronized boolean checkContextChanged(DialogContext context) {
        if (!mCurrentDialogContext.domain.equals(context.domain) ||
                !mCurrentDialogContext.person.equals(context.person)) {
            return true;
        }
        return false;
    }

    private synchronized DialogContext switchContext(DialogContext context) {
        DialogContext currentContext = getCurrentContext();

        if (!currentContext.domain.equals(context.domain) ||
                !currentContext.person.equals(context.person)) {
            if (null == context.domain) {
                context.domain = currentContext.domain;
            }
            if (null == context.person) {
                context.person = currentContext.person;
            }
            if (null == context.nluReqStr) {
                context.nluReqStr = currentContext.nluReqStr;
            }
            if (null == context.nluResStr) {
                context.nluResStr = currentContext.nluResStr;
            }
            if (null == context.nlgReqStr) {
                context.nlgReqStr = currentContext.nlgReqStr;
            }
            if (null == context.nlgResStr) {
                context.nlgResStr = currentContext.nlgResStr;
            }
        }

        setCurrentContext(context);

        return context;
    }

    public synchronized DialogContext.DialogStep getStep() {
        return mCurrentDialogContext.getStep();
    }

    public class DialogTimeoutCallback implements MyTimer.TimerCallback {
        @Override
        public void onTimeout() {
            mTimer.reset();
            handleIdle();
        }
    }

    private synchronized void handleIdle() {
        mTimer.stop();

        UiMsg uiMsg = new UiMsg();
        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_STRING;
        uiMsg.string = "idle";
        EventBus.getDefault().post(uiMsg);

        uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
        uiMsg.emotion = 4; //idle
        EventBus.getDefault().post(uiMsg);

        mSpeechServer.stopNlu();
        mSpeechServer.stopTts();

        DialogContext context = new DialogContext();
        context.setDomain(General.getInstance());
        context.setPerson(getCurrentPerson());
        context.setStep(DIALOG_IDLE);
        context.setIdle(true);
        setCurrentContext(context);

        mSpeechServer.reqWakeWord();
    }

    private synchronized void handleEnterPause() {
        mPause = true;
        handleIdle();
    }

    private synchronized void handleExitPause() {
        mPause = false;
    }

    private synchronized boolean isPaused() {
        if (mPause) {
            return true;
        }
        return false;
    }

    public void enterVisionTriggerSync() {
        handleVisionTrigger();
    }

    public void enterSoundTriggerSync() {
        handleSoundTrigger();
    }

    public void enterIdleSync() {
        handleIdle();
    }

    public void enterPauseSync() {
        if (!isPaused()) {
            handleEnterPause();
        }
    }

    public void exitPauseSync() {
        if (isPaused()) {
            handleExitPause();
        }
    }

    public void enterTtsResSync() {
        DialogContext context = getCurrentContext();
        handleTtsRes(context);
    }

    public synchronized void reset() {
        DialogContext context = getCurrentContext();
        context.setDomain(General.getInstance());
        setCurrentContext(context);
    }
}
