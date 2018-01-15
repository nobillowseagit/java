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
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.finish;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_cancel;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_parse_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_parse_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_req;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlg_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_cancel;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_parse_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_parse_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_req;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.nlu_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.start;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_play_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_req;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_res_fail;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.Events.tts_res_ok;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.FINISH;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.IDLE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLG_FINISH;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLG_RES_PARSE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLG_WAIT;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLU_FINISH;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLU_RES_PARSE;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.NLU_WAIT;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT2;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT3;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT4;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_PLAY_DEFAULT5;
import static com.sensetime.motionsdksamples.Dialog.DialogFsm.States.TTS_WAIT;

/**
 * Created by lyt on 2017/10/31.
 */

public class DialogFsm {
    public enum States implements StateEnum {
        IDLE,
        NLU_WAIT, NLU_RES_PARSE, NLU_FINISH,
        NLG_WAIT, NLG_RES_PARSE, NLG_FINISH,
        TTS_WAIT,  TTS_PLAY, TTS_PLAY_DEFAULT, TTS_FINISH,
        TTS_PLAY_DEFAULT2,
        TTS_PLAY_DEFAULT3,
        TTS_PLAY_DEFAULT4,
        TTS_PLAY_DEFAULT5,
        FINISH
    }

    public enum Events implements EventEnum {
        start,
        nlu_req,
        nlu_res_ok, nlu_res_fail,
        nlu_parse_ok, nlu_parse_fail,
        nlg_req, nlu_cancel,
        nlg_res_ok, nlg_res_fail,
        nlg_cancel,
        nlg_parse_ok, nlg_parse_fail,
        tts_req,
        tts_res_ok, tts_res_fail,
        tts_play_ok,
        fail,
        finish
    }

    private static class FlowContext extends StatefulContext {
        private String pin;
        private int invalidPinCounter;
        private int balance = 1000;
        private int withdrawAmt;
    }

    private EasyFlow<FlowContext> flow = null;
    private FlowContext mContext = null;

    private void init() {
        if (flow != null) {
            return;
        }

/*
        flow =
                from(IDLE).transit(
                        on(start).to(REQ_NLU).transit(
                                on(nlu_res_ok).to(NLU_RES_PARSE).transit(
                                        on(nlu_parse_ok).to(REQ_NLG).transit(
                                                on(nlg_res_ok).to(NLG_RES_PARSE).transit(
                                                        on(nlg_parse_ok).to(REQ_TTS).transit(
                                                                on(tts_res_ok).to(TTS_PLAY).transit(
                                                                        on(tts_play_ok).to(IDLE)
                                                                ),
                                                                on(tts_res_fail).to(TTS_PLAY_DEFAULT).transit(
                                                                        on(tts_play_ok).to(IDLE)
                                                                )
                                                        ),
                                                        on(nlg_parse_fail).to(TTS_PLAY_DEFAULT2).transit(
                                                                on(tts_play_ok).to(IDLE)
                                                        )
                                                )
                                        )
                                )
                        )
                );
*/


        flow =
                from(IDLE).transit(
                        on(nlu_req).to(NLU_WAIT).transit(
                                on(nlu_res_ok).to(NLU_RES_PARSE).transit(
                                        on(nlu_parse_ok).to(NLU_FINISH).transit(
                                                on(nlg_req).to(NLG_WAIT).transit(
                                                        on(nlg_res_ok).to(NLG_RES_PARSE).transit(
                                                                on(nlg_parse_ok).to(NLG_FINISH).transit(
                                                                        on(tts_req).to(TTS_WAIT).transit(
                                                                                on(tts_res_ok).to(TTS_PLAY).transit(
                                                                                        on(tts_play_ok).to(IDLE)
                                                                                ),
                                                                                on(tts_res_fail).to(TTS_PLAY_DEFAULT).transit(
                                                                                        on(tts_play_ok).to(IDLE)
                                                                                )
                                                                        )
                                                                ),
                                                                on(nlg_parse_fail).to(TTS_PLAY_DEFAULT2).transit(
                                                                        on(tts_play_ok).to(IDLE)
                                                                )
                                                        ),
                                                        on(nlg_res_fail).to(TTS_PLAY_DEFAULT3).transit(
                                                                on(tts_play_ok).to(IDLE)
                                                        ),
                                                        on(nlg_cancel).to(IDLE)

                                                ),
                                                on(nlu_cancel).to(IDLE)
                                        ),
                                        on(nlu_parse_fail).to(TTS_PLAY_DEFAULT4).transit(
                                                on(tts_play_ok).to(IDLE)
                                        ),
                                        on(nlu_cancel).to(IDLE)
                                ),
                                on(nlu_res_fail).to(TTS_PLAY_DEFAULT5).transit(
                                        on(tts_play_ok).to(IDLE)
                                ),
                                on(nlu_cancel).to(IDLE)
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

    private void bind() {

    }

    public void start() {
        init();
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

    public void reqNlg() {
        try {
            flow.trigger(nlg_req, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }
}
