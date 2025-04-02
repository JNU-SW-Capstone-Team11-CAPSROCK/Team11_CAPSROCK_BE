package capsrock.mainPage.dto.service;

import capsrock.geocoding.dto.service.AddressDTO;

public record Dashboard(
        AddressDTO address,
        Double maxTemp,
        Double minTemp,
        Double temp) {}