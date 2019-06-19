package br.com.ericbraga.enment.interactor;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;

public class CreateMoment implements UseCase<String, Moment> {

    private final DataRepository<Moment> mDataManager;
    private final UploadContract mImageManager;

    public CreateMoment(DataRepository<Moment> dataManager, UploadContract imageManager) {
        mDataManager = dataManager;
        mImageManager = imageManager;
    }

    @Override
    public String execute(Moment moment) {
        return null;
    }
}
