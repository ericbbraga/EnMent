package br.com.ericbraga.enment.model;

public class Moment {

    private long mLatitude;
    private long mLongitude;
    private long mAngle;
    private String mPhotoPath;

    public Moment(long latitude, long longitude, long angle, String path) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAngle = angle;
        mPhotoPath = path;
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
        return mPhotoPath;
    }
}
