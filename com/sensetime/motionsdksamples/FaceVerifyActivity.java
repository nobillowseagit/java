package com.sensetime.motionsdksamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.https.utils.HTTPSUtils;
import com.sensetime.faceapi.StAttributeResult;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceAttribute;
import com.sensetime.faceapi.StFaceConfig;
import com.sensetime.faceapi.StFaceDetector;
import com.sensetime.faceapi.StFaceException;
import com.sensetime.faceapi.StFaceFeature;
import com.sensetime.faceapi.StFaceTrack;
import com.sensetime.faceapi.StFaceVerify;
import com.sensetime.faceapi.StRect;
import com.sensetime.motionsdksamples.Common.BaseCameraFragment;
import com.sensetime.motionsdksamples.Utils.BitmapUtil;
import com.sensetime.motionsdksamples.Utils.Config;
import com.sensetime.motionsdksamples.Utils.FileUtils;
import com.sensetime.motionsdksamples.Common.Person;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class FaceVerifyActivity extends BaseCameraFragment {

    private static final String TAG = BaseCameraFragment.class.getSimpleName();

    private StFaceAttribute mStFaceAttribute = null;
    private StFaceTrack mFaceVerifyTracker = null;
    private StFaceVerify mVerify = null;
    private StFaceDetector mDetector = null;

    public static final String VERIFY_MODEL_NAME = "verify.model";

    String mRes;

    Config mConfig;

    Person mPerson = new Person();

    private void copyFaceTrackerModel() {
        try {
            // Copy model from assets to internal storage
            String modelName = VERIFY_MODEL_NAME;
            mModelPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + modelName;
            copyModelInAssets(modelName, mModelPath);
        } catch (IOException e) {
            e.printStackTrace();
            //showDialog("Fail to Copy Model", e.getMessage());
        }
    }

    public void resumeFaceTracker() {
        if (mFaceVerifyTracker == null) {
            try {
                //mStFaceAttribute = new StFaceAttribute(FileUtils.getModelPath(getActivity(), MODEL_NAME));
                mFaceVerifyTracker = new StFaceTrack(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21 | StFaceConfig.ST_DETECT_ANY_FACE);
                mVerify = new StFaceVerify(FileUtils.getModelPath(getActivity(), VERIFY_MODEL_NAME));
                mDetector = new StFaceDetector(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21|StFaceConfig.ST_DETECT_ANY_FACE);
            } catch (StFaceException e) {
                e.printStackTrace();
                //showDialog("Fail to Create Body Tracker", e.getMessage());
            }
        }
    }

    public void pauseFaceTracker() {
        if (mFaceVerifyTracker != null) {
            mFaceVerifyTracker.release();
            mFaceVerifyTracker = null;
        }

        if (mVerify != null) {
            mVerify.release();
            mVerify = null;
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

        String tmp = new String();
        if (result.getGenderMaleScore() > 50) {
            tmp = getString(R.string.male) ;
        } else {
            tmp = getString(R.string.female) ;
        }
        mPerson.setGender(tmp);
        mPerson.setEmotion(emotion);

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
        if(faces != null && faces.length >0) {
            for(int i = 0; i < faces.length ; i++) {
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
                //显示attribute结果
                //show the attribute result
                //View listViewItem = LayoutInflater.from(this).inflate(R.layout.listview_item_layout, null);
                //ImageView imageView = (ImageView)listViewItem.findViewById(R.id.image);
                //TextView textView = (TextView)listViewItem.findViewById(R.id.resultText);
                //imageView.setImageBitmap(cropFace);
                //textView.setText(resultString);
                //mResultLinearLayout.addView(listViewItem);

                //mtextViewInfo.setText(resultString);
                //mTextViewRes.setText(mRes);

                /*
                String gender;
                if (result.getGenderMaleScore() > 50) {
                    gender = "帅哥";
                } else {
                    gender = "美女";
                }
                */
                if(mPerson.mUpdated) {
                    getHttpsHtml(mPerson.getGender() + mPerson.getEmotion());
                }
            }
        } else {
            //mtextViewInfo.setText("No Face detected");
        }
    }

    public void getHttpsHtml(String question) {

        mConfig = ((MainActivity) getActivity()).getConfig();

        Request request = new Request.Builder()
                .url("https://192.168.50.65:8888/get_data?question="+question)
                .build();

        HTTPSUtils httpsUtils = new HTTPSUtils(getContext());
        httpsUtils.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("--------------onFailure--------------" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println("--------------onResponse--------------" + response.body().string());
                mRes =  response.body().string();
                //mTextViewRes.setText(aaa);
            }
        });
    }

    public void onPreviewFrameFaceTracker(byte[] data) {
        StFaceFeature feature1 = null;
        StFaceFeature feature2 = null;
        float result = 0;

        if (mFaceVerifyTracker != null) {
            // The camera's preview format is set to ImageFormat.NV21,
            // the data shall be nv21 format, so we pass PixelFormat.NV21 here
            try {
                int orientation = getMotionOrientation();

                //orientation = StFaceOrientation.ST_FACE_UP;
                Bitmap bitmap = Bytes2Bimap(data);
                //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                StFace[] faces = mFaceVerifyTracker.track(bitmap, orientation);

                Canvas canvas = mOverlapSurfaceView.getHolder().lockCanvas();
                // Clear the canvs
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                if (faces != null && faces.length != 0) {
                    feature1 = mVerify.getFeature(bitmap, faces[0]);
                }

                Bitmap bitmapStandard = bitmap;

                StFace[] faces2 = mDetector.detect(bitmapStandard, orientation);
                if (faces2 != null && faces2.length != 0) {
                    feature2 = mVerify.getFeature(bitmapStandard, faces2[0]);
                }

                mOverlapSurfaceView.getHolder().unlockCanvasAndPost(canvas);

                // 如果两张图片中的人脸特征都获取成功，那么比对这两个特征，得出最后的得分
                // if the features of two faces are got successfully, compare the
                // features, got the score of two feature
                if (feature1 != null && feature2 != null) {
                    try {
                        result = mVerify.compareFeature(feature1, feature2);
                    } catch (StFaceException e) {
                        //mErrorMessage = e.getLocalizedMessage();
                    }
                } else if (feature1 == null || feature2 == null) {
                    //mErrorMessage = getString(R.string.no_face_hint);
                }
                // 释放人脸特征对象
                // release the face feature object
                if (feature1 != null) {
                    feature1.recycle();
                }
                if (feature2 != null) {
                    feature2.recycle();
                }
                //return String.format("%.3f", result);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyFaceTrackerModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeFaceTracker();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseFaceTracker();
    }

    @Override
    protected void onPreviewFrame(byte[] data) {
        onPreviewFrameFaceTracker(data);
    }

}
