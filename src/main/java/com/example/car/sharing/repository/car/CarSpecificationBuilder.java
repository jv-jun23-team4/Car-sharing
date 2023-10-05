package com.example.car.sharing.repository.car;

import com.example.car.sharing.dto.car.CarSearchParameters;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.SpecificationBuilder;
import com.example.car.sharing.repository.SpecificationProviderManager;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationBuilder implements SpecificationBuilder<Car, CarSearchParameters> {
    private static final String KEY_FOR_BRAND = "brand";
    private static final String KEY_FOR_TYPE = "type";
    private static final String KEY_FOR_PRICE_FROM = "fromPrice";
    private static final String KEY_FOR_PRICE_TO = "toPrice";
    private final SpecificationProviderManager<Car> specificationProviderManager;

    @Override
    public Specification<Car> build(CarSearchParameters searchParameters) {
        Specification<Car> specification = Specification.where(null);
        if (Objects.nonNull(searchParameters.brand())) {
            specification = specification.and(createSpecification(KEY_FOR_BRAND,
                    searchParameters.brand()));
        }
        if (Objects.nonNull(searchParameters.type())) {
            specification = specification.and(createSpecification(KEY_FOR_TYPE,
                    searchParameters.type()));
        }
        if (Objects.nonNull(searchParameters.fromPrice())) {
            specification = specification.and(createSpecification(KEY_FOR_PRICE_FROM,
                    searchParameters.fromPrice()));
        }
        if (Objects.nonNull(searchParameters.toPrice())) {
            specification = specification.and(createSpecification(KEY_FOR_PRICE_TO,
                    searchParameters.toPrice()));
        }
        return specification;
    }

    private Specification<Car> createSpecification(String key, Object... value) {
        return specificationProviderManager
                .getSpecificationProvider(key)
                .getSpecification(value);
    }
}
