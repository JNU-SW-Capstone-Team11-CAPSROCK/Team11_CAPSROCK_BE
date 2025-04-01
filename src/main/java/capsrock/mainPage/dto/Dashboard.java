package capsrock.mainPage.dto;

import capsrock.location.geocoding.dto.service.AddressDTO;

public record Dashboard(
        AddressDTO address,
        int maxTemp,
        int minTemp,
        double temp) {}