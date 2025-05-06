package capsrock.member.model.vo;

import java.util.regex.Pattern;

public record EncryptedPassword(String value) {
    private static final Pattern BCRYPT_PATTERN = Pattern.compile(
            "^\\$2[aby]\\$\\d{2}\\$.*"
    );

    public EncryptedPassword {
        validateEncryptedPassword(value);
    }

    private void validateEncryptedPassword(String value) {
        if (value == null || !BCRYPT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("BCrypt로 암호화 된 값이 아닙니다.");
        }
    }
}
