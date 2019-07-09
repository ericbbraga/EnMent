package br.com.ericbraga.enment.model;

public class Moment {

    private long mLatitude;
    private long mLongitude;
    private long mAngle;
    private String mPhotoPath;
    private String mOwner;

    public Moment() {
    }

    public Moment(long latitude, long longitude, long angle, String photoPath, String owner) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAngle = angle;
        mPhotoPath = photoPath;
        mOwner = owner;
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

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public String getOwner() {
        return mOwner;
    }
}
