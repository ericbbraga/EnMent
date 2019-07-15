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

    public void setLatitude(long latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(long longitude) {
        mLongitude = longitude;
    }

    public void setAngle(long angle) {
        mAngle = angle;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }
}
