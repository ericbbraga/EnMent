package br.com.ericbraga.enment.environmnet.firebase.model;

import android.text.TextUtils;

public class Moment implements ValidFirebaseValue {

    private String mUser;
    private long mLatitude;
    private long mLongitude;
    private long mAngle;
    private String mPhotoId;

    public Moment() {
    }

    public Moment(String photoId, String user, long latitude, long longitude, long angle) {
        mPhotoId = photoId;
        mUser = user;
        mLatitude = latitude;
        mLongitude = longitude;
        mAngle = angle;
    }

    public String getUser() {
        return mUser;
    }

    public long getLatitude() {
        return mLatitude;
    }

    public long getLongitude() {
        return mLongitude;
    }

    public long getAngle() {
        return mAngle;
    }

    public String getPhotoId() {
        return mPhotoId;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mUser) || !TextUtils.isEmpty(mPhotoId);
    }
}
