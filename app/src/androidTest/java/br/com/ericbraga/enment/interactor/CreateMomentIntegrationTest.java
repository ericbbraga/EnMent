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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.TestMockHelper;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.firebase.constants.Constants;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class CreateMomentIntegrationTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    private static final String mOwner = "owner";
    private TestMockHelper mHelper;

    @Before
    public void setUp() throws IOException {
        Context context = InstrumentationRegistry.getTargetContext();
        mHelper = new TestMockHelper(context.getCacheDir());
        mHelper.createTemporaryFile();
    }

    @Test
    public void validMomentShouldBeInsertedInRepositories() {

        DataRepository<Moment> dataRepository = new FirebaseMomentRepository();
        UploadContract uploadContract = new FirebaseTransferFiles(Constants.sFirebaseBucket);

        File file = mHelper.getTemporaryFile();

        Moment moment = new Moment(100, 250, 180, file.getAbsolutePath(), mOwner);

        CreateMoment createMoment = new CreateMoment(dataRepository, uploadContract);
        Single<String> observable = createMoment.execute(moment);

        final Semaphore semaphore = new Semaphore(0);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(String s) {
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

    @After
    public void tearDown() {
        mHelper.removeTemporaryFile();
    }


}