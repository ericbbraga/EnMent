package br.com.ericbraga.enment.firebase;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import br.com.ericbraga.enment.environmnet.firebase.FirebaseMoment;
import br.com.ericbraga.enment.environmnet.firebase.model.Moment;
import br.com.ericbraga.enment.environmnet.transfer.DataBaseContract;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirebaseInsertDataTest {

    @Test
    public void validMomentShouldBeInserted() {
        FirebaseMoment database = new FirebaseMoment();
        Moment moment = new Moment("12345", "test", 100, 100, 45);
        final Semaphore semaphore = new Semaphore(0);

        database.insert(moment, new DataBaseContract.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String returnedValue) {
                semaphore.release();
                Assert.assertTrue(!returnedValue.isEmpty());
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
            Assert.fail("Semaphore Failed - Test Failed");
        }
    }

    @Test
    public void emptyObjectShouldThrowException() {
        FirebaseMoment database = new FirebaseMoment();
        Moment moment = new Moment();
        final Semaphore semaphore = new Semaphore(0);

        database.insert(moment, new DataBaseContract.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String returnedValue) {
                semaphore.release();
                Assert.fail("Empty Object should not be inserted");
            }

            @Override
            public void onError(String message) {
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail("Semaphore Failed - Test Failed");
        }
    }

    @Test
    public void nullObjectShouldThrowException() {
        FirebaseMoment database = new FirebaseMoment();
        Moment moment = null;
        final Semaphore semaphore = new Semaphore(0);

        database.insert(moment, new DataBaseContract.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String returnedValue) {
                semaphore.release();
                Assert.fail("Empty Object should not be inserted");
            }

            @Override
            public void onError(String message) {
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Assert.fail("Semaphore Failed - Test Failed");
        }
    }
}