package br.com.ericbraga.enment.environmnet.transfer;

import java.util.List;

import io.reactivex.Single;

public interface DataBaseContract<T> {

    Single<String> insert(T t);
    Single<List<T>> list();
    Single<List<T>> list(DataBaseContract.ListFilter filters);

    class ListFilter {

    }
}
