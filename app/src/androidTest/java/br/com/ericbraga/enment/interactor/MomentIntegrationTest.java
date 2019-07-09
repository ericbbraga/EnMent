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
import java.util.List;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.TestMockHelper;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.firebase.constants.Constants;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;

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

        Single<List<Moment>> observable = mCreateMoment.execute(moment).flatMap(
                new Function<String, SingleSource<List<Moment>>>() {
            @Override
            public SingleSource<List<Moment>> apply(String s)  {
                return mListPersonalMoments.execute(mCacheDir, mOwner);
            }
        });

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                Assert.assertEquals(1, moments.size());
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

    private Moment createMoment() {
        File file = mHelper.getTemporaryFile();
        return new Moment(100, 250, 180, file.getAbsolutePath(), mOwner);
    }

    @After
    public void tearDown() {
        mHelper.removeTemporaryFile();
    }


}