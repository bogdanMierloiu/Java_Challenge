package com.bogdanmierloiu.Java_Challenge.service;

import java.util.List;

public interface CrudOperation<T, K> {

    K add(T request);

    List<K> getAll();

    K findById(Long id);

    K update(T request);

    void delete(Long id);
}
