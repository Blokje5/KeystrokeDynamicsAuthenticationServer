package exceptions;

import exceptions.utils.ExceptionResponse;

public abstract class AppException  extends RuntimeException {
    public int status;

    public AppException(String message, int status) {
        super(message);
        this.status = status;
    }

    public ExceptionResponse toExceptionResponse() {
        return new ExceptionResponse(status, getMessage());
    }
}
