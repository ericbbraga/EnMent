package br.com.ericbraga.enment.interactor;

import android.Manifest;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.TestMockHelper;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.firebase.constants.Constants;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subscribers.DisposableSubscriber;

@RunWith(AndroidJUnit4.class)
public class MomentIntegrationTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    private static final String mOwner = "owner";
    private TestMockHelper mHelper;
    private CreateMoment mCreateMoment;
    private DeleteMoment mDeleteMoment;
    private ListPersonalMoments mListPersonalMoments;
    private ListNearestMoments mListNearestMoments;
    private File mCacheDir;

    @Before
    public void setUp() throws IOException {
        Context context = InstrumentationRegistry.getTargetContext();
        mCacheDir = context.getCacheDir();
        mHelper = new TestMockHelper(mCacheDir);
        mHelper.createTemporaryFile();

        DataRepository<Moment> dataRepository = new FirebaseMomentRepository();
        FirebaseTransferFiles firebaseTransferFiles = new FirebaseTransferFiles(Constants.sFirebaseBucket);

        mCreateMoment = new CreateMoment(dataRepository, firebaseTransferFiles);
        mDeleteMoment = new DeleteMoment(dataRepository, firebaseTransferFiles);

        mListPersonalMoments = new ListPersonalMoments(dataRepository, firebaseTransferFiles);
        mListNearestMoments = new ListNearestMoments(dataRepository, firebaseTransferFiles);
    }

    @Test
    public void validMomentShouldBeInsertedInRepositories() {
        final Moment moment = createMoment();
        Single<String> observable = mCreateMoment.execute(moment);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(String s) {
                mDeleteMoment.execute(moment);
                semaphore.release();
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
    public void validParamsShouldDownloadPersonalMoments() {
        final Moment moment = createMoment();

        final Semaphore semaphore = new Semaphore(0);

        Single<List<Moment>> observable = Completable.fromSingle(mCreateMoment.execute(moment)).andThen(
                mListPersonalMoments.execute(mCacheDir, mOwner)
        );

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                Assert.assertEquals(1, moments.size());
                Moment restoredMoment = moments.get(0);
                Assert.assertEquals(moment.getLatitude(), restoredMoment.getLatitude());
                Assert.assertEquals(moment.getLongitude(), restoredMoment.getLongitude());
                Assert.assertEquals(moment.getAngle(), restoredMoment.getAngle());
                Assert.assertEquals(moment.getOwner(), restoredMoment.getOwner());
                mDeleteMoment.execute(moment);
                semaphore.release();
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
    public void listOnlyMomentsNearestByMe() {
        final Semaphore semaphoreFilter = new Semaphore(0);
        final Semaphore semaphore = new Semaphore(0);
        final List<Moment> moments = createMultipleEqualMoments();

        List<SingleSource<String>> listObservables = new ArrayList<>();
        for (Moment moment : moments) {
            listObservables.add(mCreateMoment.execute(moment));
        }

        Moment anotherMoment = createMoment();
        listObservables.add(mCreateMoment.execute(anotherMoment));

        Flowable<String> fullRequest = Single.merge(listObservables);

        Single<List<Moment>> listObservable = Completable.fromPublisher(fullRequest).andThen(
                mListNearestMoments.execute(mCacheDir, 100, 250, 10)
        );

        Disposable disposable = listObservable.subscribeWith(
                new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                Assert.assertEquals(1, moments.size());
                semaphoreFilter.release();
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                semaphoreFilter.release();
            }
        });

        try {
            semaphoreFilter.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        } finally {
            disposable.dispose();
        }

        final List<Single<Boolean>> listDeleteObservable = new ArrayList<>();
        listDeleteObservable.add(mDeleteMoment.execute(anotherMoment));

        for (Moment moment : moments) {
            listDeleteObservable.add(mDeleteMoment.execute(moment));
        }

        Flowable<Boolean> fullDelete = Single.concat(listDeleteObservable);
        Disposable disposableDelete = fullDelete.subscribeWith(new DisposableSubscriber<Boolean>() {
            int deletedItems = 0;
            @Override
            public void onNext(Boolean aBoolean) {
                deletedItems++;
            }

            @Override
            public void onError(Throwable t) {
                Assert.fail(t.getMessage());
                semaphore.release();
            }

            @Override
            public void onComplete() {
                Assert.assertEquals(listDeleteObservable.size(), deletedItems);
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        } finally {
            disposableDelete.dispose();
        }

    }

    private Moment createMoment() {
        File file = mHelper.getTemporaryFile();
        return new Moment(100, 250, 180, file.getAbsolutePath(), mOwner);
    }

    private List<Moment> createMultipleEqualMoments() {
        List<Moment> moments = new ArrayList<>();
        File file = mHelper.getTemporaryFile();
        final String another_person = "fake_person";

        for (int i = 0; i < 5; i++) {
            Moment moment = new Moment(250, 100, 180, file.getAbsolutePath(), another_person + i);
            moments.add(moment);
        }

        return moments;
    }

    @After
    public void tearDown() {
        mHelper.removeTemporaryFile();
    }


}