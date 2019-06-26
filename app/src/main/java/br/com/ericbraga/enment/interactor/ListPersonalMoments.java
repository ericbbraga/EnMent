package br.com.ericbraga.enment.interactor;

import com.google.errorprone.annotations.Var;

import java.io.File;
import java.util.List;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
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

    public Single<List<Moment>> execute(final File outputDir) {

        if (outputDir == null) {
            return Single.error(new Exception("Output directory is invalid"));
        }

        return mDataManager.list().map(new Function<List<Moment>, List<Moment>>() {
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
