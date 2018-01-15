package com.sensetime.motionsdksamples.Dialog;

import com.example.fsm.EasyFlow;
import com.example.fsm.EventEnum;
import com.example.fsm.StateEnum;
import com.example.fsm.StatefulContext;
import com.example.fsm.SyncExecutor;
import com.example.fsm.call.EventHandler;
import com.example.fsm.call.StateHandler;
import com.example.fsm.err.LogicViolationError;

import static com.example.fsm.FlowBuilder.from;
import static com.example.fsm.FlowBuilder.on;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_parse_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_parse_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_parse_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_parse_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.start;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_play_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.IDLE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLG_RES_PARSE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLU_RES_PARSE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT2;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT3;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT4;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT5;
import static com.sensetime.motionsdksamples.Dialog.GuessFsm.Events.age_cancel;
import static com.sensetime.motionsdksamples.Dialog.GuessFsm.Events.age_fail;
import static com.sensetime.motionsdksamples.Dialog.GuessFsm.Events.age_ok;
import static com.sensetime.motionsdksamples.Dialog.GuessFsm.States.REQ_AGE;
import static com.sensetime.motionsdksamples.Dialog.GuessFsm.States.RES;

/**
 * Created by lyt on 2017/10/31.
 */

public class GuessFsm {
    public enum States implements StateEnum {
        IDLE, REQ_NLU, NLU_RES_PARSE,
        REQ_NLG, NLG_RES_PARSE,
        REQ_TTS,  TTS_PLAY, TTS_PLAY_DEFAULT,
        TTS_PLAY_DEFAULT2,
        TTS_PLAY_DEFAULT3,
        TTS_PLAY_DEFAULT4,
        TTS_PLAY_DEFAULT5,
        FINISH,
        REQ_AGE,
        RES
    }

    public enum Events implements EventEnum {
        start,
        nlu_res_ok, nlu_res_fail,
        nlu_parse_ok, nlu_parse_fail,
        nlg_res_ok, nlg_res_fail,
        nlg_parse_ok, nlg_parse_fail,
        tts_res_ok, tts_res_fail,
        tts_play_ok,
        fail,
        finish,
        age_ok,
        age_fail,
        age_cancel
    }

    private static class FlowContext extends StatefulContext {
        private String pin;
        private int invalidPinCounter;
        private int balance = 1000;
        private int withdrawAmt;
    }

    private EasyFlow<FlowContext> flow = null;
    private FlowContext mContext = null;

    public GuessFsm() {
        init();
    }

    private void initFlow() {
        if (flow != null) {
            return;
        }


        flow =
                from(IDLE).transit(
                        on(start).to(REQ_AGE).transit(
                                on(age_ok).to(IDLE),
                                on(age_fail).to(IDLE),
                                on(age_cancel).to(IDLE)
                        )
                );

        final boolean[] whenEnterCalled = {false};

        flow.whenEnter(new StateHandler<StatefulContext>() {
            @Override
            public void call(StateEnum state, StatefulContext context) throws Exception {
                whenEnterCalled[0] = true;
            }
        });

        final boolean[] whenLeaveCalled = {false};

        flow.whenLeave(new StateHandler<StatefulContext>() {
            @Override
            public void call(StateEnum state, StatefulContext context) throws Exception {
                whenLeaveCalled[0] = true;
            }
        });

        final boolean[] whenEventCalled = {false};

        flow.whenEvent(new EventHandler<StatefulContext>() {
            @Override
            public void call(EventEnum event, StateEnum from, StateEnum to, StatefulContext context) throws Exception {
                whenEventCalled[0] = true;
            }
        });
    }

    private void bindFlow() {

    }

    public void init() {
        initFlow();
        //flow.start(new FlowContext());
        mContext = new FlowContext();

        //StatefulContext ctx = new StatefulContext();
        flow.executor(new SyncExecutor())
                .trace()
                .start(mContext);

        /*
        try {
            flow.trigger(event_1, ctx);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
        */
    }

    public void test() {
        try {
            flow.trigger(start, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void start() {
        try {
            flow.trigger(start, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void okAge() {
        try {
            flow.trigger(age_ok, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void cancelAge() {
        try {
            flow.trigger(age_cancel, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }
}
