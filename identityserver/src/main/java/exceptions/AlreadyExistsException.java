package exceptions;

public class AlreadyExistsException extends AppException {
    private static final int status = 409;

    public AlreadyExistsException(String message) {
        super(message, status);
    }
}
