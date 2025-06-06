package capsrock.common.exception.handler;

import capsrock.common.exception.InternalServerException;
import capsrock.common.exception.InvalidLatitudeLongitudeException;
import capsrock.common.exception.dto.response.ErrorResponse;
import capsrock.member.exception.LoginFailedException;
import capsrock.member.exception.MemberDuplicatedEmailException;
import capsrock.member.exception.MemberException;
import capsrock.member.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CRITICAL_ERROR_MESSAGE = "서버에서 예상치 못한 에러가 발생하였습니다.";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder errorMessage = new StringBuilder();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(": ").append(message).append("\n");
        });


        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String errorMessage = "잘못된 타입이 전달되었습니다. "
                + "필드: " + ex.getName()
                + ", 타입: " + ex.getRequiredType().getName();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .isError(true)
                .errorMessage(errorMessage)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage("로그인에 실패하였습니다.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MemberDuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> handleMemberDuplicatedEmailException(
            MemberDuplicatedEmailException e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage("이미 존재하는 이메일입니다.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLatitudeLongitudeException.class)
    public ResponseEntity<ErrorResponse> handleInvaildLatitudeLongitudeException(InvalidLatitudeLongitudeException e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage("잘못된 위도, 경도입니다.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e){
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage(CRITICAL_ERROR_MESSAGE)
                .build();

        log.error(CRITICAL_ERROR_MESSAGE, e);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .isError(true)
                .errorMessage(CRITICAL_ERROR_MESSAGE)
                .build();

        log.error(CRITICAL_ERROR_MESSAGE, e);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
