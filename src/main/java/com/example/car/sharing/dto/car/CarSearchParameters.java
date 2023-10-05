package com.example.car.sharing.dto.car;

import com.example.car.sharing.dto.SearchParameters;

public record CarSearchParameters(Object[] brand, Object[] type,
                                  Integer fromPrice, Integer toPrice) implements SearchParameters {
}
