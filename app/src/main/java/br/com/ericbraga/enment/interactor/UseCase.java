package br.com.ericbraga.enment.interactor;

public interface UseCase<T, E> {

    T execute(E e);

}
