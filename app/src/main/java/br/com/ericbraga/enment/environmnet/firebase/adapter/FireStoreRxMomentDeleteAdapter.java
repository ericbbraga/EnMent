package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.SingleEmitter;

public class FireStoreRxMomentDeleteAdapter extends FirestoreRxAdapter<QuerySnapshot, Boolean> {

    public FireStoreRxMomentDeleteAdapter(SingleEmitter<Boolean> emitter) {
        super(emitter);
    }

    @Override
    void success(SingleEmitter<Boolean> emitter, QuerySnapshot result) {
        for (DocumentSnapshot document : result.getDocuments()) {
            document.getReference().delete();
        }

        emitter.onSuccess(true);
    }

}
