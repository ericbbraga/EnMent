package br.com.ericbraga.enment.interactor;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class ListPersonalMomentsTest {

    @Test
    public void nullDirectoryShouldThrowException() {
        final DataRepository<Moment> dataManager = Mockito.mock(DataRepository.class);
        final List<Moment> mockreturn =  new ArrayList<>();

        Mockito.when(
                dataManager.list()
        ).thenReturn(
                Single.just(mockreturn)
        );

        final DownloadContract imageDownloader = Mockito.mock(DownloadContract.class);

        Mockito.when(
                imageDownloader.download(Mockito.any(File.class), Mockito.anyString(), Mockito.anyString())
        ).thenReturn(
                Single.just(true)
        );

        final Semaphore semaphore = new Semaphore(0);

        ListPersonalMoments listMoments = new ListPersonalMoments(dataManager, imageDownloader);
        Single<List<Moment>> observable = listMoments.execute(null);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                semaphore.release();
                Assert.fail("null directory should throw an exception");
                Assert.assertNotNull(moments);
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
    public void usingInvalidDirectoryShouldThrowException() {
        final DataRepository<Moment> dataManager = Mockito.mock(DataRepository.class);
        final List<Moment> mockreturn =  Mockito.mock(List.class);

        Mockito.when(
                dataManager.list()
        ).thenReturn(
                Single.just(mockreturn)
        );

        final DownloadContract imageDownloader = Mockito.mock(DownloadContract.class);

        Mockito.when(
                imageDownloader.download(
                        Mockito.any(File.class),
                        Mockito.anyString(),
                        Mockito.anyString()
                )
        ).thenReturn(
                Single.just(true)
        );

        File directory = new File("invalid_directory");
        final Semaphore semaphore = new Semaphore(0);

        ListPersonalMoments listMoments = new ListPersonalMoments(dataManager, imageDownloader);
        Single<List<Moment>> observable = listMoments.execute(directory);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                semaphore.release();
                Assert.fail("invalid directory should throw an exception");
                Assert.assertNotNull(moments);
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

}