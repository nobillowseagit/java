package com.sensetime.motionsdksamples.frameAnimation;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sensetime.motionsdksamples.R;

public class FrameImageFragment extends Fragment {
   private Button mBtSpeak, mBtHead, mBtHit, mBtTouchFace, mBtShortTouchChin, mBtLongTouchChin, mBtPinchArm, mBtKiss, mBtHug, mBtFall;
    private FrameAnimationUtils frameAnimationUtils;
    private ImageView mIvImage;
    private FrameAnimationManager mFrameAnimationManager;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_frame_image, null);
		mFrameAnimationManager = FrameAnimationManager.getInstance();
        mFrameAnimationManager.setCurrencyMethod(getActivity(), mIvImage, R.array.short_time_head, 1000, true);
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void speak() {
		mFrameAnimationManager.setCurrencyMethod(getActivity(), mIvImage, R.array.short_time_head, 1000, true);
    }

    public void hit() {
        mFrameAnimationManager.setCurrencyMethod(getActivity(), mIvImage, R.array.long_time_head, 1000, true);
    }
}