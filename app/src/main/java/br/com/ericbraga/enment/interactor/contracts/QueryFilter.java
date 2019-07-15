package br.com.ericbraga.enment.interactor.contracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryFilter {

    private static String OWNER_FILTER = "owner";
    private static String LATITUDE_FILTER = "latitude";
    private static String LONGITUDE_FILTER = "longitude";

    private Long mLongitude;
    private Long mLongitudeRange;
    private Long mLatitude;
    private Long mLatitudeRange;
    private String mOwner;

    private QueryFilter() {
        mLongitude = null;
        mLatitude = null;
        mOwner = null;
    }

    public Set<Map.Entry<String, Object>> getValues() {
        Map<String, Object> values = new HashMap<>();
        if (mOwner != null) {
            values.put(OWNER_FILTER, mOwner);
        }

        if (mLongitude != null) {
            values.put(LONGITUDE_FILTER, mLongitude);
        }

        if (mLatitude != null) {
            values.put(LATITUDE_FILTER, mLatitude);
        }

        return values.entrySet();
    }

    public static class QueryBuilder {

        QueryFilter mQueryFilter;

        public QueryBuilder() {
            mQueryFilter = new QueryFilter();
        }

        public QueryBuilder setOwner(String owner) {
            mQueryFilter.mOwner = owner;
            return this;
        }

        public QueryBuilder setLatitude(long latitude, long range) {
            mQueryFilter.mLatitude = latitude;
            mQueryFilter.mLatitudeRange = range;
            return this;
        }

        public QueryBuilder setLongitude(long longitude, long range) {
            mQueryFilter.mLongitude = longitude;
            mQueryFilter.mLongitudeRange = range;
            return this;
        }

        public QueryFilter build() {
            return mQueryFilter;
        }
    }

    public static QueryFilter empty(){
        return new QueryFilter();
    }
}
