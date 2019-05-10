package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.firestore.DocumentReference;

import io.reactivex.SingleEmitter;

public class FireStoreRxDocumentInsert extends FirestoreRxAdapter<DocumentReference, String> {

    public FireStoreRxDocumentInsert(SingleEmitter<String> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<String> emitter, DocumentReference result) {
        String id = result.getId();
        if (!id.isEmpty()) {
            emitter.onSuccess(id);
        } else {
            error("Id is null");
        }
    }
}