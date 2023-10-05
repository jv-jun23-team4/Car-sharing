package com.example.car.sharing.repository.car;

import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.SpecificationProvider;
import com.example.car.sharing.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationProviderManager implements SpecificationProviderManager<Car> {
    private final List<SpecificationProvider<Car>> carSpecificationProviders;

    @Override
    public SpecificationProvider<Car> getSpecificationProvider(String key) {
        return carSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Can't find correct specification provider for key " + key));
    }
}
