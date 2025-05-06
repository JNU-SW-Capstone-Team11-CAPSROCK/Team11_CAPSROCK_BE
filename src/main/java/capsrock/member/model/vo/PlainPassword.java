package capsrock.member.model.vo;

public record PlainPassword(
        String value
) {
    private static final Integer MINIMUM_LENGTH = 8;

    public PlainPassword {
        validate(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요");
        }

        if (value.length() < MINIMUM_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 최소 %d자 여야 합니다.".formatted(MINIMUM_LENGTH));
        }
    }

}
