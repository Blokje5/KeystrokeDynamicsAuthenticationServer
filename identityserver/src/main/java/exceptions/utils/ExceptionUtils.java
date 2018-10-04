package exceptions.utils;

import exceptions.AppException;

public class ExceptionUtils {
    public static ExceptionResponse exceptionResponse(AppException appException) {
        return appException.toExceptionResponse();
    }
}
