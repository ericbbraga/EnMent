package br.com.ericbraga.enment.environmnet.transfer;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseTransferFiles implements UploadContract, DownloadContract {

    private final FirebaseStorage mStorageBase;

    public FirebaseTransferFiles(String firebaseBucket) {
        mStorageBase = FirebaseStorage.getInstance(firebaseBucket);
    }

    @Override
    public void upload(File file, String owner, final UploadContract.UploadCallback callback) {

        if (callback != null) {
            if (!file.exists()) {
                callback.onError("File does not exist:" + file.getAbsolutePath());
            }

            StorageReference storageReference = mStorageBase.getReference(owner);
            final StorageReference storageFromFile = storageReference.child(file.getName());
            Uri uriToUpload = Uri.fromFile(file);

            UploadTask task = storageFromFile.putFile(uriToUpload);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    return storageFromFile.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    callback.onSuccess(task.getResult());
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
    public void download(File outputDirectory, String userOwner, String fileName,
                         final DownloadContract.DownloadCallback callback) {

        if (callback != null) {
            if (!outputDirectory.exists() || outputDirectory.isFile()) {
                callback.onError("The directory does not exist: " + outputDirectory.getName());
            }

            if (!outputDirectory.canWrite()) {
                callback.onError("Permission denied to write on: " + outputDirectory.getName());
            }

            StorageReference storageReference = mStorageBase.getReference(userOwner);
            StorageReference documentReference = storageReference.child(fileName);

            File downloadFile = new File(outputDirectory, fileName);

            documentReference.getFile(downloadFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            callback.onSuccess();

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
