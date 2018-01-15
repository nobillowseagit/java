package com.sensetime.motionsdksamples.Detection;

import android.content.Context;

import com.example.https.utils.HTTPSUtils;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.Photography.Photo;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.Utils.Config;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

/**
 * Created by lyt on 2018/1/3.
 */

public class Detection {
    private static Detection instance = new Detection();
    public static Detection getInstance() {
        return instance;
    }

    private SpeechServer mSpeechServer;
    private DialogServer mDialogServer;

    private Context mContext;
    private Config mConfig;

    private boolean upload_complete = false;
    private boolean reg_complete = false;

    private String mRes;

    public Detection() {
        mSpeechServer = SpeechServer.getInstance();
        mDialogServer = DialogServer.getInstance();
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setConfig(Config config) {
        mConfig = config;
    }

    public synchronized void uploadImage() {
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                upload_complete = true;
            }
        });
    }

    public synchronized void getRes() {
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
                if (!obj.equals("0")) {
                    reg_complete = true;
                    mRes = obj;
                    /*
                    mSpeechServer.reqSingleTts(obj);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    */
                    //mDialogServer.exitPauseSync();
                }
            }
        });
    }

    public void process() {
        int cnt = 0;
        upload_complete = false;
        reg_complete = false;

        KLog.d("lijia: enter");

        uploadImage();

        while(true) {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (upload_complete) {
                getRes();
                if (reg_complete) {
                    //upload_complete = false;
                    //reg_complete = false;
                    break;
                }
            }

            cnt++;
            if (cnt >= 10) {
                break;
            }

        }

        if (reg_complete) {
            mSpeechServer.reqSingleTts(mRes);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        upload_complete = false;
        reg_complete = false;

        mDialogServer.exitPauseSync();
        KLog.d("lijia: exit");
    }
}
