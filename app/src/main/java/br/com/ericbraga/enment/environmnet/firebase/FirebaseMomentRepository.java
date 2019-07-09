package br.com.ericbraga.enment.environmnet.firebase;

import android.net.UrlQuerySanitizer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxDocumentInsertAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxMomentDeleteAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxMomentListAdapter;
import br.com.ericbraga.enment.interactor.contracts.DataRepository;
import br.com.ericbraga.enment.interactor.contracts.QueryFilter;
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

                CollectionReference collection = mDatabase.collection(MOMENT_TABLE);

                FireStoreRxDocumentInsertAdapter adapter =
                        new FireStoreRxDocumentInsertAdapter(emitter);

                collection.add(moment)
                        .addOnSuccessListener(adapter)
                        .addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list() {

        return Single.create(new SingleOnSubscribe<List<Moment>>() {
            @Override
            public void subscribe(SingleEmitter<List<Moment>> emitter) throws Exception {
                FirestoreRxMomentListAdapter<Moment> adapter =
                        new FirestoreRxMomentListAdapter<>(emitter, Moment.class);

                Task<QuerySnapshot> querySnapshotTask = mDatabase.collection(MOMENT_TABLE).get();
                querySnapshotTask.addOnCompleteListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<List<Moment>> list(final QueryFilter filters) {

        return Single.create(new SingleOnSubscribe<List<Moment>>() {
            @Override
            public void subscribe(SingleEmitter<List<Moment>> emitter) {
                FirestoreRxMomentListAdapter<Moment> adapter =
                        new FirestoreRxMomentListAdapter<>(emitter, Moment.class);

                Task<QuerySnapshot> querySnapshotTask = query(filters);
                querySnapshotTask.addOnCompleteListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<Boolean> delete(final Moment moment) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) {

                QueryFilter filters = new QueryFilter.QueryBuilder()
                        .setOwner(moment.getOwner())
                        .setLatitude(moment.getLatitude())
                        .setLongitude(moment.getLongitude())
                        .build();

                Task<QuerySnapshot> deleteSnapTask = query(filters);

                FireStoreRxMomentDeleteAdapter adapter =
                        new FireStoreRxMomentDeleteAdapter(emitter);
                deleteSnapTask.addOnSuccessListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    private Task<QuerySnapshot> query(QueryFilter filters) {
        CollectionReference collection = mDatabase.collection(MOMENT_TABLE);

        Set<Map.Entry<String, Object>> values = filters.getValues();

        for (Map.Entry<String, Object> entry : values) {
            collection.whereEqualTo(entry.getKey(), entry.getValue());
        }

        return collection.get();
    }

}
