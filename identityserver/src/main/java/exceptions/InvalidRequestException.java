package exceptions;

public class InvalidRequestException extends AppException {
    private static int status = 400;

    public InvalidRequestException(String message) {
        super(message, status);
    }
}
