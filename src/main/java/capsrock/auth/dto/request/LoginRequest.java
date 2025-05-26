package capsrock.auth.dto.request;

public record LoginRequest(String email, String password, Double longitude, Double latitude) {
    
    public LoginRequest {
        validate(longitude, latitude);
    }
    
    private static void validate(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("위도 또는 경도를 입력해주세요");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180과 180 사이여야 합니다.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90과 90 사이여야 합니다.");
        }
    }
}
