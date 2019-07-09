package br.com.ericbraga.enment.environmnet.firebase.adapter;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleEmitter;

public class FirestoreRxMomentListAdapter<M> extends FirestoreRxAdapter<QuerySnapshot, List<M>> {

    private final Class<M> mClass;

    public FirestoreRxMomentListAdapter(SingleEmitter<List<M>> emitter, Class<M> pClass) {
        super(emitter);
        mClass = pClass;
    }

    @Override
    void success(SingleEmitter<List<M>> emitter, QuerySnapshot result) {
        List<M> elements = new ArrayList<>();

        if (result != null) {
            for (QueryDocumentSnapshot document : result) {
                elements.add( document.toObject( mClass ) );
            }
        }

        emitter.onSuccess(elements);
    }
}
