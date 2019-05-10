package br.com.ericbraga.enment.firebase;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.environmnet.firebase.FirebaseMoment;
import br.com.ericbraga.enment.environmnet.firebase.model.Moment;
import br.com.ericbraga.enment.environmnet.transfer.DataBaseContract;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirebaseInsertDataTest {

    @Test
    public void validMomentShouldBeInserted() {
        FirebaseMoment database = new FirebaseMoment();
        Moment moment = new Moment("12345", "test", 100, 100, 45);
        final Semaphore semaphore = new Semaphore(0);

        Single<String> observable = database.insert(moment);
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
        FirebaseMoment database = new FirebaseMoment();
        Moment moment = new Moment();
        final Semaphore semaphore = new Semaphore(0);

        Single<String> observable = database.insert(moment);
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
        FirebaseMoment database = new FirebaseMoment();
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