package com.sensetime.motionsdksamples.Uart;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;

/**
 * Created by wangnannan on 2017/11/21.
 * 创建列队发送和接受数据
 */

public class ReceiveAndSendUtils implements OnOpenSerialPortListener {
    public final String TAG = "ReceiveAndSendUtils";

    public HandlerThread mHandlerThread;

    public ControlHandler mControlHandler;

    public SerialPortManager mSerialPortManager;

    public boolean isOpenSerialPort;

    public dataCallback mDataCallback;

    public static ReceiveAndSendUtils getInstance() {

        return SingleHolder.mReceiveAndSendUtils;
    }

    //初始化变量
    public void initializeVariable() {
        mHandlerThread = new HandlerThread("receive");
        mHandlerThread.start();
        mControlHandler = new ControlHandler(mHandlerThread.getLooper());
        mSerialPortManager = new SerialPortManager();
    }

    @Override
    public void onSuccess(File device) {
        Log.e(TAG, "devices open success");
    }

    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                Log.e(TAG, "No access to read and write");
                break;
            case OPEN_FAIL:
            default:
                Log.e(TAG, " Serial port failed");
                break;
        }

    }

    //创建循环模式
    public class ControlHandler extends Handler {
        private final int SENDTURNMETHOD = 1;

        public ControlHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDTURNMETHOD:
                    String mStringBuffer = (String) msg.obj;
                    getTurnMethodData(mStringBuffer);
                    break;
            }
        }
    }

    //打开串口

    /**
     * @param device   串口名称
     * @param baudRate 波特率
     *                 值不变
     */
    public void OpenSerialPort(File device, int baudRate) {
        isOpenSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this).setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {

                Log.e(TAG, "Get the data");
                mDataCallback.onSuccess(bytes);

            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.e(TAG, "Send Data ing");

            }

        }).openSerialPort(device, baudRate);
        Log.e(TAG, isOpenSerialPort ? "open Success" : "open Fail");
    }

    //发送数据到tty32
    public void sendMessageToMachine(final String message) {
        mControlHandler.post(new Runnable() {
            @Override
            public void run() {
                byte[] hexBytes = HexUtils.HexString2Bytes(message);
                boolean sendSuccessOrFail = mSerialPortManager.sendBytes(hexBytes);
                Log.e(TAG, sendSuccessOrFail ? "Success" : "Fail");
            }
        });

    }

    //接口接收数据
    public interface dataCallback {
        public void onSuccess(byte[] bytes);

        public void onFailure();
    }

    //设置接口
    public void setCallBackMethod(dataCallback callback) {
        this.mDataCallback = callback;
    }

    //关闭串口
    public void closeSerialPort() {
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
            //变成null后需要重新初始化
        }
    }

    //发送数据
    public void sendTurnMethod(String message) {
        mControlHandler.obtainMessage(mControlHandler.SENDTURNMETHOD, message).sendToTarget();
    }

    //拼接参数
    //  buffer    header valus
    public void getTurnMethodData(String turnMessage) {
        //根据head和value截取
        sendMessageToMachine(turnMessage);

    }

    //停止所有的handler

    public void pauseAllMethod() {
        mControlHandler.removeCallbacksAndMessages(null);
    }

    public static class SingleHolder{
        public static final ReceiveAndSendUtils mReceiveAndSendUtils = new ReceiveAndSendUtils();
    }

}
