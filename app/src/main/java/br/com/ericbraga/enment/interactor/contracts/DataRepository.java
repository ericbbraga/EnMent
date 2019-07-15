package br.com.ericbraga.enment.interactor.contracts;

import java.util.List;

import io.reactivex.Single;

public interface DataRepository<T> {

    Single<String> insert(T t);
    Single<List<T>> list();
    Single<List<T>> list(final QueryFilter filters);
    Single<List<T>> list(final QueryFilter filters, final int limit);
    Single<Boolean> delete(T t);

}
