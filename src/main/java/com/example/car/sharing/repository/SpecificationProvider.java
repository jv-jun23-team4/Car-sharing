package com.example.car.sharing.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    Specification<T> getSpecification(Object[] params);

    String getKey();
}
