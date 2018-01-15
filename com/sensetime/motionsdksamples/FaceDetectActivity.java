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

import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceConfig;
import com.sensetime.faceapi.StFaceTrack;
import com.sensetime.faceapi.StFaceException;
import com.sensetime.motionsdksamples.Common.BaseCameraFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FaceDetectActivity extends BaseCameraFragment {

    private static final String TAG = BaseCameraFragment.class.getSimpleName();

    private StFaceTrack mFaceTracker = null;

    private void copyFaceTrackerModel() {
        try {
            // Copy model from assets to internal storage
            String modelName = "Body_Track.model";
            mModelPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + modelName;
            copyModelInAssets(modelName, mModelPath);
        } catch (IOException e) {
            e.printStackTrace();
            //showDialog("Fail to Copy Model", e.getMessage());
        }
    }

    public void resumeFaceTracker() {
        if (mFaceTracker == null) {
            try {
                mFaceTracker = new StFaceTrack(null, StFaceConfig.ST_DETECT_ENABLE_ALIGN_21 | StFaceConfig.ST_DETECT_ANY_FACE);
            } catch (StFaceException e) {
                e.printStackTrace();
                //showDialog("Fail to Create Body Tracker", e.getMessage());
            }
        }
    }

    public void pauseFaceTracker() {
        if (mFaceTracker != null) {
            mFaceTracker.release();
            mFaceTracker = null;
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

    public void onPreviewFrameFaceTracker(byte[] data) {
        if (mFaceTracker != null) {
            // The camera's preview format is set to ImageFormat.NV21,
            // the data shall be nv21 format, so we pass PixelFormat.NV21 here
            try {
                int orientation = getMotionOrientation();

                //orientation = StFaceOrientation.ST_FACE_UP;
                Bitmap bitmap = Bytes2Bimap(data);
                //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                StFace[] faces = mFaceTracker.track(bitmap, orientation);

                Canvas canvas = mOverlapSurfaceView.getHolder().lockCanvas();
                // Clear the canvs
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                if (faces != null && faces.length > 0) {
                    // Detected bodies in this frame, let's draw them on the canvas
                    //drawBodies(canvas, face);
                    String text = "";
                    for (int i = 0; i < faces.length; i++) {
                        StFace face = faces[i];
                        text += String.format("Total %d faces, Face[%d]:\nkeyPoints: %s\n",
                                faces.length, i, Arrays.toString(face.getPointsArray()));
                    }
                    //mtextViewInfo.setText(text);
                } else {
                    //mtextViewInfo.setText("No Face detected");
                }

                mOverlapSurfaceView.getHolder().unlockCanvasAndPost(canvas);
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
        //copyFaceTrackerModel();
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
