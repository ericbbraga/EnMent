package br.com.ericbraga.enment.environmnet.firebase.adapter;

import io.reactivex.SingleEmitter;

public class FireStoreRxDocumentDeleteAdapter extends FirestoreRxAdapter<Void, Void> {
    public FireStoreRxDocumentDeleteAdapter(SingleEmitter<Void> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<Void> emitter, Void result) {
        emitter.onSuccess(null);
    }

}
