package br.com.ericbraga.enment.environmnet.transfer;

import java.io.File;

public interface DownloadContract {
    void download(File outputDirectory, String userOwner, String fileName,
                  DownloadContract.DownloadCallback callback);

    interface DownloadCallback {
        void onSuccess();
        void onError(String message);
    }
}
