package capsrock.weather.dto.service;

import capsrock.location.geocoding.dto.service.AddressDTO;

public record Dashboard(
        AddressDTO address,
        Double maxTemp,
        Double minTemp,
        Double temp) {}