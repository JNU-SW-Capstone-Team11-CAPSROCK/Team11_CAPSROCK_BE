package capsrock.member.exception;

public class MemberDuplicatedEmailException extends MemberException {
    private static final String ERROR_MESSAGE = "중복된 이메일입니다. 다른 이메일을 사용해주세요.";

    public MemberDuplicatedEmailException() {
        super(ERROR_MESSAGE);
    }
}
