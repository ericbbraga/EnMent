package br.com.ericbraga.enment.environmnet.transfer;

import android.net.Uri;

import java.io.File;

public interface UploadContract {
    void upload(File file, String owner, UploadContract.UploadCallback callback);

    interface UploadCallback {
        void onSuccess(Uri result);
        void onError(String message);
    }
}
