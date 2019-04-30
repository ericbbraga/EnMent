package br.com.ericbraga.enment.firebase;

import android.Manifest;
import android.net.Uri;
import android.os.Environment;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.transfer.UploadContract;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestUploadTestFile {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private final String mOwner = "test";
    private final String mFileName = "test.txt";

    @Before
    public void setUp() {
        final String fileContent = "This is a file from test purpose";
        File file = new File(Environment.getExternalStorageDirectory(), mFileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileContent.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void checkNewFileUploadedToFirebase() {
        UploadContract uploadAction = new FirebaseTransferFiles(sFirebaseBucket);
        File file = new File(Environment.getExternalStorageDirectory(), mFileName);

        final Semaphore semaphore = new Semaphore(0);
        uploadAction.upload(file, mOwner, new UploadContract.UploadCallback() {
            @Override
            public void onSuccess(Uri result) {
                semaphore.release();

                Assert.assertEquals(
                        String.format("%s/%s", mOwner, mFileName),
                        result.getLastPathSegment()
                );
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
