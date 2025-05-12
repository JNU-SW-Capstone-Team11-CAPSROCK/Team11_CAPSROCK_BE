package capsrock.common.exception.dto.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
    Boolean isError,
    String errorMessage
) {

}
