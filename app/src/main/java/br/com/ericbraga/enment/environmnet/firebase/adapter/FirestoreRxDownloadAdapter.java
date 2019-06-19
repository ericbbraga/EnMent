package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.storage.FileDownloadTask;

import io.reactivex.SingleEmitter;

public class FirestoreRxDownloadAdapter extends FirestoreRxAdapter<FileDownloadTask.TaskSnapshot, Boolean> {
    public FirestoreRxDownloadAdapter(SingleEmitter<Boolean> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<Boolean> emitter, FileDownloadTask.TaskSnapshot result) {
        if (result.getBytesTransferred() > 0) {
            emitter.onSuccess(true);
        } else {
            emitter.onError(new FirestoreRxException("Data could not be downloaded"));
        }
    }
}
