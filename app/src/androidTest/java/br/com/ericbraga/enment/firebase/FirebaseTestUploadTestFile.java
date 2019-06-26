package br.com.ericbraga.enment.firebase;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.TestMockHelper;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.firebase.constants.Constants;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestUploadTestFile {

    private static final String mOwner = "owner";
    private TestMockHelper mHelper;

    @Before
    public void setUp() throws IOException {
        Context context = InstrumentationRegistry.getTargetContext();
        mHelper = new TestMockHelper(context.getCacheDir());
        mHelper.createTemporaryFile();
    }

    @Test
    public void checkNewFileUploadedToFirebase() {
        UploadContract uploadAction = new FirebaseTransferFiles(Constants.sFirebaseBucket);
        File file = mHelper.getTemporaryFile();

        final Semaphore semaphore = new Semaphore(0);
        final String fileName = mHelper.getFileName();

        Single<String> observable = uploadAction.upload(file, mOwner);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(String path) {
                semaphore.release();
                Assert.assertEquals(String.format("/%s/%s", mOwner, fileName), path);
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
            Assert.fail("Semaphore Exception - Test Fail");
        } finally {
            disposable.dispose();
        }
    }

    @After
    public void tearDown() {
        mHelper.removeTemporaryFile();
    }
}
