package capsrock.auth.dto.request;

public record RegisterRequest(String email, String nickname, String password, Double longitude,
                              Double latitude) {

}
