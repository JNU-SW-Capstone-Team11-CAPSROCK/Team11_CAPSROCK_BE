package capsrock.auth.dto.request;

public record LoginRequest(String email, String password, Double longitude, Double latitude) { }
