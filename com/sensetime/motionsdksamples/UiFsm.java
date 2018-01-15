package com.sensetime.motionsdksamples;

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
import static com.sensetime.motionsdksamples.UiFsm.Events.running_failed;
import static com.sensetime.motionsdksamples.UiFsm.Events.sound_trigger;
import static com.sensetime.motionsdksamples.UiFsm.Events.speak_completed;
import static com.sensetime.motionsdksamples.UiFsm.Events.think_feedback;
import static com.sensetime.motionsdksamples.UiFsm.Events.timeout;
import static com.sensetime.motionsdksamples.UiFsm.Events.vision_detected;
import static com.sensetime.motionsdksamples.UiFsm.Events.vision_trigger;
import static com.sensetime.motionsdksamples.UiFsm.Events.voice_detected;
import static com.sensetime.motionsdksamples.UiFsm.States.IDLE;
import static com.sensetime.motionsdksamples.UiFsm.States.DETECTING;
import static com.sensetime.motionsdksamples.UiFsm.States.SPEAKING;
import static com.sensetime.motionsdksamples.UiFsm.States.THINKING;


/**
 * Created by lyt on 2017/10/31.
 */

public class UiFsm {
    private String mString;

    public enum States implements StateEnum {
        IDLE, DETECTING, THINKING, SPEAKING
    }

    public enum Events implements EventEnum {
        sound_trigger, vision_trigger,
        voice_detected, vision_detected,
        think_feedback, speak_completed,
        running_failed, timeout
    }

    private static class FlowContext extends StatefulContext {
        private String pin;
        private int invalidPinCounter;
        private int balance = 1000;
        private int withdrawAmt;
    }

    private EasyFlow<FlowContext> flow = null;
    private FlowContext mContext = null;

    public synchronized String getString() {
        return mString;
    }

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


        flow =
                from(IDLE).transit(
                        on(sound_trigger).to(DETECTING).transit(
                                on(voice_detected).to(THINKING).transit(
                                        on(think_feedback).to(SPEAKING).transit(
                                                on(speak_completed).to(DETECTING),
                                                on(running_failed).to(IDLE),
                                                on(timeout).to(IDLE)
                                        ),
                                        on(running_failed).to(IDLE),
                                        on(timeout).to(IDLE)
                                ),
                                on(vision_detected).to(THINKING),
                                on(running_failed).to(IDLE),
                                on(timeout).to(IDLE)
                        ),
                        on(vision_trigger).to(DETECTING)
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

        flow
                .whenEnter(IDLE, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                        mString = "idle";
                    }
                })
                .whenEnter(DETECTING, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                        mString = "listening";
                    }
                })
                .whenEnter(THINKING, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                        mString = "thinking";
                    }
                })
                .whenEnter(SPEAKING, new ContextHandler<StatefulContext>() {
                    @Override
                    public void call(StatefulContext context) throws Exception {
                        mString = "speaking";
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
    }

    public void soundTrigger() {
        try {
            flow.trigger(sound_trigger, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void visionTrigger() {
        try {
            flow.trigger(vision_trigger, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void voiceDetected() {
        try {
            flow.trigger(voice_detected, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void visionDetected() {
        try {
            flow.trigger(voice_detected, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void thinkFeedback() {
        try {
            flow.trigger(think_feedback, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void speakCompleted() {
        try {
            flow.trigger(speak_completed, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void runFailed() {
        try {
            flow.trigger(running_failed, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }

    public void runTimeout() {
        try {
            flow.trigger(timeout, mContext);
        } catch (LogicViolationError logicViolationError) {
            logicViolationError.printStackTrace();
        }
    }
}
