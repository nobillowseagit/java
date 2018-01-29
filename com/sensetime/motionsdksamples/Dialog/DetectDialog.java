package com.sensetime.motionsdksamples.Dialog;

import android.content.Context;

import com.example.https.utils.HTTPSUtils;
import com.sensetime.motionsdksamples.Detection.Detection;
import com.sensetime.motionsdksamples.Photography.Photo;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.UiMsg;
import com.sensetime.motionsdksamples.Utils.Config;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLG_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_NLU_REQ;
import static com.sensetime.motionsdksamples.Dialog.DialogMsg.DIALOG_CMD.DIALOG_CMD_TTS_REQ;
import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_DETECT;
import static java.lang.Thread.sleep;

/**
 * Created by lyt on 2017/11/6.
 */

public class DetectDialog extends DialogBase {
    private static DetectDialog instance = new DetectDialog();
    public static DetectDialog getInstance() {
        return instance;
    }

    private Context mContext;
    private Config mConfig;

    private Photo mPhoto;
    private Detection mDetection;

    private DialogServer mDialogServer;
    private SpeechServer mSpeechServer;

    private DetectPhotoSingleCallback mDetectPhotoSingleCallback;

    public DetectDialog() {
        super(DOMAIN_DETECT);
        mDialogServer = DialogServer.getInstance();
        mSpeechServer = SpeechServer.getInstance();

        mPhoto = Photo.getInstance();
        mDetectPhotoSingleCallback = new DetectPhotoSingleCallback();
        mPhoto.setPhotoSingleCallback(mDetectPhotoSingleCallback);

        mDetection = Detection.getInstance();
    }

    public void init() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setConfig(Config config) {
        mConfig = config;
    }

    @Override
    public void start(DialogContext context) {
        super.start(context);
    }

    @Override
    public void nluRes(DialogContext context) {
        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLG_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void nlgRes(DialogContext context) {
        UiMsg uiMsg = new UiMsg();

        if (null != context.nlgActCmdStr) {
            switch (context.nlgActCmdStr) {
                case "交互":
                    if (null != context.nlgActParamStr) {
                        switch (context.nlgActParamStr) {
                            case "微笑":
                                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                uiMsg.emotion = 1;
                                EventBus.getDefault().post(uiMsg);
                                break;
                            case "睡觉":
                                uiMsg.cmd = UiMsg.UI_CMD.UI_CMD_EMOTION;
                                uiMsg.emotion = 2;
                                EventBus.getDefault().post(uiMsg);
                                break;
                        }
                    }
                    break;
            }
        }

        context.ttsReqStr = context.nlgResStr;

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_TTS_REQ;
        msg.context = context;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void ttsRes(DialogContext context) {
        mDialogServer.enterPauseSync();

        mPhoto.takePicA();
        //upImage();
        //mSpeechServer.reqSingleTts();

        DialogMsg msg = new DialogMsg();
        msg.cmd = DIALOG_CMD_NLU_REQ;
        EventBus.getDefault().post(msg);
    }

    public void cancel() {

    }

    public void finish() {

    }

    public void getRes() {
        String urlString = "https://" + mConfig.mServerIp + ":" + mConfig.mServerPort + "/image_get_res";
        Request request = new Request.Builder()
                //.url("https://192.168.50.65:8888/get_data?question="+question)
                .url(urlString)
                .build();

        HTTPSUtils httpsUtils = new HTTPSUtils(mContext);

        httpsUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mSpeechServer.reqSingleTts("不认识");
                mDialogServer.exitPauseSync();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String obj = response.body().string();
                obj.trim();
                KLog.v(obj);
                mSpeechServer.reqSingleTts(obj);
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDialogServer.exitPauseSync();

                reg_complete = true;
            }
        });
    }

    public void upImage() {
        //File file = new File(Environment.getExternalStorageDirectory() + "/Download/image1.jpg");
        File file = new File("/sdcard/Download/" + "image1.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img", "image1.jpg",
                        RequestBody.create(MediaType.parse("image/png"), file));

        MultipartBody requestBody = builder.build();

        String strUrl = "https://" + mConfig.mServerIp + ":" + mConfig.mServerPort + "/upload";

        Request request = new Request.Builder()
                .url(strUrl)
                .post(requestBody)
                .build();

        HTTPSUtils httpsUtils = new HTTPSUtils(mContext);

        httpsUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //System.out.println("--------------onFailure--------------" + e.toString());
                //mSpeechServer.reqSingleTts("不认识");
                //mDialogServer.exitPauseSync();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println("--------------onResponse--------------" + response.body().string());
                /*
                String obj = response.body().string();
                obj.trim();
                KLog.v(obj);
                mSpeechServer.reqSingleTts(obj);
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDialogServer.exitPauseSync();
                */
                upload_complete = true;
            }
        });
    }


    private boolean upload_complete = false;
    private boolean reg_complete = false;

    public class DetectPhotoSingleCallback implements Photo.PhotoSingleCallback {
        @Override
        public void onCompleted() {
            //DetectDialog detectObject = DetectDialog.getInstance();
            //detectObject.upImage();

            mDetection.process();

            /*
            int cnt = 0;
            upload_complete = false;
            reg_complete = false;

            upImage();

            while(true) {
                if (upload_complete) {
                    getRes();
                    if (reg_complete) {
                        upload_complete = false;
                        reg_complete = false;
                        break;
                    }
                }

                cnt++;
                if (cnt >= 10) {
                    upload_complete = false;
                    reg_complete = false;
                    break;
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //mDialogServer.exitPauseSync();
            */
        }
    }
}
