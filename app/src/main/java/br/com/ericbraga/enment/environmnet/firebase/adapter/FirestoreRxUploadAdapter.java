package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.storage.UploadTask;

import io.reactivex.SingleEmitter;

public class FirestoreRxUploadAdapter extends FirestoreRxAdapter<UploadTask.TaskSnapshot, String> {
    public FirestoreRxUploadAdapter(SingleEmitter<String> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<String> emitter, UploadTask.TaskSnapshot result) {
        emitter.onSuccess( result.getStorage().getPath() );
    }
}
