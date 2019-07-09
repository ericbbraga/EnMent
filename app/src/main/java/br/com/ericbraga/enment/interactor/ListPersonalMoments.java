package br.com.ericbraga.enment.interactor;

import java.io.File;
import java.util.List;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.interactor.contracts.QueryFilter;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ListPersonalMoments {

    private final DataRepository<Moment> mDataManager;
    private final DownloadContract mImageDownloader;

    public ListPersonalMoments(DataRepository<Moment> dataManager, DownloadContract imageDownloader) {
        mDataManager = dataManager;
        mImageDownloader = imageDownloader;
    }

    public Single<List<Moment>> execute(final File outputDir, String owner) {

        if (outputDir == null || !outputDir.exists()) {
            return Single.error(new Exception("Output directory is invalid"));
        }

        QueryFilter filter = new QueryFilter.QueryBuilder().setOwner(owner).build();

        return mDataManager.list(filter).map(new Function<List<Moment>, List<Moment>>() {
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
