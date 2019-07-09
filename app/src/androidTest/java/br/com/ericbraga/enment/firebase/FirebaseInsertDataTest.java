package br.com.ericbraga.enment.firebase;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseInsertDataTest {

    @Test
    public void validMomentShouldBeInserted() {
        final FirebaseMomentRepository firebaseMomentRepository = new FirebaseMomentRepository();
        final Moment momentFirebase = new Moment(100, 100, 45, "12345", "test");
        final Semaphore semaphore = new Semaphore(0);

        Single<Boolean> observable = firebaseMomentRepository.insert(momentFirebase).flatMap(
                new Function<String, SingleSource<? extends Boolean>>() {
            @Override
            public SingleSource<? extends Boolean> apply(String s) {
                return firebaseMomentRepository.delete(momentFirebase);
            }
        });
        Disposable disposable = observable.subscribeWith(new DisposableSingleObserver<Boolean>(){

            @Override
            public void onSuccess(Boolean result) {
                Assert.assertTrue(result);
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