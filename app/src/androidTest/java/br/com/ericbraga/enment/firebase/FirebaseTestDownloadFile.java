package br.com.ericbraga.enment.firebase;

import android.Manifest;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestDownloadFile {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Test
    public void checkFileCouldBeDownloaded() {

        Context context = InstrumentationRegistry.getTargetContext();
        final File tempDirectory = context.getCacheDir();
        DownloadContract downloadAction = new FirebaseTransferFiles(sFirebaseBucket);

        final Semaphore semaphore = new Semaphore(0);

        final String ownerDir = "test";
        final String fileName = "test.txt";
        Single<Boolean> observable = downloadAction.download(tempDirectory, ownerDir, fileName);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                semaphore.release();
                Assert.assertTrue(aBoolean);

                File userDir = new File(tempDirectory, ownerDir);
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
}
