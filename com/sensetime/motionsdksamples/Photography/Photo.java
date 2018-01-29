package com.sensetime.motionsdksamples.Photography;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.stitch.Stitch;
import com.sensetime.motionsdksamples.Common.FaceAttrFragment;
import com.sensetime.motionsdksamples.Common.MotionServer;
import com.sensetime.motionsdksamples.Common.MyTimer;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.Speech.SpeechServer;
import com.sensetime.motionsdksamples.Utils.BitmapUtil;
import com.sensetime.motionsdksamples.Utils.UniqueId;
import com.socks.library.KLog;

/**
 * Created by lyt on 2017/11/21.
 */

public class Photo extends ServerThread {
    private static Photo instance = new Photo();
    public static Photo getInstance() {
        return instance;
    }

    private static final int FULL_START = 1;
    private static final int TURN_COMPLETED = 2;
    private static final int PHOTO_COMPLETED = 3;
    private static final int FULL_COMPLETED = 4;
    private static final int PHOTO_SINGLE_COMPLETED = 5;

    private SpeechServer mSpeechServer;

    private PhotoCompletedCallback mFullCompletedCallback;
    private PhotoSingleCallback mPhotoSingleCallback;

    private PhotoMotionCallback mPhotoMotionCallback;
    private PhotoFsm mPhotoFsm;

    Bitmap bitmapA;
    Bitmap bitmapB;
    Bitmap bitmapC;
    String pathA;
    String pathB;
    String pathC;
    int slot;
    private Stitch stitch;

    private FaceAttrFragment mFaceAttrFragment;
    private Handler mCameraHandler;

    MotionServer mMotionServer;

    boolean mFullStarted;
    boolean mTakeCompleted;
    private List<Bitmap> mListBitmap;
    private List<String> mListPath;

    private PhotoThread mThread;
    private PhotoHandler mHandler;

    public Photo() {
        super("photo");
        stitch = new Stitch();

        mMotionServer = MotionServer.getInstance();
        mPhotoMotionCallback = new PhotoMotionCallback();
        mMotionServer.setMotionCallback(mPhotoMotionCallback);

        mSpeechServer = SpeechServer.getInstance();

        mThread = new PhotoThread("photo");
        mThread.start();
        mHandler = new PhotoHandler(mThread.getLooper());

        mListBitmap = new ArrayList<Bitmap>();
        mListPath = new ArrayList<String>();

        mPhotoFsm = new PhotoFsm();
        mPhotoFsm.start();

        clear();
    }

    public void init(FaceAttrFragment faceAttrFragment) {
        mFaceAttrFragment = faceAttrFragment;
        mCameraHandler = mFaceAttrFragment.getHandler();
    };

    public void setPhotoCompletedCallback(PhotoCompletedCallback callback) {
        mFullCompletedCallback = callback;
    }

    public void setPhotoSingleCallback(PhotoSingleCallback callback) {
        mPhotoSingleCallback = callback;
    }

    public void clear() {
        bitmapA = null;
        bitmapB = null;
        bitmapC = null;
        pathA = null;
        pathB = null;
        pathC = null;
        slot = 0;
        mTakeCompleted = true;
        mFullStarted = false;

        mListBitmap.clear();
        mListPath.clear();
    }

    public void start() {
        clear();
    }

    public synchronized int getSlot() {
        return slot;
    }

    public synchronized void setSlot() {
        if (0 == slot) {
            slot = 1;
        } else {
            slot = 0;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (0 == getSlot()) {
            bitmapA = bitmap;
            pathA = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";
            savePic(bitmapA, pathA);
        } else {
            bitmapB = bitmap;
            pathB = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";
            savePic(bitmapB, pathB);
        }
        setSlot();
    }

    public synchronized void setBitmap2(Bitmap bitmap) {
        //mListBitmap.add(bitmap);
        String path = "/sdcard/Download/" + "image1.jpg";
        BitmapUtil.saveBitmap(bitmap, path);
        //savePic(bitmap, path);
        //mListPath.add(path);

        /*
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("cmd", PHOTO_SINGLE_COMPLETED);
        msg.setData(b);
        mHandler.sendMessage(msg);
        */

        //mFullCompletedCallback.onPhotoSingleCompleted();
        mPhotoSingleCallback.onCompleted();
    }

    public synchronized void setBitmap3(Bitmap bitmap) {
        mListBitmap.add(bitmap);
        String path = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";
        BitmapUtil.saveBitmap(bitmap, path);
        //savePic(bitmap, path);
        mListPath.add(path);
        takePicCompleted();

        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("cmd", PHOTO_COMPLETED);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    private synchronized void takePicCompleted() {
        mTakeCompleted = true;
    }

    public void setPath(String path) {
        if (0 == getSlot()) {
            pathA = path;
        } else {
            pathB = path;
        }
        setSlot();
    }

    public void processBitmap() {

    }

    public void process() {
        if (null == pathA || null == pathB) {
            return;
        }

        pathC = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";

        stitch.run(pathA, pathB, pathC);

        //debug
        //delPic(pathA);
        //delPic(pathB);

        pathA = pathC;
        slot = 1;
        pathB = null;
        pathC = null;

        bitmapA = bitmapC;
        bitmapB = null;
        bitmapC = null;
    }

    public void process3() {
        String resPath = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";
        stitch.run(mListPath, resPath);
    }

    public void savePic(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delPic(String path) {
        File file = new File(path);
        file.delete();
    }

    public synchronized void setFullStart(boolean b) {
        mFullStarted = b;
    }

    public synchronized void takePicA() {
        KLog.i("lijia shotting");
        mSpeechServer.reqSingleTts("正在拍摄中");
        //FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
        Message msg = new Message();
        msg.what = 2;
        mCameraHandler.sendMessage(msg);
    }

    public void shot() {
        takePicA();
    }

    public void startFull() {
        if (mFullStarted) {
            KLog.i("lijia full photo already started");
            return;
        }
        KLog.i("lijia start full photo");
        clear();
        setFullStart(true);

        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("cmd", FULL_START);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public synchronized void takePic() {
        KLog.i("lijia shotting");
        mTakeCompleted = false;
        mSpeechServer.reqSingleTts("正在拍摄中");
        FaceAttrFragment.MyHandler handler = mFaceAttrFragment.getHandler();
        Message msg = new Message();
        handler.sendMessage(msg);
    }

    public void shotComplete() {

    }

    private int findLeft() {
        mMotionServer.reqFindLeft();
        return 1;
    }

    private int findRight() {
        mMotionServer.reqFindRight();
        return 0;
    }

    public void findRightComplete() {
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("cmd", FULL_COMPLETED);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void turnComplete() {
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("cmd", TURN_COMPLETED);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public class PhotoHandler extends Handler {
        public PhotoHandler(Looper looper) {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            switch (b.getInt("cmd")) {
                case FULL_START:
                    findLeft();
                    break;
                case TURN_COMPLETED:
                    takePic();
                    break;
                case PHOTO_COMPLETED:
                    findRight();
                    break;
                case FULL_COMPLETED:
                    fullComplete();
                    break;
            }
        }
    }

    public void fullComplete() {
        KLog.i("lijia full photo complete");
        process3();
        mSpeechServer.reqSingleTts("拍摄完成");
        setFullStart(false);
        mFullCompletedCallback.onPhotoCompleted();
    }

    public interface PhotoCompletedCallback {
        public void onPhotoCompleted();
    }

    public interface PhotoSingleCallback {
        public void onCompleted();
    }


    public class PhotoMotionCallback implements MotionServer.MotionCallback {
        @Override
        public void onMotionComplete(MotionServer.CallbackState state) {
            switch (state) {
                case FOUND_LEFT_FACE:
                    turnComplete();
                    break;
                case COMPLETED_TRUN:
                    turnComplete();
                    break;
                case FOUND_RIGHT_FACE:
                    findRightComplete();
                    break;
            }
        }
    }

    public class PhotoThread extends HandlerThread {
        private boolean mRunnable;

        public PhotoThread(String name) {
            super(name);
        }

        public Looper getLooper() {
            return super.getLooper();
        }
    }
}
