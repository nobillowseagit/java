package com.sensetime.motionsdksamples.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sensetime.motionsdksamples.Uart.DecimalConversionUtils;
import com.sensetime.motionsdksamples.Uart.HexUtils;
import com.sensetime.motionsdksamples.Uart.UartServer;
import com.socks.library.KLog;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Created by lyt on 2017/10/11.
 */

public class MotionServer implements IMotion {
    private static final int MAN_EYE_DISTANCE = 62;
    private static final int WOMAN_EYE_DISTANCE = 58;

    private static MotionServer instance = new MotionServer();

    public static MotionServer getInstance() {
        return instance;
    }

    private boolean mScanStarted = false;
    private boolean mFaceLocationUpdated;
    private int mNoFaceLocationCount = 0;
    private int mMotorType = 1;

    public enum CallbackState {
        FOUND_MAJOR_FACE, FOUND_LEFT_FACE, FOUND_RIGHT_FACE, COMPLETED_TRUN
    }

    Rectangle mFaceRectangle = new Rectangle(0, 0, 0, 0);
    Rectangle mSceneRectangle = new Rectangle(0, 0, 0, 0);
    float mScale;
    int mX;
    int mY;
    float mAngle;
    boolean mRunning;
    public int mMotor1Direction;
    public int mMotor1Angle;
    public int mMotor2Direction;
    public int mMotor2Angle;
    public int mMotor3Direction;
    public int mMotor3Angle;
    public FaceLocation mFaceLocation;

    private MotionHandler mMotionHandler;
    private MotionCallback mMotionCallback;

    private FindLeftFaceThread mFindLeftFaceThread;
    private FindRightFaceThread mFindRightFaceThread;

    private UartServer mUartServer;
    private DecimalConversionUtils mDecimalConversionUtils;

    private MyTimer mTimer;
    private MotionTimerCallback mMotionTimerCallback;

    //external
    public MotionServer() {
        //super("motion-server");

        mMotionHandler = new MotionHandler();
        mFindLeftFaceThread = new FindLeftFaceThread();
        //mFindRightFaceThread = new FindRightFaceThread();

        mFaceLocation = new FaceLocation();
        Rectangle face = new Rectangle(2, 1, 2, 1);
        Rectangle scene = new Rectangle(720, 0, 1280, 0);
        mFaceLocation.mRecFace = face;
        mFaceLocation.mRightFaceRec = face;
        mFaceLocation.mLeftFaceRec = face;
        mSceneRectangle = scene;

        mUartServer = UartServer.getInstance();
        mDecimalConversionUtils = DecimalConversionUtils.getInstance();
        mDecimalConversionUtils.initializeserialPort();
        mDecimalConversionUtils.setOnDataReceiveLintener(new DecimalConversionUtils.OnDataReceiveLintener() {
            @Override
            public void OndataReceive(byte[] bytes) {
                Log.e("MotionServer~~~",bytes.toString());
                String receive = HexUtils.bytes2HexString(bytes,bytes.length);
                Log.e("MotionServer~~~",receive);
            }
        });

        mTimer = new MyTimer(5);  //0.5s
        mMotionTimerCallback = new MotionTimerCallback();
        mTimer.setCallback(mMotionTimerCallback);
        mTimer.start();

        mFaceLocationUpdated = false;
    }

    public void setMotorType(int motorType) {
        mMotorType = motorType;
    }

    public void setMotionCallback(MotionCallback callback) {
        mMotionCallback = callback;
    }

    public void turnLeft() {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("01", "00", 10);
    }

    public void turn2Left() {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("02", "00", 10);
    }

    public void turn3Left() {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "00", 10);
    }

    public void turn3Right() {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "01", 10);
    }

    public void turn3Left(int degree) {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "00", degree);
    }

    public void turn3Right(int degree) {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "01", degree);
    }

    public void turnRight() {
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("01", "00", 10);
    }

    public void shakeHead() {
        stopMotion();
        mDecimalConversionUtils.setHeadMethod();
    }

    public void nodHead() {
        stopMotion();
        mDecimalConversionUtils.setNodMethod();
    }


    //internal
    public float calcScale(Rectangle face, Rectangle scene) {
        mScale = scene.area / face.area;
        return mScale;
    }

    public void calcMotion() {
        mX = (int) ((mFaceRectangle.centerX - mSceneRectangle.centerX) * mScale);
        mY = (int) ((mFaceRectangle.centerY - mSceneRectangle.centerY) * mScale);
    }

    public void calcMotion(Rectangle face, Rectangle scene) {
        float scale = calcScale(face, scene);
        mX = (int) ((face.centerX - scene.centerX) * scale);
        mY = (int) ((face.centerY - scene.centerY) * scale);
    }

    private void xnstep() {
        //String f2 = DecimalConversionUtils.setSingleStepMotorRotationMethod("00", "00");
        //mUartServer.mReceiveAndSendUtils.sendTurnMethod(f2);
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("02", "00", 5);

    }

    private void xpstep() {
        //String f2 = DecimalConversionUtils.setSingleStepMotorRotationMethod("00", "00");
        //mUartServer.mReceiveAndSendUtils.sendTurnMethod(f2);
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("02", "01", 5);
    }

    private void ynstep() {
        //String f2 = DecimalConversionUtils.setSingleStepMotorRotationMethod("00", "00");
        //mUartServer.mReceiveAndSendUtils.sendTurnMethod(f2);
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "00", 5);
    }

    private void ypstep() {
        //String f2 = DecimalConversionUtils.setSingleStepMotorRotationMethod("00", "00");
        //mUartServer.mReceiveAndSendUtils.sendTurnMethod(f2);
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("03", "01", 5);
    }

    public void trackFaceMotion(Rectangle face, Rectangle scene) {
        String string = "移动;";

        /*
        if (0 == face.right || 0 == face.bottom) {
            if (!mScanStarted) {
                KLog.i("lijia motion: 开始巡检");
                mScanStarted = true;
                DecimalConversionUtils.setCursorModeMethod();
            }
            return;
        } else {
            KLog.i("lijia motion: 停止巡检");
            DecimalConversionUtils.stopCursorModeMethod();
        }
        */

        float scale = calcScale(face, scene);  //1280x720  center: 640, 360

        if (face.centerX > (1280 / 2 - 100) && face.centerX < (1280 / 2 + 100)) {
            KLog.e("lijia; X 在中心");
        } else {
            mX = (int) ((face.centerX - scene.centerX) * scale);

            //mY = (int) ((face.centerY - scene.centerY) * scale);
            if (mX >= (scene.right / 2)) {
                string += "向左转";
                xpstep();
            } else {
                string += "向右转";
                xnstep();
            }
        }

        if (face.centerY > (720 / 2 - 100) && face.centerY < (720 / 2 + 100)) {
            KLog.e("lijia; Y 在中心");
        } else {
            mY = (int) ((face.centerY - scene.centerY) * scale);

            if (mY >= (scene.bottom / 2)) {
                string += " 向下转";
                ypstep();
            } else {
                string += " 向上转";
                ynstep();
            }
        }

        //KLog.i("lijia" + "scene_right" + scene.right + "," + "scene_bottom:" + scene.bottom);
        KLog.i("lijia " + "scale:" + scale + "," +
                "center_x:" + face.centerX + "," + "center_y:" + face.centerY + "," +
                "face_left:" + face.left + "," + "face_right:" + face.right + "," + "face_top:" + face.top + "," + "face_bottom:" + face.bottom + "," +
                "x:" + mX + "," + "y:" + mY);
        KLog.i("lijia " + string);
    }

    private void trackFaceMotion() {
        FaceLocation faceLocation = getFaceLocation();
        Rectangle face = faceLocation.mRecFace;
        Rectangle scene = faceLocation.mRecScene;
        String string = "移动;";

        if (faceLocation.mRightFaceRec.centerX > (1280 / 2 - 100) && faceLocation.mLeftFaceRec.centerX < (1280 / 2 + 100)) {
            KLog.i("lijia face already in the center");
            return;
        }

        float scale = calcScale(face, scene);  //1280x720  center: 640, 360
        mX = (int) ((face.centerX - scene.centerX) * scale);
        mY = (int) ((face.centerY - scene.centerY) * scale);
        if (mX >= (scene.right / 2)) {
            string += "向左转";
            xpstep();
        } else {
            string += "向右转";
            xnstep();
        }

        if (mY >= (scene.bottom / 2)) {
            string += " 向下转";
            ypstep();
        } else {
            string += " 向上转";
            ynstep();
        }
        //KLog.i("lijia" + "scene_right" + scene.right + "," + "scene_bottom:" + scene.bottom);
        KLog.i("lijia " + "scale:" + scale + "," +
                "center_x:" + face.centerX + "," + "center_y:" + face.centerY + "," +
                "face_left:" + face.left + "," + "face_right:" + face.right + "," + "face_top:" + face.top + "," + "face_bottom:" + face.bottom + "," +
                "x:" + mX + "," + "y:" + mY);
        KLog.i("lijia " + string);
    }

    public synchronized FaceLocation getFaceLocation() {
        return mFaceLocation;
    }

    public synchronized void setFaceLocation(FaceLocation faceLocation) {
        mFaceLocation = faceLocation;
        setFaceLocationUpdated(true);
    }

    public void calcMotion(float angle) {
        mAngle = angle;

        mMotor1Direction = 1;
        mMotor1Angle = 60;
    }

    public void execMotion(int type) {
        if (type == 1) {
            //sendRequest(type, motor1, direction, angle);
        } else if (type == 2) {
            //sendRequest(type, motor2, direction, angle);
            //sendRequest(type, motor3, direction, angle);
        }
        //waitResult();
    }

    public void turnMotor1() {

    }

    private double calcEyeDistance(FaceLocation faceLocation) {
        double z;
        int delta_x = abs(faceLocation.mRightEye.x - faceLocation.mLeftEye.x);
        int delta_y = abs(faceLocation.mRightEye.y - faceLocation.mRightEye.y);
        z = sqrt(delta_x * delta_x + delta_y * delta_y);
        z = faceLocation.mEysDist;
        KLog.i("lijia" + "eye_distance:" + z);
        return z;
    }


    /*
    @Override
    public void run() {
        KLog.trace();
        //执行耗时操作
        while (isRunning) {
            //count();
        }
    }
    */

    /*
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MsgBase msgBase) {

        if (msgBase.type != MSG_TYPE_MOTION) {
            return;
        }

        MotionMsg msg = (MotionMsg)msgBase;
        switch (msg.mCmd) {
            case MOTION_CMD_FACE:
                handleFaceLocation(msg.mFaceLocation);
                break;
            case MOTION_CMD_SOUND:
                handleSoundLocation(msg.mSoundLocation);
                break;
        }
    }
    */

    private synchronized void handleFaceLocation(FaceLocation faceLocation) {
        trackFaceMotion(faceLocation.mRecFace, faceLocation.mRecScene);
        calcEyeDistance(faceLocation);
    }

    private synchronized void handleSoundLocation(SoundLocation soundLocation) {
        //calcMotion();
    }

    private synchronized void handleFaceTrack(FaceLocation faceLocation) {
        trackFaceMotion(faceLocation.mRecFace, faceLocation.mRecScene);
    }

    public void calcSoundLocation(SoundLocation soundLocation) {
        handleSoundLocation(soundLocation);
    }

    public void calcFaceLocation(FaceLocation faceLocation) {
        startFaceLocation();
        handleFaceLocation(faceLocation);
    }

    private void startFaceLocation() {

    }

    public void reqFindLeft() {
        //DecimalConversionUtils.setCursorModeMethod();
        stopMotion();
        mDecimalConversionUtils.setCursorModeMethod();
        mFindLeftFaceThread.start();
        //start find motion
    }

    public void resFindLeft() {

    }

    public void reqFindRight() {
        //String ff = DecimalConversionUtils.setSplicingParameterHead("ff", "00", "00", "01", 270);
        //mUartServer.mReceiveAndSendUtils.sendTurnMethod(ff);
        stopMotion();
        mDecimalConversionUtils.setSplicingParameterHead("01", "00", 10);

        mFindRightFaceThread = new FindRightFaceThread();
        mFindRightFaceThread.start();
        //start right motion
    }

    public void resFindRight() {

    }

    public void stopMotion() {
        if (2 == mMotorType) {
            mDecimalConversionUtils.stopMotor();
        }
    }

    public void startFull() {
        Message msg = Message.obtain();
        mMotionHandler.sendMessage(msg);
    }

    public void completeFull() {

    }

    class MotionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int cmd = bundle.getInt("cmd");
            switch (cmd) {
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    }

    public class FindLeftFaceThread extends Thread {
        @Override
        public void run() {
            int count = 0;
            while (true) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                FaceLocation faceLocation = getFaceLocation();

                if (faceLocation.mLeftFaceRec.centerX > (1280 / 2 - 100) &&
                        faceLocation.mLeftFaceRec.centerX < (1280 / 2 + 100)) {
                    KLog.i("lijia motion: found left face");
                    stopMotion();
                    mMotionCallback.onMotionComplete(CallbackState.FOUND_LEFT_FACE);
                    break;
                }

                count++;
                if (count >= 6) {
                    count = 0;
                    KLog.i("lijia motion: emulated found left face");
                    stopMotion();
                    mMotionCallback.onMotionComplete(CallbackState.FOUND_LEFT_FACE);
                    break;
                }
            }
        }
    }

    int mFindRightCount = 0;

    public class FindRightFaceThread extends Thread {
        @Override
        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FaceLocation faceLocation = getFaceLocation();

            if (faceLocation.mRightFaceRec.centerX > ((1280 / 3) * 2)) {
                KLog.i("lijia motion: found right face");
                stopMotion();
                mMotionCallback.onMotionComplete(CallbackState.FOUND_RIGHT_FACE);
            } else {
                //emulate
                mFindRightCount++;
                if (mFindRightCount >= 3) {
                    mFindRightCount = 0;
                    KLog.i("lijia motion: emulated found right face");
                    stopMotion();
                    mMotionCallback.onMotionComplete(CallbackState.FOUND_RIGHT_FACE);
                } else {
                    KLog.i("lijia motion: turn 10");
                    stopMotion();
                    mMotionCallback.onMotionComplete(CallbackState.COMPLETED_TRUN);
                }
            }
        }
    }

    public class FindFaceThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                FaceLocation faceLocation = getFaceLocation();
                if (faceLocation.mRightFaceRec.centerX > (1280 / 2 - 100) && faceLocation.mLeftFaceRec.centerX < (1280 / 2 + 100)) {
                    KLog.i("lijia: main face in center");
                    stopMotion();
                    mMotionCallback.onMotionComplete(CallbackState.FOUND_MAJOR_FACE);
                } else {
                    trackFaceMotion();
                }
            }
        }
    }

    public interface MotionCallback {
        public void onMotionComplete(CallbackState state);
    }

    private synchronized void setFaceLocationUpdated(boolean b) {
        mFaceLocationUpdated = b;
    }

    private synchronized boolean getFaceLocationUpdated() {
        return mFaceLocationUpdated;
    }

    private void scan() {
        if (mFaceLocationUpdated) {
            setFaceLocationUpdated(false);
            if (mScanStarted) {
                KLog.i("lijia motion: 停止巡检");
                mScanStarted = false;
                //DecimalConversionUtils.stopCursorModeMethod();
                mDecimalConversionUtils.stopMotor();


            }
            FaceLocation faceLocation = getFaceLocation();
            trackFaceMotion(faceLocation.mRecFace, faceLocation.mRecScene);
            calcEyeDistance(faceLocation);
        } else {
            mNoFaceLocationCount++;
            if (mNoFaceLocationCount > 10) {
                mNoFaceLocationCount = 0;
                //巡视
                if (!mScanStarted) {
                    KLog.i("lijia motion: 开始巡检");
                    mScanStarted = true;
                    stopMotion();
                    //DecimalConversionUtils.setCursorModeMethod();
                    mDecimalConversionUtils.setCursorModeMethod();
                }
            }
        }
    }

    public class MotionTimerCallback implements MyTimer.TimerCallback {

        @Override
        public void onTimeout() {
            mTimer.reset();
            scan();
        }
    }
}
