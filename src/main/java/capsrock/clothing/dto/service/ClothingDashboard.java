package capsrock.clothing.dto.service;

import capsrock.location.geocoding.dto.service.AddressDTO;

public record ClothingDashboard(
        AddressDTO address,
        Integer clothingId,
        Double feelsLikeTemp,
        Double correction,
        Double CorrectedFeelsLikeTemp
) {

}
