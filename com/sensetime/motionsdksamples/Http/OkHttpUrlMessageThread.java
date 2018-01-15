package com.sensetime.motionsdksamples.Http;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangnannan on 2017/10/31.
 */

public class OkHttpUrlMessageThread extends Thread {
    public static final String TAG = "OkHttpUrlMessageThread";
    public Context mContext;
    public HandlerThread mHandlerThread;
    public urlHandler urlHandler;
    public okCallback okCallback;

    public OkHttpUrlMessageThread(Context context) {
        this.mContext = context;
        mHandlerThread = new HandlerThread("okhttpurlmessagethread");
        mHandlerThread.start();
        urlHandler = new urlHandler(mHandlerThread.getLooper());

    }

    private class urlHandler extends Handler {
        private static final int GETFACECOOD = 2;
        private static final int GETVOICECOOD = 3;

        public urlHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETFACECOOD:
                    getFaceRequestUrlData();
                    break;
                case GETVOICECOOD:
                    getVoiceUrlRequestData();
                    break;
            }
        }
    }

    public void setFaceRequestUrlData() {
        urlHandler.sendEmptyMessage(urlHandler.GETFACECOOD);
    }

    public void getFaceRequestUrlData() {
        String url = "https://192.168.50.65:8888/get_data?question=美女";
        processRequestData(url);
    }

    private void processRequestData(String url) {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        OkHttpClient okHttpClient = mBuilder.build();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "IOException~~~" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] responseBytes = response.body().bytes();
                    String responseUrl = new String(responseBytes, "UTF-8");
                    Log.e(TAG,"responseUrl~~"+responseUrl);
                    okCallback.onSuccess(response);
                }
            }
        });
    }

    public void setVoiceUrlRequestData() {
        urlHandler.sendEmptyMessage(urlHandler.GETVOICECOOD);
    }

    public void getVoiceUrlRequestData() {
        String url = "";
        processRequestData(url);
    }

    public interface okCallback {
        public void onSuccess(Response response);

        public void onFailure();
    }

    public void setDataOnCallBack(okCallback onCallBack) {
        this.okCallback = onCallBack;
    }

    private class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }



}
