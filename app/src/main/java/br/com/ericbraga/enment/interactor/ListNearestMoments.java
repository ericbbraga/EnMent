package br.com.ericbraga.enment.interactor;

import java.io.File;
import java.util.List;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.interactor.contracts.QueryFilter;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ListNearestMoments {
    private final DataRepository<Moment> mDataManager;
    private final DownloadContract mImageDownloader;
    private final long mLatitudeRange;
    private final long mLongitudeRange;

    public ListNearestMoments(DataRepository<Moment> dataManager, DownloadContract imageDownloader,
                              long latitudeRange, long longitudeRange) {
        mDataManager = dataManager;
        mImageDownloader = imageDownloader;
        mLatitudeRange = latitudeRange;
        mLongitudeRange = longitudeRange;
    }

    public Single<List<Moment>> execute(final File outputDir, long latitude, long longitude,
                                        final int limit) {

        if (outputDir == null || !outputDir.exists()) {
            return Single.error(new Exception("Output directory is invalid"));
        }

        QueryFilter filter = new QueryFilter.QueryBuilder()
                .setLatitude(latitude, mLatitudeRange)
                .setLongitude(longitude, mLongitudeRange)
                .build();

        return mDataManager.list(filter, limit).map(new Function<List<Moment>, List<Moment>>() {
            @Override
            public List<Moment> apply(List<Moment> moments) {
                for (Moment moment : moments) {
                    mImageDownloader.download(outputDir, moment.getOwner(), moment.getPhotoPath());
                }

                return moments;
            }
        });
    }


}
