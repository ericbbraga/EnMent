package br.com.ericbraga.enment;

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

import br.com.ericbraga.enment.environmnet.transfer.DownloadContract;
import br.com.ericbraga.enment.environmnet.transfer.FirebaseTransferFiles;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestDownloadFile {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    @Test
    public void checkFileCouldBeDownloaded() {

        Context context = InstrumentationRegistry.getTargetContext();
        final File tempDirectory = context.getCacheDir();
        DownloadContract downloadAction = new FirebaseTransferFiles(sFirebaseBucket);

        final Semaphore semaphore = new Semaphore(0);

        downloadAction.download(tempDirectory, "test", "test.txt",
                new DownloadContract.DownloadCallback() {
            @Override
            public void onSuccess() {
                semaphore.release();
                Log.i("Download", "onSuccess: " + tempDirectory.getAbsolutePath());
            }

            @Override
            public void onError(String message) {
                semaphore.release();
                Assert.fail(message);
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail("Semaphore Exception - Test Fail");
        }

    }

}
