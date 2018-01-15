package com.sensetime.motionsdksamples.Common;

import android.graphics.Bitmap;

import com.sensetime.faceapi.StFaceFeature;
import com.sensetime.motionsdksamples.Utils.ObjectUtils;

import static com.sensetime.motionsdksamples.Utils.UniqueId.getStrUid;

/**
 * Created by lyt on 2017/9/28.
 */

public class Person {
    public static final int PERSON_NOT_SIMILAR = 0;
    public static final int PERSON_SIMILAR = 1;
    public int mDid;
    public String mStrUid;
    public String mName;
    public String mPinyin;
    public String mGender;
    public int mAge;
    byte[] mPictureByte;
    public Bitmap mPictureBitmap;
    String mProfession;
    boolean mMarried;
    String mRelationship;
    public boolean mUpdated;
    public StFaceFeature mFeature;
    public byte[] mFeatureByte;
    public String mEmotion;
    public int mRegistered;

    public Person() {
        mUpdated = false;
        mStrUid = getStrUid();
        mRegistered = 0;
        mAge = 0;
        mName = "无姓名";
        mGender = "未知性别";
    }

    public void update() {
        mUpdated = true;
    }

    public void accquied() {
        mUpdated = false;
    }

    public void setGender(String gender) {
        mGender = gender;
        update();
    }

    public void setAge(int age) {
        mAge = age;
        update();
    }

    public String getGender() {
        accquied();
        return mGender;
    }

    public int getAge() {
        accquied();
        return mAge;
    }

    public void setEmotion(String emotion) {
        mEmotion = emotion;
        update();
    }

    public String getEmotion() {
        accquied();
        return mEmotion;
    }

    public void setRelationship(String relationship) {
        mRelationship = relationship;
        update();
    }

    public String getRelatioship() {
        accquied();
        return mRelationship;
    }

    public void setFeatureByte(StFaceFeature feature) {
        try {
            mFeatureByte = ObjectUtils.getBytesFromObject(feature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFeatureByte() {
        return mFeatureByte;
    }

    public boolean compare(Person person) {
        if (!this.mStrUid.equals(person.mStrUid)) {
            return false;
        }
        if (this.mAge != person.mAge ||
                !this.mGender.equals(person.mGender) ||
                !this.mEmotion.equals(person.mEmotion)) {
            return false;
        }
        return true;
    }

    public int similar(Person person) {
        if (Math.abs(this.mAge - person.mAge) > 5 ||
                !this.mGender.equals(person.mGender) ||
                !this.mEmotion.equals(person.mEmotion)) {
            return PERSON_NOT_SIMILAR;
        }
        return PERSON_SIMILAR;
    }

    public boolean isRegistered() {
        if (mRegistered == 0) {
            return false;
        }
        return true;
    }
}
