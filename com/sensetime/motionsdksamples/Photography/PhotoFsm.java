package com.sensetime.motionsdksamples.Photography;

import com.example.fsm.EasyFlow;
import com.example.fsm.EventEnum;
import com.example.fsm.StateEnum;
import com.example.fsm.StatefulContext;
import com.example.fsm.SyncExecutor;
import com.example.fsm.call.ContextHandler;
import com.example.fsm.call.EventHandler;
import com.example.fsm.call.StateHandler;
import com.example.fsm.err.LogicViolationError;

import static com.example.fsm.FlowBuilder.from;
import static com.example.fsm.FlowBuilder.on;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.complete_full_shot;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.detect_left_face;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.detect_right_face;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.no_detect_right_face;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.shot_complete;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.start_full_shot;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.timeout;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.Events.turn_complete;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.IDLE;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.WAIT_FOUND_LEFT_FACE;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.WAIT_FOUND_RIGHT_FACE;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.WAIT_FULL_SHOT_COMPLETE;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.WAIT_SHOT_COMPLETE;
import static com.sensetime.motionsdksamples.Photography.PhotoFsm.States.WAIT_TURN_COMPLETE;


/**
 * Created by lyt on 2017/10/31.
 */

public class PhotoFsm {
    public enum States implements StateEnum {
        IDLE, WAIT_FOUND_LEFT_FACE, WAIT_TURN_COMPLETE, WAIT_SHOT_COMPLETE,
        WAIT_FOUND_RIGHT_FACE, WAIT_FULL_SHOT_COMPLETE
    }

    public enum Events implements EventEnum {
        start_full_shot, detect_left_face,
        turn_complete, shot_complete,
        detect_right_face, no_detect_right_face,
        complete_full_shot,
        timeout
    }

    public static class FlowContext extends StatefulContext {
        public States mState;
    }

    private FlowContext mFlowContext = null;
    private EasyFlow<FlowContext> flow = null;

    private void init() {
        if (flow != null) {
            return;
        }

/*
        flow =
                from(IDLE).transit(
                        on(sound).to(REQ_NLU).transit(
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

        flow = from(IDLE).transit(
                on(start_full_shot).to(WAIT_FOUND_LEFT_FACE).transit(
                        on(detect_left_face).to(WAIT_TURN_COMPLETE).transit(
                                on(turn_complete).to(WAIT_SHOT_COMPLETE).transit(
                                        on(shot_complete).to(WAIT_FOUND_RIGHT_FACE).transit(
                                                on(detect_right_face).to(WAIT_FULL_SHOT_COMPLETE).transit(
                                                        on(complete_full_shot).to(IDLE)
                                                ),
                                                on(no_detect_right_face).to(WAIT_TURN_COMPLETE)
                                        )
                                ),
                                on(timeout).to(WAIT_FULL_SHOT_COMPLETE)
                        ),
                        on(timeout).to(WAIT_FULL_SHOT_COMPLETE)
                )
        );


        final boolean[] whenEnterCalled = {false};

        flow.whenEnter(new StateHandler<StatefulContext>() {
            @Override
            public void call(StateEnum state, StatefulContext context) throws Exception {
                whenEnterCalled[0] = true;
                mFlowContext.mState = (States) state;
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

        flow
                .whenEnter(IDLE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                    }
                })
                .whenEnter(WAIT_FOUND_LEFT_FACE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                    }
                })
                .whenEnter(WAIT_TURN_COMPLETE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                    }
                })
                .whenEnter(WAIT_FOUND_RIGHT_FACE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                    }
                })
                .whenEnter(WAIT_FULL_SHOT_COMPLETE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                    }
                });

    }

    private void bind() {

    }

    public void start() {
        init();
        //flow.start(new FlowContext());
        mFlowContext = new FlowContext();

        //StatefulContext ctx = new StatefulContext();
        flow.executor(new SyncExecutor())
                .trace()
                .start(mFlowContext);
    }

    public FlowContext getFlowContext() {
        return mFlowContext;
    }

}
