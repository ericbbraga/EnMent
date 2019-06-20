package br.com.ericbraga.enment.firebase;

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
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.TestMockHelper;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.firebase.constants.Constants;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestDownloadFile {

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

        UploadContract uploadAction = new FirebaseTransferFiles(Constants.sFirebaseBucket);

        final Semaphore semaphore = new Semaphore(0);
        File file = mHelper.getTemporaryFile();

        Single<String> observable = uploadAction.upload(file, mOwner);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(String path) {
                semaphore.release();
            }

            @Override
            public void onError(Throwable e) {
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

    @Test
    public void checkFileCouldBeDownloaded() {

        Context context = InstrumentationRegistry.getTargetContext();
        final File tempDirectory = context.getCacheDir();
        DownloadContract downloadAction = new FirebaseTransferFiles(Constants.sFirebaseBucket);

        final Semaphore semaphore = new Semaphore(0);

        final String fileName = mHelper.getFileName();

        Single<Boolean> observable = downloadAction.download(tempDirectory, mOwner, fileName);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                semaphore.release();
                Assert.assertTrue(aBoolean);

                File userDir = new File(tempDirectory, mOwner);
                Assert.assertTrue(userDir.exists());
                Assert.assertTrue(userDir.isDirectory());

                File file = new File(userDir, fileName);
                Assert.assertTrue(file.exists());
                Assert.assertTrue(file.isFile());
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
