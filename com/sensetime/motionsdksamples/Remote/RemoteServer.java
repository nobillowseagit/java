package com.sensetime.motionsdksamples.Remote;

import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.sensetime.motionsdksamples.Common.FaceAttrFragment;
import com.sensetime.motionsdksamples.Common.MotionServer;
import com.sensetime.motionsdksamples.Detection.Detection;
import com.sensetime.motionsdksamples.Dialog.DetectDialog;
import com.sensetime.motionsdksamples.Dialog.DialogContext;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.Dialog.General;
import com.sensetime.motionsdksamples.Dialog.Guess;
import com.sensetime.motionsdksamples.Dialog.Introduce;
import com.sensetime.motionsdksamples.Dialog.UserConfig;
import com.sensetime.motionsdksamples.Common.FaceAttr;
import com.sensetime.motionsdksamples.Common.FaceProxy;
import com.sensetime.motionsdksamples.Dialog.DialogMsg;
import com.sensetime.motionsdksamples.EventBusUtils.MsgBase;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.InfoServer;
import com.sensetime.motionsdksamples.Common.Person;
import com.sensetime.motionsdksamples.Dialog.Domain;
import com.sensetime.motionsdksamples.MainActivity;
import com.sensetime.motionsdksamples.Photography.Photo;
import com.sensetime.motionsdksamples.Speech.SpeechContext;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.Utils.Config;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NEW_PERSON;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_RES;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_RES;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_PHOTOGRAPH;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_SOUND_TRIGGER;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_RES;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_VISION_TRIGGER;
import static com.sensetime.motionsdksamples.EventBusUtils.MsgBase.MSG_TYPE.MSG_TYPE_REMOTE;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_INTRODUCE;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_USER;

/**
 * Created by lyt on 2017/10/16.
 */

public class RemoteServer extends ServerThread implements IRemote {
    private static RemoteServer instance = new RemoteServer();
    public static RemoteServer getInstance() {
        return instance;
    }

    AsyncHttpServer mHttpServer;
    Config mConfig;
    private FaceServer mFaceServer;
    private FaceProxy mFaceProxy;
    private Person mCurrentPerson;
    private SpeechServer mSpeechServer;
    private DialogServer mDialogServer;
    private InfoServer mInfoServer;
    private MotionServer mMotionServer;
    private FaceAttrFragment mFaceAttrFragment;
    private MainActivity mMainActivity;

    private Detection mDetection;

    public RemoteServer() {
        super("remote-server");
        mMotionServer = MotionServer.getInstance();

        mDetection = Detection.getInstance();
    }

    @Override
    public void run() {
        KLog.trace();

        //执行耗时操作
        while (isRunning) {
            //count();
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MsgBase msgBase) {
        /* Do something */
        KLog.trace();

        if (msgBase.type != MSG_TYPE_REMOTE) {
            return;
        }
    }

    public void init(final Config config, FaceAttrFragment faceAttrFragment) {
        mConfig = config;
        mFaceAttrFragment = faceAttrFragment;


        mHttpServer = new AsyncHttpServer();

        List<WebSocket> _sockets = new ArrayList<WebSocket>();

        mHttpServer.get("/set", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Multimap query = request.getQuery();
                String string;
                string = query.getString("test");
                if (string != null) {
                    mSpeechServer = SpeechServer.getInstance();
                    SpeechContext context = new SpeechContext();
                    if (string.equals("0")) {
                        mSpeechServer.stopNlu(context);
                    } else if (string.equals("1")) {
                        mSpeechServer.reqNlu(context);
                    }

                }

                string = query.getString("vision_trigger");
                if (string != null) {
                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_VISION_TRIGGER;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("req_nlg");
                if (string != null) {
                }

                mFaceServer = FaceServer.getInstance();
                mFaceProxy = new FaceProxy(mFaceServer);
                FaceAttr faceAttr = new FaceAttr();

                string = query.getString("age");
                if (string != null) {
                    faceAttr.mAge = Integer.valueOf(string);
                }

                string = query.getString("gender");
                if (string != null) {
                    faceAttr.mGender = string;
                }

                string = query.getString("name");
                if (string != null) {
                    mFaceServer.setFaceAttr(faceAttr);
                    Person person = new Person();
                    person.mName = string;
                    person.mAge = faceAttr.mAge;
                    person.mGender = faceAttr.mGender;

                    mCurrentPerson = person;

                    /*
                    DialogContext context = new DialogContext();
                    context.domain = new Domain(DOMAIN_DEFAULT);
                    context.person = person;

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_SET_PERSON;
                    msg.context = context;

                    EventBus.getDefault().post(msg);
                    */

                    InfoServer infoServer = InfoServer.getInstance();
                    infoServer.setCurrentPerson(person);
                    //EventBus.getDefault().post(msg);
                }

                string = query.getString("vision_trigger");
                if (string != null) {
                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_VISION_TRIGGER;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("server_ip");
                if (string != null) {
                    mConfig.mServerIp = string;
                }

                string = query.getString("guess");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = Guess.getInstance();

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("photograph");
                if (string != null) {
                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_PHOTOGRAPH;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("introduce");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = Introduce.getInstance();
                    context.nluResStr = "朋友";

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("user_name");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = UserConfig.getInstance();
                    context.nluResStr = "我叫李佳";

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("nlu_res");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.nluResStr = string;

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("nlg_res");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = General.getInstance();
                    context.nlgResStr = string;

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLG_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("tts_res");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = General.getInstance();

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_TTS_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("nlu_req");
                if (string != null) {
                    mSpeechServer = SpeechServer.getInstance();
                    mSpeechServer.reqNlu();
                }

                string = query.getString("tts_req");
                if (string != null) {
                    mSpeechServer = SpeechServer.getInstance();
                    mSpeechServer.reqTts("你好");
                }

                string = query.getString("wake_req");
                if (string != null) {
                    mSpeechServer = SpeechServer.getInstance();
                    mSpeechServer.reqWakeWord();
                }

                string = query.getString("user_new");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = new Domain(DOMAIN_USER);
                    context.person = new Person();

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NEW_PERSON;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("user_new_info");
                if (string != null) {
                    DialogContext context = new DialogContext();
                    context.domain = new Domain(DOMAIN_USER);
                    context.person = mCurrentPerson;

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("sound_trigger");
                if (string != null) {
                    /*
                    DialogContext context = new DialogContext();
                    Domain domain = General.getInstance();
                    context.step = DIALOG_START;
                    context.domain = domain;
                    domain.operation.process(context);
                    */
                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_SOUND_TRIGGER;
                    EventBus.getDefault().post(msg);
                }

                string = query.getString("db_get_all");
                if (string != null) {
                    mInfoServer = InfoServer.getInstance();
                    List<Person> listPerson = mInfoServer.getAllPerson();
                    KLog.trace();
                }

                string = query.getString("db_delete_all");
                if (string != null) {
                    mInfoServer = InfoServer.getInstance();
                    mInfoServer.deleteAllPerson();
                    KLog.trace();
                }

                string = query.getString("take_picture");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    Photo photo = Photo.getInstance();
                    photo.startFull();
                }

                string = query.getString("ui_speak");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.speak();
                }

                string = query.getString("ui_head");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.head();
                }

                string = query.getString("ui_hit");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.hit();
                }

                string = query.getString("ui_touch_chin");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.touchChin();
                }

                string = query.getString("ui_touch_face");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.touchFace();
                }

                string = query.getString("ui_pinch_arm");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.pinchArm();
                }

                string = query.getString("ui_kiss");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.kiss();
                }

                string = query.getString("ui_hug");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.hug();
                }

                string = query.getString("ui_fall");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    mMainActivity.fall();
                }

                string = query.getString("turn_left");
                if (string != null) {
                    mMotionServer.turnLeft();
                }

                string = query.getString("turn_right");
                if (string != null) {
                    mMotionServer.turnRight();
                }

                string = query.getString("obj");
                if (string != null) {
                    //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
                    //Message msg = new Message();
                    //handler.sendMessage(msg);
                    //mMainActivity.fall();
                    Photo photo = Photo.getInstance();
                    RemotePhotoSingleCallback mRemotePhotoSingleCallback = new RemotePhotoSingleCallback();
                    photo.setPhotoSingleCallback(mRemotePhotoSingleCallback);

                    photo.takePicA();
                    //DetectDialog detectObject = DetectDialog.getInstance();
                    //detectObject.upImage();
                }

                string = query.getString("detect");
                if (string != null) {
                    mDetection.process();
                }

                response.send("OK");
            }
        });

        mHttpServer.get("/introduce", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Multimap query = request.getQuery();
                String string;
                Person person = new Person();

                string = query.getString("gender");
                if (string != null) {
                    person.mGender = string;
                }

                string = query.getString("age");
                if (string != null) {
                    person.mAge = Integer.valueOf(string);

                    DialogContext context = new DialogContext();
                    context.domain = new Domain(DOMAIN_INTRODUCE);
                    context.person = person;

                    DialogMsg msg = new DialogMsg();
                    msg.cmd = DIALOG_CMD_NLU_RES;
                    msg.context = context;

                    EventBus.getDefault().post(msg);
                }
                response.send("OK");
            }
        });

        mHttpServer.get("/get", new HttpServerRequestCallback() {
            @Override
            public void onRequest(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                //sendResources(request, response);
                Multimap query = request.getQuery();
                String string = query.getString("test2");
                if (string != null) {
                    response.send("test2=2");
                }

                string = query.getString("face_attr");
                if (string != null) {
                    //FaceAttr faceAttr = mFaceProxy.getFaceAttr();
                    KLog.trace();
                }

                string = query.getString("face_location");
                if (string != null) {
                    //FaceLocation faceLocation = mFaceProxy.getFaceLocation();
                    KLog.trace();
                }
            }
        });

// listen on port 5000
        mHttpServer.listen(5000);
// browsing http://localhost:5000 will return Hello!!!
    }

    public void setMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public class RemotePhotoSingleCallback implements Photo.PhotoSingleCallback {
        @Override
        public void onCompleted() {
            DetectDialog detectDialog = DetectDialog.getInstance();
            detectDialog.upImage();
            //mDialogServer.exitPauseSync();
        }
    }
}