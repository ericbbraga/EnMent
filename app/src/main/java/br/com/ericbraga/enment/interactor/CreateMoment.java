package br.com.ericbraga.enment.interactor;

import java.io.File;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class CreateMoment {

    private final DataRepository<Moment> mDataManager;
    private final UploadContract mImageManager;

    public CreateMoment(DataRepository<Moment> dataManager, UploadContract imageManager) {
        mDataManager = dataManager;
        mImageManager = imageManager;
    }

    public Single<String> execute(final Moment moment) {

        if (moment == null) {
            return Single.error(
                    new Exception("Moment is null")
            );
        }

        File file = new File(moment.getPhotoPath());
        if (!file.exists()) {
            return Single.error(
                    new Exception("File does not exist: " + file.getAbsolutePath())
            );
        }

        return mImageManager.upload(file, moment.getOwner()).flatMap(
            new Function<String, SingleSource<String>>() {
                @Override
                public SingleSource<String> apply(String s) {
                    return mDataManager.insert(moment);
                }
            }
        );
    }
}
