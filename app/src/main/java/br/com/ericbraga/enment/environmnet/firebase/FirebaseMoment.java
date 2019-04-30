package br.com.ericbraga.enment.environmnet.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.ericbraga.enment.environmnet.firebase.model.Moment;
import br.com.ericbraga.enment.environmnet.transfer.DataBaseContract;

public class FirebaseMoment implements DataBaseContract<Moment> {
    public static final String MOMENT_TABLE = "moments";

    private FirebaseFirestore mDatabase;

    public FirebaseMoment() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public void insert(Moment moment, final DatabaseCallback<String> callback) {
        if (callback != null) {

            if (moment == null || !moment.isValid()) {
                callback.onError("Invalid Object to Insert");

            } else {
                mDatabase.collection(MOMENT_TABLE).add(moment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callback.onSuccess(documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError(e.getMessage());
                    }
                });
            }
        }

    }

    @Override
    public void list(final DatabaseCallback<List<Moment>> callback) {
        if (callback != null) {
            mDatabase.collection(MOMENT_TABLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    List<Moment> moments = new ArrayList<>();

                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();

                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                moments.add( document.toObject(Moment.class) );

                            }
                        }

                    }

                    callback.onSuccess(moments);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onError(e.getMessage());
                }
            });
        }
    }

    @Override
    public void list(ListFilter filters, DatabaseCallback<List<Moment>> callback) {
        list(callback);
    }
}
