package com.sensetime.motionsdksamples.Common;

import com.sensetime.motionsdksamples.Dialog.DialogServer;

/**
 * Created by lyt on 2017/10/24.
 */

public class FaceServer implements IFace {
    private static FaceServer instance = new FaceServer();
    public static FaceServer getInstance() {
        return instance;
    }

    FaceLocation mCurrentFaceLocation;
    FaceAttr mCurrentFaceAttr;
    Person mCurrentPerson;
    MyTimer mTimer;
    FaceCallback mFaceCallback;
    DialogServer mDialogServer = null;

    public FaceServer() {
        mCurrentPerson = new Person();
        mCurrentFaceAttr = new FaceAttr();
        mCurrentFaceLocation = new FaceLocation();
        mDialogServer = DialogServer.getInstance();

        mTimer = new MyTimer(600);
        mFaceCallback = new FaceCallback();
        mTimer.setCallback(mFaceCallback);
    }

    @Override
    public FaceLocation getFaceLocation() {
        return _getFaceLocation();
    }

    @Override
    public FaceAttr getFaceAttr() {
        return _getFaceAttr();
    }

    public void setFaceLoaction(FaceLocation faceLocation) {
        _setFaceLocation(faceLocation);
    }

    public void setFaceAttr(FaceAttr faceAttr) {
        _setFaceAttr(faceAttr);
    }

    private synchronized void _setFaceAttr(FaceAttr faceAttr) {
        mCurrentFaceAttr = faceAttr;
    }

    private synchronized FaceAttr _getFaceAttr() {
        return mCurrentFaceAttr;
    }

    private synchronized FaceLocation _getFaceLocation() {
        return mCurrentFaceLocation;
    }

    private synchronized void _setFaceLocation(FaceLocation faceLocation) {
        mCurrentFaceLocation = faceLocation;
    }

    public synchronized Person getCurrentPerson() {
        if (null == mCurrentPerson) {
            mCurrentPerson = new Person();
        }
        return mCurrentPerson;
    }

    public void setCurrentPerson(Person person) {
        mTimer.start();
        _setCurrentPerson(person);
        //mDialogServer.enterVisionTriggerSync();
        /*
        if (PERSON_NOT_SIMILAR == mCurrentPerson.similar(person))
        {
            mDialogServer.enterVisionTriggerSync();
        }
        */
    }

    public synchronized void _setCurrentPerson(Person person) {
        mCurrentPerson = person;
    }

    public class FaceCallback implements MyTimer.TimerCallback {
        @Override
        public void onTimeout() {
            mTimer.stop();

            Person person = new Person();
            _setCurrentPerson(person);

            FaceAttr faceAttr = new FaceAttr();
            _setFaceAttr(faceAttr);
        }
    }
}

