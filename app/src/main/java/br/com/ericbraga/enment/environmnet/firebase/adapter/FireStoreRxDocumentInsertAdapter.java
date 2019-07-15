package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleEmitter;

import static br.com.ericbraga.enment.environmnet.firebase.FirebaseMomentRepository.EXTRA_TIMESTAMP_FIELD;

public class FireStoreRxDocumentInsertAdapter extends FirestoreRxAdapter<DocumentReference, String> {

    public FireStoreRxDocumentInsertAdapter(SingleEmitter<String> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<String> emitter, DocumentReference result) {
        String id = result.getId();

        Map<String, Object> values = new HashMap<>();
        values.put(EXTRA_TIMESTAMP_FIELD, com.google.firebase.firestore.FieldValue.serverTimestamp());
        result.update(values);

        if (!id.isEmpty()) {
            emitter.onSuccess(id);
        } else {
            error("Could not insert document: Id is null");
        }
    }
}
