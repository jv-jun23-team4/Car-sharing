package com.example.car.sharing.repository.car.specification;

import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class FromPriceSpecificationProvider implements SpecificationProvider<Car> {
    private static final String KEY = "fromPrice";
    private static final String NAME_OF_COLUMN = "dailyFee";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Car> getSpecification(Object... params) {
        Integer fromPrice = (Integer) params[0];
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get(NAME_OF_COLUMN), fromPrice);
    }
}
