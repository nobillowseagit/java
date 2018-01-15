package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.FaceAttr;
import com.sensetime.motionsdksamples.Common.FaceProxy;
import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.Person;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_GUESS_AGE;

/**
 * Created by lyt on 2017/10/31.
 */

public class Guess extends Domain {
    private static final int GUESS_START = 1;
    private static final int GUESS_CANCEL = 2;
    public FaceServer mFaceServer;
    public FaceProxy mFaceProxy;
    public FaceAttr mFaceAttr;
    public GuessFsm mGuessFsm;
    private List<String> mNluResKeyWords;
    private List<String> mNlgResKeyWords;
    private DialogManager mDialogManager;

    public Guess() {
        super(DOMAIN_GUESS_AGE);
        mGuessFsm = new GuessFsm();
        GuessOperation operation = new GuessOperation();
        setDomainOperation(operation);
        init();

        mNluResKeyWords = new ArrayList<>();
        mNlgResKeyWords = new ArrayList<>();

        mNluResKeyWords.add("猜");

        mNlgResKeyWords.add("猜");
        mNlgResKeyWords.add("看不到");
    }

    private static Guess instance = new Guess();

    public static Guess getInstance() {
        return instance;
    }

    public void init() {
        mFaceServer = FaceServer.getInstance();
        mFaceProxy = new FaceProxy(mFaceServer);
    }

    public void start() {
        mGuessFsm.start();
        /*
        NlgMsg msg = new NlgMsg();
        msg.cmd = NlgMsg.NLG_CMD.NLG_CMD_REQ;
        Domain domain = new Domain(DOMAIN_GUESS_AGE);
        Person person = new Person();
        msg.mNlgContext.mDomain = domain;
        msg.mNlgContext.mPerson = person;
        EventBus.getDefault().post(msg);
        */
        mFaceAttr = mFaceProxy.getFaceAttr();

        Domain domain = new Domain(DOMAIN_GUESS_AGE);

        Person person = new Person();
        person.mGender = mFaceAttr.mGender;
        person.mAge = mFaceAttr.mAge;

        DialogContext context = new DialogContext();
        context.domain = domain;
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void nlu(DialogContext context) {
        /*
        int res = 0;
        for (int i = 0; i < nluResKeyWords.size(); i++) {
            if (context.nluResStr.contains(nluResKeyWords.get(i))) {
                res = 1;
                break;
            }
        }
        if (1 == res) {
            context.domain = this;
            context.person = getCurrentPerson();
            context.nlgReqStr = context.nluResStr;

            DialogMsg msg = new DialogMsg();
            msg.cmd = DIALOG_CMD_NLG_REQ;
            msg.context = context;
            EventBus.getDefault().post(msg);
        }
        */

        context.domain = this;
        context.person = getCurrentPerson();
        context.nlgReqStr = context.nluResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    public void nlg(DialogContext context) {
        /*
        int res = 0;
        for (int i = 0; i < nlgResKeyWords.size(); i++) {
            if (context.nlgResStr.contains(nlgResKeyWords.get(i))) {
                res = 1;
                break;
            }
        }
        if (1 == res) {
            if (null != context.nlgActCmdStr) {
                switch (context.nlgActCmdStr) {
                    case "猜年龄":
                        String target = context.nlgResStr.replace("@:年龄#", String.valueOf(context.person.getAge()));
                        //context.nlgResStr = target;
                        context.ttsReqStr = target;
                        break;
                    default:
                        context.ttsReqStr = context.nlgResStr;
                }
            } else {
                context.ttsReqStr = context.nlgResStr;
            }
            DialogMsg msg = new DialogMsg();
            msg.cmd = DIALOG_CMD_TTS_REQ;
            msg.context = context;
            EventBus.getDefault().post(msg);
        }
        */

        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "猜年龄":
                    String target = context.nlgResStr.replace("@:年龄#", String.valueOf(context.person.getAge()));
                    //context.nlgResStr = target;
                    context.ttsReqStr = target;
                    break;
                default:
                    context.ttsReqStr = context.nlgResStr;
            }
        } else {
            context.ttsReqStr = context.nlgResStr;
        }
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }


    public String ok(String string) {
        mGuessFsm.okAge();

        String target = string.replace("$年龄", String.valueOf(mFaceAttr.mAge));

        Domain domain = new Domain(DOMAIN_GUESS_AGE);

        Person person = new Person();
        person.mGender = mFaceAttr.mGender;
        person.mAge = mFaceAttr.mAge;

        DialogContext context = new DialogContext();
        context.domain = domain;
        context.person = person;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);

        return target;
    }

    public void cancel() {
        mGuessFsm.okAge();
    }

    public void finish() {

    }

    public void tts(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    /*
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(GuessMsg msg) {
        if (msg.type != MSG_TYPE_GUESS) {
            return;
        }
        msg.mString.replace("$年龄", String.valueOf(mFaceAttr.mAge));
        mGuessFsm.okAge();
        mTimerTask.cancel();
        mTimer.purge();
    };
    */

    public class GuessOperation implements Domain.DomainOperation {
        @Override
        public void preprocess(DialogContext context) {

        }

        @Override
        public void process(DialogContext context) {
            switch (context.step) {
                case DIALOG_START:
                    start();
                    break;
                case DIALOG_NLU_RES:
                    nlu(context);
                    break;
                case DIALOG_NLG_RES:
                    //ok(context.nlgResStr);
                    nlg(context);
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
}
