package com.sensetime.motionsdksamples.Uart;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kongqw.serialportlibrary.SeriaPortBufferManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataBufferListener;

import java.io.File;

/**
 * author: wangnannan
 * date: 2017/11/22 16:30
 * desc: TODO
 */

public class ReceiveAndSendBufferUtils implements OnOpenSerialPortListener {
    public final String TAG = "ReceiveAndSendBufferUtils";

    public static final ReceiveAndSendBufferUtils mReceiveAndSendBufferUtils = new ReceiveAndSendBufferUtils();

    public HandlerThread mHandlerThread;

    public ControlHandler mControlHandler;

    public SeriaPortBufferManager mSeriaPortBufferManager;

    public boolean isOpenSerialPort;

    public dataCallback mDataCallback;

    public static ReceiveAndSendBufferUtils getInstance() {

        return mReceiveAndSendBufferUtils;
    }

    //初始化变量
    public void initializeVariable() {
        mHandlerThread = new HandlerThread("receive");
        mHandlerThread.start();
        mControlHandler = new ControlHandler(mHandlerThread.getLooper());
        mSeriaPortBufferManager = new SeriaPortBufferManager();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSuccess(File device) {
        Log.e(TAG, "devices open success");

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onFail(File device, OnOpenSerialPortListener.Status status) {
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

        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDTURNMETHOD:
                    StringBuffer mStringBuffer = (StringBuffer) msg.obj;
                    Log.e(TAG, "Handler~~~" + mStringBuffer.toString());
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
    @SuppressLint("LongLogTag")
    public void OpenSerialPort(File device, int baudRate) {
        isOpenSerialPort = mSeriaPortBufferManager.setOnOpenSerialPortListener(this).setOnSerialPortDataListener(new OnSerialPortDataBufferListener() {
            @Override
            public void onDataReceived(final StringBuffer buffer) {
                Log.e(TAG, "Get the data");
                mControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataCallback.onSuccess(buffer);
                    }
                });

            }

            @Override
            public void onDataSent(StringBuffer buffer) {
                Log.e(TAG, "Send Data ing");
            }

        }).openSerialPort(device, baudRate);
        Log.e(TAG, isOpenSerialPort ? "open Success" : "open Fail");
    }

    //发送数据到tty32
    public void sendMessageToMachine(final StringBuffer message) {
        mControlHandler.post(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                boolean sendSuccessOrFail = mSeriaPortBufferManager.sendBufferData(message);
                Log.e(TAG, sendSuccessOrFail ? "Success" : "Fail");
            }
        });

    }

    //接口接收数据
    public interface dataCallback {
        public void onSuccess(StringBuffer mStringBuffer);

        public void onFailure();
    }

    //设置接口
    public void setCallBackMethod(dataCallback callback) {
        this.mDataCallback = callback;
    }

    //关闭串口
    public void closeSerialPort() {
        if (null != mSeriaPortBufferManager) {
            mSeriaPortBufferManager.closeSerialPort();
            mSeriaPortBufferManager = null;
            //变成null后需要重新初始化
        }
    }

    //转动命令

    /**
     * @param fixedvalue        固定值
     * @param direction         位置
     * @param motorSerialNumber 电机的序列号
     * @param angle             角度
     */
    @SuppressLint("LongLogTag")
    public void sendTurnMethod(String fixedvalue, String direction, String motorSerialNumber, String angle) {
        StringBuffer mStringBuffer = new StringBuffer();
        mStringBuffer.append(fixedvalue).append(direction).append(motorSerialNumber).append(angle);
        Log.e(TAG, mStringBuffer.toString());
        mControlHandler.obtainMessage(mControlHandler.SENDTURNMETHOD, mStringBuffer).sendToTarget();
    }

    //拼接参数
    //  buffer    header valus
    public void getTurnMethodData(StringBuffer turnMessage) {
        //根据head和value截取
        sendMessageToMachine(turnMessage);

    }

    //停止所有的handler
    public void pauseAllMethod() {
        mControlHandler.removeCallbacksAndMessages(null);
    }
}
