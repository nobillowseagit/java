package com.sensetime.motionsdksamples.Uart;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: wangnannan
 * date: 2017/11/27 16:18
 * desc: TODO
 */

public class DecimalConversionUtils {
    private final static String TAG = DecimalConversionUtils.class.getSimpleName();
    //两个电机是1:4，一个电机是1:3
    //01是3:1,02和03是4:1
    private double MULTIPLEFOUR = (1.8 / 128);
    private double MULTIPLETHREE = (1.8 / 96);
    private final int CRUISEMODE = 1;
    private final long SECOND = 1000;
    private ReceiveAndSendUtils mReceiveAndSendUtils = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CRUISEMODE:
                    Bundle bundle = (Bundle) msg.obj;
                    String motorId = bundle.getString("motorid");
                    String derection = bundle.getString("derection");
                    setTrackModeMethod(motorId, derection);
                    mHandler.removeMessages(CRUISEMODE);
                    sendTrackMessageDelayOneSecind(motorId, derection);
                    Log.e(TAG, "~~~~~~~~~~~~~~~~~~~~~~" + motorId);
                    break;
            }
        }
    };

    public static DecimalConversionUtils getInstance() {

        return SingleHolder.mDecimalConversionUtils;
    }

    //转为16进制
    public String formattingH(int a) {
        return Integer.toHexString(a);
    }

    //得到进的步数
    //需要修改
    public int getRoundMothod(String motorId, int degrees) {
        double remainder = 0;
        if (motorId.equals("02") || motorId.equals("03")) {
            remainder = (double) (degrees / MULTIPLEFOUR);
        } else if (motorId.equals("01")) {
            remainder = (double) (degrees / MULTIPLETHREE);
        }
        DecimalFormat mDecimalFormat = new DecimalFormat("0");
        String remainderNumber = mDecimalFormat.format(remainder);
        Log.e(TAG, "remainderNumber~~" + remainderNumber);
        return Integer.valueOf(remainderNumber);
    }

    //得到转动的16进制角度
    //还需要得到小数的步数暂未处理
    public String getSplicedCharacter(String motorId, int number) {
        String sixteenDecimalSystemNumber = formattingH(getRoundMothod(motorId, number));
        Log.e(TAG, "sixteenDecimalSystemnumber~~" + sixteenDecimalSystemNumber);
        if (sixteenDecimalSystemNumber.length() == 1) {
            return "0" + sixteenDecimalSystemNumber;
        } else if (sixteenDecimalSystemNumber.length() == 2) {
            return sixteenDecimalSystemNumber;
        } else if (sixteenDecimalSystemNumber.length() == 3) {
            return "0" + sixteenDecimalSystemNumber;
        } else if (sixteenDecimalSystemNumber.length() == 4) {
            return sixteenDecimalSystemNumber;
        }
        return "";
    }

    //上传的数据

    public String getSplicingParameterHead(String motorType, String rotationdirection, int rotationAngle) {
        String turnMseeage;
        turnMseeage = "ff" + motorType + rotationdirection + setAgentMode("00") + setNoDefinitionMethod(motorType, rotationAngle) + getSplicedCharacter(motorType, rotationAngle);
        return turnMseeage;
    }

    //16进制转化为10进制得到步数
    public int decodeSixteenMothod(String angle) {
        int tenNumber = Integer.parseInt(angle, 16);
        return tenNumber;

    }

    //得到转动的角度
    public String getAngleMethod(int steps) {
        double doubleStep = steps * MULTIPLEFOUR;
        DecimalFormat mDecimalFormat = new DecimalFormat("0");
        String angle = mDecimalFormat.format(doubleStep);
        return angle;
    }

    //正则匹配是否为整数是
    public boolean isInteger(String str) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(str);
        return mer.find();
    }

    //判断传递的角度数据是否为整数
    public void separationNumberMothod(String angleNumber) {
        if (TextUtils.isEmpty(angleNumber)) {
            return;
        }
        if (isInteger(angleNumber)) {
            //是整数不做处理
            Log.e(TAG, "angleNumber~~~" + angleNumber);
        } else {
            //根据小数点进行截取
            int num = angleNumber.indexOf(".");
            Log.e(TAG, "num~~" + num);
            String integer = angleNumber.substring(0, num);
            Log.e(TAG, "integer~~~" + integer);
            String decimal = angleNumber.substring(num, angleNumber.length());
            decimal = "0" + decimal;
            Log.e(TAG, "decimal~~~" + decimal);
        }
    }

    //机器人模式
    public String setAgentMode(String agentMode) {
        if (TextUtils.isEmpty(agentMode)) {

            return "00";
        }
        return agentMode;
    }

    //暂无定义
    public String setNoDefinitionMethod(String motorId, int number) {
        String turnAngle = getSplicedCharacter(motorId, number);
        //判断长度
        if (TextUtils.isEmpty(turnAngle)) {
            return "00000000";
        } else if (turnAngle.length() == 1) {
            return "0000000";
        } else if (turnAngle.length() == 2) {
            return "000000";
        } else if (turnAngle.length() == 3) {
            return "00000";
        } else if (turnAngle.length() == 4) {
            return "0000";
        }
        return "";
    }

    //根据余下的字节数判断
    public String noDefinitionBytes(String motorId, int angelNumber) {
        String noDefinitionMessage = setNoDefinitionMethod(motorId, angelNumber);
        if (noDefinitionMessage.length() == 6) {
            //无任何占位
            return SetOneMessageMethod("00") + SetTwoMessageMethod("00") + setThreeMessageMethod("00");
        } else if (noDefinitionMessage.length() == 4) {
            //占位16进制一字节
            return SetOneMessageMethod("00") + SetTwoMessageMethod("00");
        } else if (noDefinitionMessage.length() == 2) {
            //占位16进制二字节
            return SetOneMessageMethod("00");
        } else if (TextUtils.isEmpty(noDefinitionMessage) || noDefinitionMessage.length() == 0) {
            //全部占用
            return "";
        }
        return "";
    }

    //第一个默认字符
    public String SetOneMessageMethod(String oneMessage) {

        return oneMessage;
    }

    //第二个默认字符
    public String SetTwoMessageMethod(String twoMessage) {

        return twoMessage;
    }

    //第三个默认字符
    public String setThreeMessageMethod(String threeMessage) {

        return threeMessage;
    }

    //跟踪模式
    public void setTrackModeMethod(String motorId, String derection) {
        getReceiveAndSendInstate();
        //需要判断电机比例
        if (motorId.equals("02") || motorId.equals("03")) {
            //1:4
            mReceiveAndSendUtils.sendTurnMethod("ff" + motorId + derection + "00" + "000002c7");
        } else if (motorId.equals("01")) {
            //1:3
            mReceiveAndSendUtils.sendTurnMethod("ff" + motorId + derection + "00" + "00000215");
        }
        sendTrackMessageDelayOneSecind(motorId, derection);

    }

    public void sendTrackMessageDelayOneSecind(String motorId, String derection) {
        final Bundle bundle = new Bundle();
        bundle.putString("motorid", motorId);
        bundle.putString("derection", derection);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(CRUISEMODE, bundle).sendToTarget();
            }
        }, SECOND);
    }

    //移除跟踪
    public void stopTrackModeMethod() {
        mHandler.removeMessages(CRUISEMODE);
        mHandler.removeCallbacksAndMessages(null);
    }

    //某个电机单步转动

    /**
     * @param motorId   电机id
     * @param direction 方向
     * @return
     */
    public String getSingleStepMotorRotationMethod(String motorId, String direction) {
        //需要判断电机比例
        if (motorId.equals("02") || motorId.equals("03")) {
            //1:4
            return "ff" + motorId + direction + "00" + "000002c7";
        } else if (motorId.equals("01")) {
            //1:3
            return "ff" + motorId + direction + "00" + "00000215";
        }
        return "ff" + "00" + "00" + "00" + "00000000";
    }

    //全景拍照模式

    public String getPanoramaModeMethod() {
        return "ff" + "00" + "00" + "02" + "00000000";
    }

    //巡航模式
    public String getCursorModeMethod() {

        return "ff" + "00" + "00" + "01" + "00000000";
    }

    //暂停电机
    public String getPauseMotorMethod() {

        return "ff" + "00" + "00" + "05" + "00000000";
    }

    //点头模式
    public String getNodMotorMethod() {

        return "ff" + "00" + "00" + "03" + "00000000";
    }

    //摇头方法
    public String getHeadMotorMethod() {

        return "ff" + "00" + "00" + "04" + "00000000";
    }

    //受控模式
    public void setSplicingParameterHead(String motorType, String rotationdirection, int rotationAngle) {
        //stopMotor();
        //在发送数据'
        Log.e(TAG, "setSplicingParameterHead~~~" + getSplicingParameterHead(motorType, rotationdirection, rotationAngle));
        if (motorType.equals("01")) {
            if (rotationAngle >= 0 && rotationAngle <= 30) {
                sendSplicingParameterHead(motorType, rotationdirection, rotationAngle);
            } else {
                Log.e(TAG, "01 电机的转动角度在 0~~~30 度之间");
            }
        } else if (motorType.equals("02")) {
            if (rotationAngle >= 0 && rotationAngle <= 45) {
                sendSplicingParameterHead(motorType, rotationdirection, rotationAngle);
            } else {
                Log.e(TAG, "02 电机的转动角度在 0~~45度之间");
            }
        } else if (motorType.equals("03")) {
            if (rotationAngle >= 0 && rotationAngle <= 135) {
                sendSplicingParameterHead(motorType, rotationdirection, rotationAngle);
            } else {
                Log.e(TAG, "03 电机的转动角度在0~~~135度之间");
            }
        } else {
            Log.e(TAG, "电机参数错误");
        }

    }

    private void sendSplicingParameterHead(String motorType, String rotationdirection, int rotationAngle) {
        mReceiveAndSendUtils.sendTurnMethod(getSplicingParameterHead(motorType, rotationdirection, rotationAngle));
    }


    public void stopMotor() {
        getReceiveAndSendInstate();
        //先停止
        Log.e(TAG, "getPauseMotorMethod~~~" + getPauseMotorMethod());
        mReceiveAndSendUtils.sendTurnMethod(getPauseMotorMethod());
    }

    private void getReceiveAndSendInstate() {
        if (mReceiveAndSendUtils == null) {
            mReceiveAndSendUtils = ReceiveAndSendUtils.getInstance();
        }
    }

    //巡航模式
    public void setCursorModeMethod() {
        //stopMotor();
        Log.e(TAG, "getCursorModeMethod~~~~" + getCursorModeMethod());
        mReceiveAndSendUtils.sendTurnMethod(getCursorModeMethod());
    }

    //全景拍照模式
    public void setPanoramaModeMethod() {
        //stopMotor();
        Log.e(TAG, "getPanoramaModeMethod~~~~" + getPanoramaModeMethod());
        mReceiveAndSendUtils.sendTurnMethod(getPanoramaModeMethod());
    }

    //点头方法
    public void setNodMethod() {
        Log.e(TAG, "getNodMotorMethod~~~~" + getNodMotorMethod());
        //stopMotor();
        mReceiveAndSendUtils.sendTurnMethod(getNodMotorMethod());
    }

    //摇头方法
    public void setHeadMethod() {
        //stopMotor();
        Log.e(TAG, "getHeadMotorMethod~~~~" + getHeadMotorMethod());
        mReceiveAndSendUtils.sendTurnMethod(getHeadMotorMethod());
    }

    //初始化串口
    public void initializeserialPort() {
        getReceiveAndSendInstate();
        mReceiveAndSendUtils.initializeVariable();
        mReceiveAndSendUtils.OpenSerialPort(new File("/dev/ttyS1"), 9600);
        mReceiveAndSendUtils.setCallBackMethod(new ReceiveAndSendUtils.dataCallback() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (datalintener != null) {
                    datalintener.OndataReceive(bytes);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    //回调
    private OnDataReceiveLintener datalintener;

    public interface OnDataReceiveLintener {
        void OndataReceive(byte[] bytes);
    }

    public void setOnDataReceiveLintener(OnDataReceiveLintener lintener) {
        this.datalintener = lintener;
    }

    public static class SingleHolder {
        public static DecimalConversionUtils mDecimalConversionUtils = new DecimalConversionUtils();
    }
}
