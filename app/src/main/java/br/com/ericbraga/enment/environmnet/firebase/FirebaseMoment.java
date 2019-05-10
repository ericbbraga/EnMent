package br.com.ericbraga.enment.environmnet.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxDocumentInsert;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxDocumentAdapter;
import br.com.ericbraga.enment.environmnet.firebase.model.Moment;
import br.com.ericbraga.enment.environmnet.transfer.DataBaseContract;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FirebaseMoment implements DataBaseContract<Moment> {
    private static final String MOMENT_TABLE = "moments";

    private FirebaseFirestore mDatabase;

    public FirebaseMoment() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public Single<String> insert(final Moment moment) {

        if (moment == null || !moment.isValid()) {
            return Single.error(new IllegalArgumentException("Moment is invalid"));
        }

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                FirestoreRxAdapter adapter = new FireStoreRxDocumentInsert(emitter);
                mDatabase.collection(MOMENT_TABLE).add(moment).addOnSuccessListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list() {

        return Single.create(new SingleOnSubscribe<List<Moment>>() {
            @Override
            public void subscribe(SingleEmitter<List<Moment>> emitter) {
                FirestoreRxDocumentAdapter<Moment> adapter =
                        new FirestoreRxDocumentAdapter<>(emitter, Moment.class);

                mDatabase.collection(MOMENT_TABLE).get().addOnCompleteListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list(ListFilter filters) {
        return list();
    }
}
