package br.com.ericbraga.enment.interactor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(MockitoJUnitRunner.class)
public class CreateMomentTest {

    private String mOwner;
    private File mFile;

    @Before
    public void setUp() throws IOException {
        mOwner = "owner";
        mFile = new File("test.file");

        if (!mFile.exists() && !mFile.createNewFile()) {
            throw new IOException("Important file could not be created");
        }
    }

    @Test
    public void methodsShouldBeCalledForValidMoment() {

        final DataRepository<Moment> dataRepositoryMock = Mockito.mock(DataRepository.class);
        Mockito.when(
                dataRepositoryMock.insert(Mockito.any(Moment.class))
        ).thenReturn(
                Single.just("id")
        );

        final UploadContract imageContractMock = Mockito.mock(UploadContract.class);
        Mockito.when(
                imageContractMock.upload(Mockito.any(File.class), Mockito.anyString())
        ).thenReturn(
                Single.just("/owner/test.file")
        );

        Moment moment = new Moment(100, 100, 10, mFile.getAbsolutePath(), mOwner);

        CreateMoment createMoment = new CreateMoment(dataRepositoryMock, imageContractMock);
        Single<String> observable = createMoment.execute(moment);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                semaphore.release();

                Mockito.verify(imageContractMock).upload(Mockito.any(File.class), Mockito.anyString());
                Mockito.verify(dataRepositoryMock).insert(Mockito.any(Moment.class));
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();
                Assert.fail(e.getMessage());
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        disposable.dispose();
    }

    @Test
    public void invalidMomentObjectShouldThrowException() {
        DataRepository<Moment> dataRepositoryMock = Mockito.mock(DataRepository.class);
        UploadContract imageContractMock = Mockito.mock(UploadContract.class);

        Moment moment = new Moment(100, 100, 10, "invalid_file_location.txt", mOwner);

        CreateMoment createMoment = new CreateMoment(dataRepositoryMock, imageContractMock);
        Single<String> observable = createMoment.execute(moment);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.fail("An Exception should be raised");
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        disposable.dispose();
    }

    @Test
    public void nullMomentObjectShouldThrowException() {
        DataRepository<Moment> dataRepositoryMock = Mockito.mock(DataRepository.class);
        UploadContract imageContractMock = Mockito.mock(UploadContract.class);

        CreateMoment createMoment = new CreateMoment(dataRepositoryMock, imageContractMock);
        Single<String> observable = createMoment.execute(null);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.fail("An Exception should be raised");
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        disposable.dispose();
    }

    @Test
    public void whenUploadFailedDataRepositoryShouldNotBeCalled() {
        final DataRepository<Moment> dataRepositoryMock = Mockito.mock(DataRepository.class);

        final UploadContract imageContractMock = Mockito.mock(UploadContract.class);
        Mockito.when(
                imageContractMock.upload(Mockito.any(File.class), Mockito.anyString())
        ).thenReturn(
                Single.<String>error(new Throwable())
        );

        Moment moment = new Moment(100, 100, 10, mFile.getAbsolutePath(), mOwner);

        CreateMoment createMoment = new CreateMoment(dataRepositoryMock, imageContractMock);
        Single<String> observable = createMoment.execute(moment);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.fail("An Exception should be raised");
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();

                Mockito.verify(
                        imageContractMock
                ).upload(
                        Mockito.any(File.class), Mockito.anyString()
                );

                Mockito.verify(
                        dataRepositoryMock, Mockito.never()
                ).insert(
                        Mockito.any(Moment.class)
                );

            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        disposable.dispose();
    }

    @After
    public void tearDown() {
        mFile.delete();
    }

}