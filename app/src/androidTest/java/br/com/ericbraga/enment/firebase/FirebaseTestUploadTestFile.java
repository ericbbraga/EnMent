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

import br.com.ericbraga.enment.environmnet.firebase.FirebaseTransferFiles;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseTestUploadTestFile {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";

    private String mOwner;
    private String mFileName;
    private File mTempDirectory;

    @Before
    public void setUp() {

        Context context = InstrumentationRegistry.getTargetContext();
        mTempDirectory = context.getCacheDir();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String prefix = sdf.format(Calendar.getInstance().getTime());

        mOwner = "test";
        mFileName = String.format("test.txt_%s", prefix);

        final String fileContent = "This is a file from test purpose";



        File file = new File(mTempDirectory, mFileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileContent.getBytes());

        } catch (IOException e) {
            Assert.fail(e.getMessage());

        } finally {

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            }
        }
    }

    @Test
    public void checkNewFileUploadedToFirebase() {
        UploadContract uploadAction = new FirebaseTransferFiles(sFirebaseBucket);
        File file = new File(mTempDirectory, mFileName);

        final Semaphore semaphore = new Semaphore(0);
        Single<String> observable = uploadAction.upload(file, mOwner);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(String path) {
                semaphore.release();

                Assert.assertEquals(
                        String.format("/%s/%s", mOwner, mFileName),
                        path
                );
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
        File file = new File(mTempDirectory, mFileName);
        file.delete();
    }
}
