package com.sensetime.motionsdksamples.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensetime.faceapi.StAttributeResult;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceAttribute;
import com.sensetime.faceapi.StFaceConfig;
import com.sensetime.faceapi.StFaceDetector;
import com.sensetime.faceapi.StFaceException;
import com.sensetime.faceapi.StFaceFeature;
import com.sensetime.faceapi.StFaceOrientation;
import com.sensetime.faceapi.StFaceTrack;
import com.sensetime.faceapi.StFaceVerify;
import com.sensetime.faceapi.StRect;
import com.sensetime.motionsdksamples.Dialog.DialogServer;
import com.sensetime.motionsdksamples.EventBusUtils.ServerThread;
import com.sensetime.motionsdksamples.R;
import com.sensetime.motionsdksamples.UiMsg;
import com.sensetime.motionsdksamples.Utils.BitmapUtil;
import com.sensetime.motionsdksamples.Utils.FileUtils;
import com.sensetime.motionsdksamples.Utils.ObjectUtils;
import com.sensetime.motionsdksamples.Utils.UniqueId;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.sensetime.motionsdksamples.Common.FaceAttrFragment.FaceMode.FACE_MODE_ATTR;
import static com.sensetime.motionsdksamples.Common.FaceAttrFragment.FaceMode.FACE_MODE_NO;
import static com.sensetime.motionsdksamples.Common.FaceAttrFragment.FaceMode.FACE_MODE_VERIFY;
import static java.lang.Thread.State.TERMINATED;

public class FaceAttrFragment extends BaseCameraFragment implements BitmapUtil.PrepareLicenseAsyncTask.LicenseResultListener {
    private static final String TAG = BaseCameraFragment.class.getSimpleName();

    private float FACE_SIMILAR_FACTOR = new Float(0.85);
    private static final int FACE_VERIFY_COUNT = 2;
    private static final int FACE_ATTR_COUNT = 2;
    private static final int FACE_ATTR_SIMILAR = 1;
    private static final int FACE_ATTR_NOT_SIMILAR = 0;
    private static final int MSG_TIME_OUT = 1;
    private static final int FACE_TIME_OUT = 30000;
    public static final String ATTR_MODEL_NAME = "attribute.model";
    public static final String VERIFY_MODEL_NAME = "verify.model";

    private StFaceAttribute mStFaceAttribute = null;
    private StFaceTrack mFaceAttrTracker = null;
    private StFaceVerify mVerify = null;
    //private StFaceDetector mFaceDetector = null;

    private int mDetectConfig;
    private int mFaceAttrCount;
    private int mFaceVerifyCount;
    private StFaceFeature mCurrentFeature = new StFaceFeature();
    private StFaceFeature mPreviousFeature = new StFaceFeature();

    private Context mContext;
    private Person mCurrentPerson = new Person();
    private FaceLocation mCurrentFaceLocation = new FaceLocation();
    private FaceAttr mCurrentFaceAttr = new FaceAttr();
    private FaceAttr mPreviousFaceAttr = new FaceAttr();

    private FaceServer mFaceServer;
    private InfoServer mInfoServer;
    private MotionServer mMotionServer;

    private MyHandler mHandler;

    private ProcessServer mProcessServer;
    private ProcessServerCallback mProcessServerCallback;
    private ProcessTask mProcessTask;

    private LooperThread mLooperThread = new LooperThread();

    private boolean mProcessCompleted;


    public enum FaceMode {
        FACE_MODE_NO, FACE_MODE_ATTR, FACE_MODE_VERIFY
    }
    FaceMode mFaceMode = FACE_MODE_NO;

    //UI debug
    //private EditText mEditText;
    //private EditText mETInfo;
    //private EditText mETRes;
    //private InfoServer mInfoServer;
    //private int mMode;

    @Override
    public void onLicenseInitSuccess() {

    }

    @Override
    public void onLicenseInitFailed(String errorMessage) {

    }

    /*
    private synchronized FaceMode configFace(FaceMode mode) {
        if (mFaceMode != mode) {
            mFaceMode = mode;
            switch (mFaceMode) {
                case FACE_MODE_NO:
                    break;
                case FACE_MODE_ATTR:
                    //preAttr();
                    break;
                case FACE_MODE_VERIFY:
                    //preVerify();
                    break;
            }
        }
        return  mFaceMode;
    }
    */

    private void preAttr() {
        copyModel(ATTR_MODEL_NAME);

        try {
            mStFaceAttribute = new StFaceAttribute(FileUtils.getModelPath(getActivity(), ATTR_MODEL_NAME));
        } catch (StFaceException e) {
            e.printStackTrace();
        }
    }

    private void preVerify() {
        copyModel(VERIFY_MODEL_NAME);

        try {
            mVerify = new StFaceVerify(FileUtils.getModelPath(getActivity(), VERIFY_MODEL_NAME));
        } catch (StFaceException e) {
            e.printStackTrace();
        }
    }

    private StFace mStFace[] = new StFace[3];
    private Rectangle mRecFace[] = new Rectangle[3];

    private void findFace(StFace faces[]) {
        int max_area = 0, min_left = 10000, max_right = 0;
        int index_max_area = 0;
        int index_min_left = 0;
        int index_max_right = 0;
        StRect rect;
        Rectangle recFace;

        for (int i = 0; i < faces.length; i++ ) {
            rect = faces[i].getFaceRect();
            recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);

            if (recFace.area > max_area) {
                max_area = recFace.area;
                index_max_area = i;
            }

            if (recFace.centerX < min_left) {
                min_left = recFace.centerX;
                index_min_left = i;
            }

            if (recFace.centerX > max_right) {
                max_right = recFace.centerX;
                index_max_right = i;
            }
        }

        mStFace[0] = faces[index_max_area];
        mStFace[1] = faces[index_min_left];
        mStFace[2] = faces[index_max_right];
    }

    private void runMotion(Bitmap bitmap, StFace faces[]) {
        KLog.debug("lijia runMotion enter");
        Rectangle recScene = new Rectangle(bitmap.getHeight(), 0, bitmap.getWidth(), 0);
        //findFace(faces);

        /*
        int max_area = 0, min_left = 10000, max_right = 0;
        int index_max_area = 0;
        int index_min_left = 0;
        int index_max_right = 0;
        StRect rect, max_area_rect, min_left_rect, max_right_rect;
        Rectangle recFace = null, max_area_rec, min_left_rec, max_right_rec;

        for (int i = 0; i < faces.length; i++ ) {
            rect = faces[i].getFaceRect();
            recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);

            if (recFace.area > max_area) {
                max_area = recFace.area;
                index_max_area = i;
            }

            if (recFace.centerX < min_left) {
                min_left = recFace.centerX;
                index_min_left = i;
            }

            if (recFace.centerX > max_right) {
                max_right = recFace.centerX;
                index_max_right = i;
            }
        }

        FaceLocation faceLocation = new FaceLocation();
        faceLocation.mRecScene = recScene;

        rect = faces[index_max_area].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mRecFace = recFace;
        faceLocation.mEysDist = faces[index_max_area].getEyeDist();

        rect = faces[index_min_left].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mLeftFaceRec = recFace;

        rect = faces[index_max_right].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mRightFaceRec = recFace;
        */

        FaceLocation faceLocation = new FaceLocation();
        faceLocation.mRecScene = recScene;

        StRect rect;
        Rectangle recFace;

        rect = mStFace[0].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mRecFace = recFace;
        faceLocation.mEysDist = mStFace[0].getEyeDist();

        rect = mStFace[1].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mLeftFaceRec = recFace;

        rect = mStFace[2].getFaceRect();
        recFace = new Rectangle(rect.bottom, rect.left, rect.right, rect.top);
        faceLocation.mRightFaceRec = recFace;

        mCurrentFaceLocation = faceLocation;

        //interactive with MotionServer
        mMotionServer.setFaceLocation(mCurrentFaceLocation);
        //mMotionServer.calcFaceLocation(mCurrentFaceLocation);

        /*
        MotionMsg msg = new MotionMsg();
        msg.mFaceLocation = faceLocation;
        msg.type = MSG_TYPE_MOTION;
        msg.mCmd = MOTION_CMD_FACE;
        EventBus.getDefault().post(msg);
        */
    }

    private void runAttr(Bitmap bitmap, StFace faces[]) {
        if (faces != null && faces.length != 0) {
            startGetAttribute(bitmap, faces);
        }
    }

    private void runVerify(Bitmap bitmap, StFace faces[]) {
        StFaceFeature feature = null;
        float result = 0, max_result = 0;
        int max_index = 0;

        if (faces != null && faces.length != 0) {
            //KLog.debug("lijia: face detected");

            //String path = "/sdcard/Download/" + UniqueId.getStrUid() + ".jpg";
            //BitmapUtil.saveBitmap(bitmap, path);

            try {
                mCurrentFeature = mVerify.getFeature(bitmap, faces[0]);
            } catch (StFaceException e) {
                e.printStackTrace();
            }

            if (mCurrentFeature.equals(mPreviousFeature)) {
            } else {
                mPreviousFeature = mCurrentFeature;
            }
            if (mCurrentFeature != null && mPreviousFeature != null) {
                try {
                    result = mVerify.compareFeature(mCurrentFeature, mPreviousFeature);
                    //UI debug
                    //mETRes.setText(Float.toString(result));
                    if (result > FACE_SIMILAR_FACTOR) {
                        mFaceVerifyCount++;
                        if (mFaceVerifyCount >= FACE_VERIFY_COUNT) {
                            //runMotion(bitmap, faces);
                            runMotion(bitmap, mStFace);

                            //get all person info
                            List<Person> listPersons = mInfoServer.getAllPerson();
                            if (null != listPersons) {
                                max_index = -1;
                                max_result = 0;
                                for (int i = 0; i < listPersons.size(); i++) {
                                    try {
                                        feature = (StFaceFeature) ObjectUtils.getObjectFromBytes(listPersons.get(i).mFeatureByte);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (mCurrentFeature != null && feature != null) {
                                        result = mVerify.compareFeature(mCurrentFeature, feature);
                                        if (result > max_result && result > FACE_SIMILAR_FACTOR) {
                                            max_result = result;
                                            max_index = i;
                                        }
                                    }
                                }

                                //数据库中有此人特征
                                if (max_index >= 0) {
                                    //UI debug
                                    //mETRes.append(",found in database");
                                    mCurrentPerson = listPersons.get(max_index);
                                    //setCurrentFeature(mFeature);
                                } else {  //数据库中无此人，新建
                                    //UI debug
                                    //mETRes.append(",new person");

                                    //interactive with InfoServer
                                    Person person = new Person();
                                    person.setFeatureByte(mCurrentFeature);
                                    //setCurrentFeature(mFeature);
                                    mCurrentPerson = person;
                                    //mInfoServer = InfoServer.getInstance();
                                    //mInfoServer.setCurrentPerson(person);
                                }

                                //get face attr
                                //configFace(FACE_MODE_ATTR);
                                //runAttr(bitmap, faces);
                                runAttr(bitmap, mStFace);
                            }
                            else {  //Database is null, new person
                                Person person = new Person();
                                person.setFeatureByte(mCurrentFeature);
                                //person.setFeatureByte(getCurrentFeature());
                                //setCurrentFeature(mFeature);
                                mCurrentPerson = person;
                                //configFace(FACE_MODE_ATTR);
                                //runAttr(bitmap, faces);
                                runAttr(bitmap, mStFace);
                            }
                        }
                    } else {
                        mFaceVerifyCount = 0;
                        //UI debug
                        //mETRes.append("other person");
                    }
                } catch (StFaceException e) {
                    //mErrorMessage = e.getLocalizedMessage();
                }
            }
        } else {
            mFaceVerifyCount = 0;
            //UI debug
            //mETRes.setText("empty");
        }
    }

    private synchronized StFaceFeature getCurrentFeature() {
        return mCurrentFeature;
    }

    private synchronized void setCurrentFeature(StFaceFeature feature) {
        mCurrentFeature = feature;
    }

    private synchronized Person getCurrentPerson() {
        return mCurrentPerson;
    }

    private synchronized void setCurrentPerson(Person person) {
        mCurrentPerson = person;
    }

    private void copyModel(String modelName) {
        try {
            mModelPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + modelName;
            copyModelInAssets(modelName, mModelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public void resumeFaceDetector() {
        mDetectConfig = StFaceConfig.ST_DETECT_ENABLE_ALIGN_21;
        if (mFaceDetector == null) {
            try {
                //mStFaceAttribute = new StFaceAttribute(FileUtils.getModelPath(getActivity(), ATTR_MODEL_NAME));
                //mFaceAttrTracker = new StFaceTrack(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21 | StFaceConfig.ST_DETECT_ANY_FACE);
                mFaceDetector = new StFaceDetector(null, mDetectConfig | StFaceConfig.ST_DETECT_ANY_FACE);
            } catch (StFaceException e) {
                e.printStackTrace();
                //showDialog("Fail to Create Body Tracker", e.getMessage());
            }
        }
    }

    public void pauseFaceDetector() {
        if (mFaceDetector != null) {
            mFaceDetector.release();
            mFaceDetector = null;
        }
    }
*/

    private void preTrack() {
        if (mFaceAttrTracker == null) {
            try {
                //mStFaceAttribute = new StFaceAttribute(FileUtils.getModelPath(getActivity(), ATTR_MODEL_NAME));
                mFaceAttrTracker = new StFaceTrack(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21 | StFaceConfig.ST_DETECT_ANY_FACE);
            } catch (StFaceException e) {
                e.printStackTrace();
                //showDialog("Fail to Create Body Tracker", e.getMessage());
            }
        }
    }

    public void resumeFaceTracker() {
        if (mFaceAttrTracker == null) {
            try {
                //mStFaceAttribute = new StFaceAttribute(FileUtils.getModelPath(getActivity(), ATTR_MODEL_NAME));
                mFaceAttrTracker = new StFaceTrack(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21 | StFaceConfig.ST_DETECT_ANY_FACE);
            } catch (StFaceException e) {
                e.printStackTrace();
                //showDialog("Fail to Create Body Tracker", e.getMessage());
            }
        }

        if (mStFaceAttribute == null) {
            preAttr();
        }

        if (mVerify == null) {
            preVerify();
        }
    }

    public void pauseFaceTracker() {
        if (mFaceAttrTracker != null) {
            mFaceAttrTracker.release();
            mFaceAttrTracker = null;
        }
    }

    public Bitmap Bytes2Bimap(byte[] data) {
        ByteArrayOutputStream baos;
        byte[] rawImage;
        Bitmap bitmap;

        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return bitmap;
    }

    /**
     * 根据置信度获取主情绪<br>
     * get the main emotion description base on emotion scores
     *
     * @param emotionScores
     *            情绪置信度数组 <br>
     *            emotion score array
     * @return 置信度最高的情绪表达<br>
     *         the description of main emotion
     */
    private String getMainEmotion(int[] emotionScores) {
        String emotion = null;
        int maxFlag = 0;
        int maxScore = emotionScores[0];
        String[] emotionsStrings = getResources().getStringArray(R.array.emotion_type);
        for (int i = 1; i < emotionScores.length; i++) {
            if (emotionScores[i] > 0 && maxScore < emotionScores[i]) {
                maxScore = emotionScores[i];
                maxFlag = i;
            }
        }
        if (maxScore != 0) {
            emotion = emotionsStrings[maxFlag];
        }
        return emotion;
    }

    private int getMainEmotionIndex(int[] emotionScores) {
        String emotion = null;
        int maxFlag = 0;
        int maxScore = emotionScores[0];
        String[] emotionsStrings = getResources().getStringArray(R.array.emotion_type);
        for (int i = 1; i < emotionScores.length; i++) {
            if (emotionScores[i] > 0 && maxScore < emotionScores[i]) {
                maxScore = emotionScores[i];
                maxFlag = i;
            }
        }
        return maxFlag;
    }

    /**
     * 根据阈值过滤人脸属性内容，sample中设置阈值为50，详细的设置方法可以参考白皮书<br>
     * filter the attribute string based on threshold, now the threshold is set
     * to 50, the detail of threshold refer to white paper
     *
     * @param result
     *            人脸属性结果 <br>
     *            the attribute result object
     * @return 过滤后的人脸属性内容<br>
     *         the string format of attribute
     */
    private String getAttributeString(StAttributeResult result) {
        //Person person = new Person();
        String emotion = null;
        int[] emotionScores = new int[] { result.getAngryScore(), result.getCalmScore(), result.getConfusedScore(),
                result.getDisgustScore(), result.getHappyScore(), result.getSadScore(), result.getScaredScore(),
                result.getSurprisedScore(), result.getSquintScore(), result.getScreamScore() };
        emotion = getMainEmotion(emotionScores);

        String resultString = String.format(getString(R.string.age), result.getAge())+getString(R.string.comma)+
                (result.getGenderMaleScore() > 50 ? getString(R.string.male) : getString(R.string.female)) + getString(R.string.comma)
                + String.format(getString(R.string.attractive), result.getAttrActive())
                + (result.getSmileScore() > 50 ?  getString(R.string.comma)+getString(R.string.smile): "")
                + (result.getEyeGlassScore() > 50 ? getString(R.string.comma)+getString(R.string.eyeglass) : "")
                + (result.getSunGlassScore() > 50 ? getString(R.string.comma)+getString(R.string.sunglass): "")
                + (result.getMaskScore() > 50 ? getString(R.string.comma)+getString(R.string.mask) : "") + getString(R.string.comma)
                + (result.getRace() == 0 ? getString(R.string.yellowrace): (result.getRace() == 1 ? getString(R.string.blackrace) : getString(R.string.whiterace)))
                + (result.getEyeOpenScore() > 50 ? getString(R.string.comma)+getString(R.string.eye_open) : "")
                + (result.getMouthOpenScore() > 50 ? getString(R.string.comma)+getString(R.string.mouth_open) : "")
                + (result.getBeardScore() > 50 ? getString(R.string.comma)+getString(R.string.bear) : "") + "\n"
                + String.format(getString(R.string.emotion), emotion);

        String gender = null;
        if (result.getGenderMaleScore() > 50) {
            gender = getString(R.string.male) ;
        } else {
            gender = getString(R.string.female) ;
        }
        /*
        person.setGender(tmp);
        person.setAge(result.getAge());
        person.setEmotion(emotion);
        */
        mCurrentFaceAttr.mGender = gender;
        mCurrentFaceAttr.mAge = result.getAge();
        mCurrentFaceAttr.mEmotion = emotion;

        /*
        switch (getMainEmotionIndex(emotionScores)) {
            case 0:
            {
                mPerson.setEmotion(Person.Emotion.ANGER);
            }
            case 4:
            {
                mPerson.setEmotion(Person.Emotion.HAPPY);
            }
            default:
                mPerson.setEmotion(Person.Emotion.CALM);
        }
        */

        return resultString;
    }

    private void startGetAttribute(Bitmap bm, StFace[] faces) {
        if(faces != null && faces.length > 0) {
            //for(int i = 0; i < faces.length ; i++) {
            for(int i = 0; i < 1 ; i++) {
                StRect cvFaceRect = faces[i].getFaceRect();
                Rect facerect = new Rect(cvFaceRect.left,cvFaceRect.top,cvFaceRect.right,cvFaceRect.bottom);
                //从源图像中抠出人脸图片
                // crop the face from image
                Bitmap cropFace = BitmapUtil.getCropBitmap(bm,facerect);
                //获取属性值
                //get the attribute for each face
                StAttributeResult result = null;
                try {
                    result = mStFaceAttribute.attribute(bm, faces[i]);
                } catch (StFaceException e) {
                    e.printStackTrace();
                }
                //根据属性结果过滤显示内容
                //get the attribute string after filter base on threshold
                String resultString = getAttributeString(result);
                if (FACE_ATTR_SIMILAR != similarFaceAtrr(mCurrentFaceAttr, mPreviousFaceAttr)) {
                    mPreviousFaceAttr = mCurrentFaceAttr;
                    mFaceAttrCount = 0;
                } else {
                    mFaceAttrCount++;
                    if ((mFaceAttrCount >= FACE_ATTR_COUNT) && (mFaceVerifyCount >= FACE_VERIFY_COUNT)) {
                        if (mCurrentPerson.mAge == 0) {
                            mCurrentPerson.mAge = mCurrentFaceAttr.mAge;
                        }
                        if (mCurrentPerson.mGender.equals("未知性别")) {
                            mCurrentPerson.mGender = mCurrentFaceAttr.mGender;
                        }
                        mCurrentPerson.mEmotion = mCurrentFaceAttr.mEmotion;
                        //UI debug
                        //mETInfo.setText(resultString);
                        mCurrentFaceAttr.mStrId = mCurrentPerson.mStrUid;
                        mCurrentFaceAttr.mAge = mCurrentPerson.mAge;
                        mCurrentFaceAttr.mGender = mCurrentPerson.mGender;
                        //mCurrentFaceAttr.mEmotion = mCurrentPerson.mEmotion;

                        //interactive with FaceServer
                        mFaceServer.setFaceAttr(mCurrentFaceAttr);
                        mFaceServer.setCurrentPerson(mCurrentPerson);
                        KLog.i("lijia detected a fixed person" +
                                mCurrentPerson.mName + mCurrentPerson.mGender + mCurrentPerson.mAge);
                    }
                }
            }
        } else {
            //UI debug
            //mETInfo.setText("No Face detected");
        }
    }

    public void onPreviewFrameFaceTracker(byte[] data) {
        if (mFaceAttrTracker != null) {
            // The camera's preview format is set to ImageFormat.NV21,
            // the data shall be nv21 format, so we pass PixelFormat.NV21 here
            try {
                int orientation = getMotionOrientation();

                //orientation = StFaceOrientation.ST_FACE_UP;
                Bitmap bitmap = Bytes2Bimap(data);
                //KLog.debug("lijia" + );
                //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                StFace[] faces = mFaceAttrTracker.track(bitmap, orientation);
                //StFace[] faces = mFaceDetector.detect(bitmap, orientation);

                if (null != faces && 0 != faces.length) {
                    findFace(faces);

                    //Canvas canvas = mOverlapSurfaceView.getHolder().lockCanvas();
                    // Clear the canvs
                    //canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                    /*
                    configFace(FACE_MODE_VERIFY);
                    switch (mFaceMode) {
                        case FACE_MODE_ATTR:
                            runAttr(bitmap, faces);
                            break;
                        case FACE_MODE_VERIFY:
                            runVerify(bitmap, faces);
                            break;
                    }
                    */
                    //runVerify(bitmap, faces);
                    runVerify(bitmap, mStFace);
                    //mStFace
                    //mOverlapSurfaceView.getHolder().unlockCanvasAndPost(canvas);
                }
            } catch (StFaceException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    private void drawBodies(Canvas canvas, Body[] bodies) {
        if (canvas == null) {
            return;
        }
        canvas.setMatrix(getMatrix());
        Paint paint = new Paint();

        for (Body body : bodies) {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            int rotate = getRotate();
            float[] keypoints = null;
            if (rotate == 90) {
                keypoints = StUtils.rotateDeg90(body, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT);
            } else if (rotate == 270) {
                keypoints = StUtils.rotateDeg270(body, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT);
            } else {
                keypoints = body.getKeypoints();
            }

            float[] keypointScores = body.getKeypointScores();

            float[] mappedPoints = new float[keypoints.length];
            for (int i = 0; i < keypoints.length; i += 2) {
                // The preview for frontal camera is mirrored image
                mappedPoints[i] = keypoints[i];
                mappedPoints[i + 1] = CAMERA_PREVIEW_WIDTH - keypoints[i + 1];
                // Draw keypoints of detected body
                if (keypointScores[i / 2] > KEY_POINT_THRESHOLD) {
                    canvas.drawCircle(mappedPoints[i], mappedPoints[i + 1], 20, paint);
                }
            }

            // Draw skeletons
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(20);
            paint.setStyle(Paint.Style.STROKE);

            int limbs[][] = {{0, 1}, {2, 3}, {3, 4}, {5, 6}, {6, 7}, {8, 9}, {9, 10}, {11, 12}, {12, 13}, {2, 5}, {8, 11}};

            for (int i = 0; i < limbs.length; i++) {
                int a = limbs[i][0];
                int b = limbs[i][1];
                if (keypointScores[a] > KEY_POINT_THRESHOLD && keypointScores[b] > KEY_POINT_THRESHOLD) {
                    canvas.drawLine(mappedPoints[a * 2], mappedPoints[a * 2 + 1],
                            mappedPoints[b * 2], mappedPoints[b * 2 + 1], paint);
                }
            }
        }
    }
    */

    private int similarFaceAtrr(FaceAttr currentFaceAttr, FaceAttr previousFaceAttr) {
        if (Math.abs(currentFaceAttr.mAge - previousFaceAttr.mAge) > 5 ||
                !currentFaceAttr.mGender.equals(previousFaceAttr.mGender) ||
                !currentFaceAttr.mEmotion.equals(previousFaceAttr.mEmotion)) {
            return FACE_ATTR_NOT_SIMILAR;
        }
        return FACE_ATTR_SIMILAR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initHandler();
        //configFace(FACE_MODE_VERIFY);
        preVerify();
        preAttr();
        preTrack();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeFaceTracker();
        //resumeFaceDetector();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseFaceTracker();
        //pauseFaceDetector();
    }

    @Override
    protected void onPreviewFrame(byte[] data) {
        if (mProcessCompleted) {
            setProcessCompleted(false);
            mProcessServer = new ProcessServer();
            mProcessServer.setData(data);
            mProcessServer.start();
        }
    }

    private void init() {
        setProcessCompleted(true);

        mFaceServer = FaceServer.getInstance();
        mInfoServer = InfoServer.getInstance();
        mMotionServer = MotionServer.getInstance();
    }

    public MyHandler getHandler() {
        return mHandler;
    }

    public void initHandler() {
        mHandler = new MyHandler();
        //mThread = new MyThread();
        //new Thread(mThread).start();
    }

    public class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            // 此处可以更新UI

            if (2 == msg.what) {
                takePicture(2);
            } else {
                Bundle b = msg.getData();
                String color = b.getString("color");
                takePicture(1);
            }
        }
    }

    public class MyThread implements Runnable {
        public void run() {

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("color", "我的");
            msg.setData(b);
            mHandler.sendMessage(msg); // 向Handler发送消息，更新UI
        }
    }

    class LooperThread extends Thread {
        public Handler mLooperHandler;

        public LooperThread() {
            mLooperHandler = new LooperHandler();
        }

        public void run() {
            Looper.prepare();
            Looper.loop();
        }
    }

    class LooperHandler extends Handler {
        public void handleMessage(Message msg) {
            // process incoming messages here
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            onPreviewFrameFaceTracker(data);
        }
    }



    public synchronized void setProcessCompleted (boolean b) {
        mProcessCompleted = b;
    }

    public class ProcessServer extends Thread {
        private byte[] mData;

        public void setData(byte[] data) {
            mData = data;
        }

        @Override
        public void run() {
            onPreviewFrameFaceTracker(mData);
            setProcessCompleted(true);
        }
    }

    public class ProcessServerCallback implements ServerThread.ServerCallback {

        @Override
        public void onTimeout() {

        }

        @Override
        public void onComplete() {
            setProcessCompleted(true);
        }

    }

    private class ProcessTask extends AsyncTask<String, Integer, String> {
        byte[] mData;

        public void setData(byte[] data) {
            mData = data;
        }

        @Override
        protected String doInBackground(String... strings) {
            setProcessCompleted(false);
            onPreviewFrameFaceTracker(mData);
            setProcessCompleted(true);
            return null;
        }
    }
}
