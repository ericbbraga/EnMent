package br.com.ericbraga.enment.interactor;

import java.io.File;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class DeleteMoment {

    private final DataRepository<Moment> mDataManager;
    private final UploadContract mImageManager;

    public DeleteMoment(DataRepository<Moment> dataManager, UploadContract imageManager) {
        mDataManager = dataManager;
        mImageManager = imageManager;
    }

    public Single<Boolean> execute(final Moment moment) {

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

        return mImageManager.delete(file.getName(), moment.getOwner()).flatMap(
                new Function<Void, SingleSource<? extends Boolean>>() {
                    @Override
                    public SingleSource<? extends Boolean> apply(Void aVoid) {
                        return mDataManager.delete(moment);
                    }
                }
        );
    }
}
