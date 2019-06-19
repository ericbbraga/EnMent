package br.com.ericbraga.enment.interactor.contracts;

import java.io.File;

import io.reactivex.Single;

public interface DownloadContract {
    Single<Boolean> download(File outputDirectory, String userOwner, String fileName);
}
