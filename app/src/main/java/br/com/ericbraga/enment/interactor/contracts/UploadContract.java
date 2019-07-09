package br.com.ericbraga.enment.interactor.contracts;

import android.net.Uri;

import java.io.File;

import io.reactivex.Single;

public interface UploadContract {
    Single<String> upload(File file, String owner);
    Single<Void> delete(String fileName, String owner);
}
