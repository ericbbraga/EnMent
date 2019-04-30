package br.com.ericbraga.enment.environmnet.transfer;

import java.util.List;

public interface DataBaseContract<T> {

    void insert(T t, DataBaseContract.DatabaseCallback<String> callback);
    void list(DataBaseContract.DatabaseCallback<List<T>> callback);
    void list(DataBaseContract.ListFilter filters, DataBaseContract.DatabaseCallback<List<T>> callback);

    interface DatabaseCallback<T> {
        void onSuccess(T returnedValue);
        void onError(String message);
    }

    class ListFilter {

    }
}
