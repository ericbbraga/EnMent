package br.com.ericbraga.enment.environmnet.firebase.adapter;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.SingleEmitter;

public abstract class FirestoreRxAdapter<T, M> implements OnCompleteListener<T>, OnSuccessListener<T>, OnFailureListener {

    private final SingleEmitter<M> mEmitter;

    FirestoreRxAdapter(SingleEmitter<M> emitter) {
        mEmitter = emitter;
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {

        if (task.isSuccessful()) {
            T result = task.getResult();

            if (result != null) {
                success(mEmitter, result);
            } else {
                error("Invalid Result from Firebase");
            }

        } else {
            error("Unsuccessful onComplete - task is not successful");
        }
    }

    @Override
    public void onSuccess(T result) {
        if (result != null) {
            success(mEmitter, result);
        } else {
            error("Invalid Result from Firebase");
        }
    }

    abstract void success(SingleEmitter<M> emitter, T result);

    @Override
    public void onFailure(@NonNull Exception e) {
        error(e.getMessage());
    }

    void error(String message) {
        mEmitter.onError(new FirestoreRxException(message));
    }
}
