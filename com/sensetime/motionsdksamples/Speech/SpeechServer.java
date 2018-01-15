package com.sensetime.motionsdksamples.Speech;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.engines.AICloudSdsEngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AISdsListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sensetime.motionsdksamples.Dialog.DialogContext;
import com.sensetime.motionsdksamples.Dialog.DialogMsg;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Speech.AISpeech.asinterface.IAISpeechActionsCallback;
import com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions;
import com.sensetime.motionsdksamples.Speech.AISpeech.service.AISpeechService;
import com.sensetime.motionsdksamples.Speech.SpeechUtils.SampleConstants;
import com.sensetime.motionsdksamples.UiMsg;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.sensetime.motionsdksamples.Speech.AISpeech.entity.Actions.SdsResult;


/**
 * Created by lyt on 2017/10/16.
 */

public class SpeechServer extends ServerThread {
    private static SpeechServer instance = new SpeechServer("speech-server");
    public static SpeechServer getInstance() {
        return instance;
    }

    public static final int SPEECH_MSG_NLU_REQ = 1;
    private static final int SPEECH_MSG_NLU_RES = 2;
    private static final int SPEECH_MSG_NLU_STOP = 3;

    private boolean mSingleTts;

    private Context mContext;
    private Context mApplicationContext;
    private AICloudASREngine mEngine;
    private AICloudSdsEngine mSdsEngine;
    private String Tag;
    String mResultText;
    private SpeechServer mSpeechServer;
    private DialogServer mDialogServer;

    private SpeechHandler mSpeechHandler;
    private AISpeechService aiSpeechService;

    public SpeechServer(String name) {
        super(name);
        mDialogServer = DialogServer.getInstance();
    }

    private void ASRInit() {
        if (aiSpeechService != null) {
            aiSpeechService.GetASRCommander().InitASR(mApplicationContext, new IAISpeechActionsCallback() {
                @Override
                public void SendActionMessage(Actions action, boolean isSuccess, String data) {
                    Log.v("SmartAgentMessage:", "Action:" + action.toString() + "   IsSuccess:" + isSuccess + "           Data:" + data);
                }
            });
        }
    }

    void SdsInit() {
        if (aiSpeechService != null) {
            aiSpeechService.GetSdsCommander().InitSds(mApplicationContext, new IAISpeechActionsCallback() {
                @Override
                public void SendActionMessage(Actions action, boolean isSuccess, String data) {
                    Log.v("SmartAgentMessage:", "Action:" + action.toString() + "   IsSuccess:" + isSuccess + "           Data:" + data);
                    switch (action) {
                        case SdsResult:
                            String strDomain = null;
                            String strNlg = null;
                            String strInput = null;

                            com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(data);
                            if (null != jsonObj) {
                                strDomain = jsonObj.getString("Domain");
                                strNlg = jsonObj.getString("NLG");
                                strInput = jsonObj.getString("Input");

                                if (strInput.startsWith("介绍")) {
                                    strDomain = null;
                                }
                            }
                            strDomain = null;
                            strNlg = null;

                            //UiMsg uiMsg = new UiMsg();
                            //uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_LISTENED;
                            //EventBus.getDefault().post(uiMsg);

                            //String json = objectToJson(data);
                            //Map map = jsonToMap(json);

                            /*
                            int keyword_index = -1;
                            int keyword_end_index;
                            int start, end;
                            String keyword = "\"input\":";
                            keyword_index = data.indexOf(keyword);
                            keyword_end_index = keyword_index + keyword.length();
                            keyword_end_index += 1;

                            String str2 = data.substring(keyword_end_index);
                            start = str2.indexOf("\"");
                            start += 1;

                            String str3 = str2.substring(start);
                            end = str3.indexOf("\"");

                            String target = str3.substring(start - 1, end);
                            */

                            DialogContext context = new DialogContext();
                            //context.nluResStr = target;
                            context.nluResStr = strInput;
                            context.nluResDomain = strDomain;
                            context.nluResNlg = strNlg;

                            DialogMsg msg = new DialogMsg();
                            msg.cmd = DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_RES;
                            msg.context = context;
                            EventBus.getDefault().post(msg);
                            break;

                        case SdsError:
                            DialogContext context2 = new DialogContext();
                            context2.nluResStr = "无效";
                            DialogMsg msg2 = new DialogMsg();
                            msg2.cmd = DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_RES;
                            msg2.context = context2;
                            EventBus.getDefault().post(msg2);
                            break;
                    }

                }
            });
        }
    }

    private void TtsInit() {
        if (aiSpeechService != null) {
            aiSpeechService.GetTtsCommander().InitTts(mApplicationContext, new IAISpeechActionsCallback() {
                @Override
                public void SendActionMessage(Actions action, boolean isSuccess, String data) {
                    Log.v("SmartAgentMessage:", "Action:" + action.toString() +
                            "   IsSuccess:" + isSuccess + "           Data:" + data);
                    if (!mSingleTts) {
                        DialogMsg msg = new DialogMsg();
                        msg.cmd = DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_RES;
                        EventBus.getDefault().post(msg);
                        //mDialogServer.enterTtsResSync();
                    }
                }
            });
        }
    }

    void WakeUpInit() {
        if (aiSpeechService != null) {
            aiSpeechService.GetWakeUpCommander().InitWakeUp(mApplicationContext, new IAISpeechActionsCallback() {
                @Override
                public void SendActionMessage(Actions action, boolean isSuccess, String data) {
                    Log.v("SmartAgentMessage:", "Action:" + action.toString() +
                            "   IsSuccess:" + isSuccess + "           Data:" + data);
                    if (isSuccess && action == Actions.WakeupInit) {
                        KLog.v("lijia wakeup init completed");
                        //wakeUpAndUnlock(true);
                        aiSpeechService.GetWakeUpCommander().StartWakeup();
                    } else if (isSuccess && action == Actions.WakeupStart) {
                        KLog.v("lijia wake word detected");
                        aiSpeechService.GetWakeUpCommander().StartWakeup();

                        DialogMsg msg = new DialogMsg();
                        msg.cmd = DialogMsg.DIALOG_CMD.DIALOG_CMD_SOUND_TRIGGER;
                        EventBus.getDefault().post(msg);
                    }
                }
            });
        }
    }

    public static String objectToJson(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
        }
        return "";
    }

    public Map jsonToMap(String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        Map map = null;
        try {
            map = parseObject(jsonData, new TypeReference<Map>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void init(Context context, Context appContext, AISpeechService aiSpeechService) {
        mContext = context;
        mApplicationContext = appContext;
        this.aiSpeechService = aiSpeechService;
        mSpeechServer = instance;

        SpeechAuth auth = new SpeechAuth(context);
        auth.runAuth();

        //initSds(context);

        //mSpeechServer.start();
        //Looper looper = mSpeechServer.getLooper();
        //mSpeechHandler = new SpeechHandler(looper);

        WakeUpInit();
        SdsInit();
        TtsInit();
    }

    public void reqWakeWord() {
        aiSpeechService.GetWakeUpCommander().StartWakeup();
    }


    public void reqNlu() {
        aiSpeechService.GetSdsCommander().StartRecording();
    }

    public void stopNlu() {
        aiSpeechService.GetSdsCommander().SdsCancel();
        aiSpeechService.GetSdsCommander().StopRecording();
    }

    public void reqTts(String string) {
        mSingleTts = false;
        aiSpeechService.GetTtsCommander().StartSpeaking(string);
    }

    public void reqSingleTts(String string) {
        mSingleTts = true;
        aiSpeechService.GetTtsCommander().StartSpeaking(string);
    }

    public void stopTts() {
        aiSpeechService.GetTtsCommander().StopSpeaking();
    }


    public void start() {
        super.start();
        mSpeechHandler = new SpeechHandler();
    }

    public void reqNlu(SpeechContext context) {
        Message msg = Message.obtain();
        msg.what = SPEECH_MSG_NLU_REQ;
        msg.obj = context;
        mSpeechHandler.sendMessage(msg);
    }

    public void resNlu(SpeechContext context) {
        Message msg = Message.obtain();
        msg.what = SPEECH_MSG_NLU_RES;
        msg.obj = context;
        mSpeechHandler.sendMessage(msg);
    }

    public void stopNlu(SpeechContext context) {
        Message msg = Message.obtain();
        msg.what = SPEECH_MSG_NLU_STOP;
        msg.obj = context;
        mSpeechHandler.sendMessage(msg);
    }

    public class SpeechHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            SpeechContext context = (SpeechContext) msg.obj;
            switch (msg.what) {
                case SPEECH_MSG_NLU_REQ:
                    handleNluReq(context);
                    break;
                case SPEECH_MSG_NLU_RES:
                    handleNluRes(context);
                    break;
                case SPEECH_MSG_NLU_STOP:
                    handleNluStop(context);
                    break;
            }
        }

        private void handleNluReq(SpeechContext context) {
            mEngine.start();
        }

        private void handleNluRes(SpeechContext context) {
            mEngine.stopRecording();
        }

        private void handleNluStop(SpeechContext context) {
            mEngine.cancel();
            mEngine.stopRecording();
        }
    }

}
