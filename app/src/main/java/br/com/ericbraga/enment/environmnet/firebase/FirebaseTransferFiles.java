package br.com.ericbraga.enment.environmnet.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import br.com.ericbraga.enment.environmnet.firebase.adapter.FireStoreRxDocumentDeleteAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxDocumentDownloadAdapter;
import br.com.ericbraga.enment.environmnet.firebase.adapter.FirestoreRxDocumentUploadAdapter;
import br.com.ericbraga.enment.interactor.contracts.DownloadContract;
import br.com.ericbraga.enment.interactor.contracts.UploadContract;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FirebaseTransferFiles implements UploadContract, DownloadContract {

    private final FirebaseStorage mStorageBase;

    public FirebaseTransferFiles(String firebaseBucket) {
        mStorageBase = FirebaseStorage.getInstance(firebaseBucket);
    }

    @Override
    public Single<String> upload(final File file, final String owner) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(final SingleEmitter<String> emitter) {
                if (!file.exists()) {
                    emitter.onError(new IllegalArgumentException("File does not exist: "
                            + file.getAbsolutePath()));
                }

                final StorageReference storageReference = mStorageBase.getReference(owner);
                final StorageReference storageFromFile = storageReference.child(file.getName());
                final Uri uriToUpload = Uri.fromFile(file);

                FirestoreRxDocumentUploadAdapter adapter = new FirestoreRxDocumentUploadAdapter(emitter);

                final UploadTask task = storageFromFile.putFile(uriToUpload);
                task.addOnSuccessListener(adapter).addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<Boolean> download(final File outputDirectory, final String userOwner,
                                    final String fileName) {

        if (!outputDirectory.exists() || outputDirectory.isFile()) {
            return Single.error(new IllegalArgumentException("The directory does not exist: "
                    + outputDirectory.getName()));
        }

        if (!outputDirectory.canWrite()) {
            return Single.error(new IllegalArgumentException(
                    "Permission denied to write on: " + outputDirectory.getName()));
        }

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws IOException {
                StorageReference storageReference = mStorageBase.getReference(userOwner);
                StorageReference documentReference = storageReference.child(fileName);

                FirestoreRxDocumentDownloadAdapter adapter = new FirestoreRxDocumentDownloadAdapter(emitter);


                File ownerDirectory = new File(outputDirectory, userOwner);
                if (!ownerDirectory.exists()) {
                    ownerDirectory.mkdir();
                }

                File downloadFile = new File(ownerDirectory, fileName);

                if (!downloadFile.exists()) {
                    downloadFile.createNewFile();
                }

                FileDownloadTask task = documentReference.getFile(downloadFile);
                task.addOnSuccessListener(adapter)
                        .addOnFailureListener(adapter);
            }
        });
    }

    @Override
    public Single<Void> delete(final String fileName, final String owner) {
        return Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> emitter) throws Exception {
                StorageReference storageReference = mStorageBase.getReference(owner);
                StorageReference documentReference = storageReference.child(fileName);

                Task<Void> task = documentReference.delete();
                FireStoreRxDocumentDeleteAdapter adapter = new FireStoreRxDocumentDeleteAdapter(emitter);
                task.addOnSuccessListener(adapter).addOnFailureListener(adapter);
            }
        });
    }



}
