package br.com.ericbraga.enment.firebase;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository;
import br.com.ericbraga.enment.environmnet.firebase.model.MomentFirebase;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseInsertDataTest {

    @Test
    public void validMomentShouldBeInserted() {
        FirebaseMomentRepository firebaseMomentRepository = new FirebaseMomentRepository();
        MomentFirebase momentFirebase = new MomentFirebase("12345", "test", 100, 100, 45);
        final Semaphore semaphore = new Semaphore(0);

        Single<String> observable = firebaseMomentRepository.insert(momentFirebase);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>(){
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.assertTrue(!s.isEmpty());
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
            Assert.fail("Semaphore Failed - Test Failed");
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void emptyObjectShouldThrowException() {
        FirebaseMomentRepository firebaseMomentRepository = new FirebaseMomentRepository();
        MomentFirebase momentFirebase = new MomentFirebase();
        final Semaphore semaphore = new Semaphore(0);

        Single<String> observable = firebaseMomentRepository.insert(momentFirebase);
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>(){
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.fail("Empty Object should not be inserted");
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();
            }

        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail("Semaphore Failed - Test Failed");
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void nullObjectShouldThrowException() {
        FirebaseMomentRepository database = new FirebaseMomentRepository();
        final Semaphore semaphore = new Semaphore(0);

        Single<String> observable = database.insert(null);

        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<String>(){
            @Override
            public void onSuccess(String s) {
                semaphore.release();
                Assert.fail("Null Object should not be inserted");
            }

            @Override
            public void onError(Throwable e) {
                semaphore.release();
            }

        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail("Semaphore Failed - Test Failed");
        } finally {
            disposable.dispose();
        }
    }
}