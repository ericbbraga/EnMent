package br.com.ericbraga.enment.environmnet.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxDocumentInsert;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxDocumentListAdapter;
import br.com.ericbraga.enment.environmnet.firebase.model.MomentFirebase;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FirebaseMomentRepository implements DataRepository<MomentFirebase> {
    private static final String MOMENT_TABLE = "moments";

    private FirebaseFirestore mDatabase;

    public FirebaseMomentRepository() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public Single<String> insert(final MomentFirebase momentFirebase) {

        if (momentFirebase == null || !momentFirebase.isValid()) {
            return Single.error(new IllegalArgumentException("MomentFirebase is invalid"));
        }

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                FirestoreRxAdapter adapter = new FireStoreRxDocumentInsert(emitter);
                mDatabase.collection(MOMENT_TABLE).add(momentFirebase).addOnSuccessListener(adapter);
            }
        });
    }

    @Override
    public Single<List<MomentFirebase>> list() {

        return Single.create(new SingleOnSubscribe<List<MomentFirebase>>() {
            @Override
            public void subscribe(SingleEmitter<List<MomentFirebase>> emitter) {
                FirestoreRxDocumentListAdapter<MomentFirebase> adapter =
                        new FirestoreRxDocumentListAdapter<>(emitter, MomentFirebase.class);

                Task<QuerySnapshot> querySnapshotTask = mDatabase.collection(MOMENT_TABLE).get();
                querySnapshotTask.addOnCompleteListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<List<MomentFirebase>> list(ListFilter filters) {
        return list();
    }
}
