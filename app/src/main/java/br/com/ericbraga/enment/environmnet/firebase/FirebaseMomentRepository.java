package br.com.ericbraga.enment.environmnet.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxDocumentInsert;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxDocumentListAdapter;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.model.Moment;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FirebaseMomentRepository implements DataRepository<Moment> {
    private static final String MOMENT_TABLE = "moments";

    private FirebaseFirestore mDatabase;

    public FirebaseMomentRepository() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public Single<String> insert(final Moment moment) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                FirestoreRxAdapter adapter = new FireStoreRxDocumentInsert(emitter);
                mDatabase.collection(MOMENT_TABLE).add(moment)
                        .addOnSuccessListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list() {

        return Single.create(new SingleOnSubscribe<List<Moment>>() {
            @Override
            public void subscribe(SingleEmitter<List<Moment>> emitter) throws Exception {
                FirestoreRxDocumentListAdapter<Moment> adapter =
                        new FirestoreRxDocumentListAdapter<>(emitter, Moment.class);

                Task<QuerySnapshot> querySnapshotTask = mDatabase.collection(MOMENT_TABLE).get();
                querySnapshotTask.addOnCompleteListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list(ListFilter filters) {
        return list();
    }
}
