package capsrock.ultraviolet.dto.service;

import capsrock.location.geocoding.dto.service.AddressDTO;

public record Dashboard(
        AddressDTO address,
        Integer ultravioletLevel
) {
}
