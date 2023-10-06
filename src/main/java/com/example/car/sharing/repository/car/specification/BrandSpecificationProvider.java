package com.example.car.sharing.repository.car.specification;

import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BrandSpecificationProvider implements SpecificationProvider<Car> {
    private static final String KEY = "brand";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Car> getSpecification(Object... params) {
        return (root, query, criteriaBuilder) -> root.get(KEY).in(Arrays.stream(params).toArray());
    }
}
