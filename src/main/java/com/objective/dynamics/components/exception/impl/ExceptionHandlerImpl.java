package com.objective.dynamics.components.exception.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.util.string.StringUtil;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 10:06 AM
 */
@SuppressWarnings("unused")
public class ExceptionHandlerImpl<T extends Throwable> implements ExceptionHandler<T> {

    private List<T> exceptions = new ArrayList<>();

    private static final int DEFAULT_MAX_NUMBER_OF_EXCEPTIONS = 500;
    private static final String DEFAULT_EXCEPTION_SEPARATOR = "\n";
    private static final String DEFAULT_USER_EXCEPTION_SEPARATOR = ".";


    private int maxNumberOfExceptions = DEFAULT_MAX_NUMBER_OF_EXCEPTIONS;
    private static final String NESTED_SEP = "->";
    private static final String EXCEPT_MSG_SEP = ":";

    private String exceptionSeparator = DEFAULT_EXCEPTION_SEPARATOR;
    private String userExceptionSeparator = DEFAULT_USER_EXCEPTION_SEPARATOR;

    public ExceptionHandlerImpl() {
    }

    public ExceptionHandlerImpl(int maxNumberOfExceptions) {
        if (maxNumberOfExceptions <= 0) {
            throw new RuntimeException("maxNumberOfExceptions <= 0");
        }
        this.maxNumberOfExceptions = maxNumberOfExceptions;
    }

    /**
     * @see com.objective.dynamics.components.exception.ExceptionHandler#hasExceptions()
     */
    public final boolean hasExceptions() {
        return size() > 0;
    }

    /**
     * @see com.objective.dynamics.components.exception.ExceptionHandler#addException(Throwable e);
     */
    public synchronized final void addException(T e) {
        if (size() < maxNumberOfExceptions) {
            List<T> exceptions = exceptions();
            if (!exceptions.contains(e)) {
                exceptions.add(e);
            }
        }
    }

    /**
     * @see com.objective.dynamics.components.exception.ExceptionHandler#size()
     */
    public synchronized final int size() {
        return exceptions().size();
    }


    /**
     * Returns exception at index. If index >= exceptions.size() - returns null.
     *
     * @param index index of exception
     * @return exception at index. If index >= exceptions.size() - returns null.
     */
    public synchronized final T getException(int index) {
        return index < size() ? exceptions().get(index) : null;
    }

    public final T getFirstException() {
        return getException(0);
    }

    /**
     * Extracts inner most formatted error message from the exception.
     *
     * @param cause Exception to process
     * @return formatted error message from the exceptions including nested ones.
     */
    public static String extractInnerMostMessage(Throwable cause) {
        return extractInnerMostMessage(cause, -1);
    }


    /**
     * Extracts inner most formatted error message from the exception.
     *
     * @param cause                 Exception to process
     * @param maxNumberOfCharacters maximum number of characters to keep in the exception string
     * @return formatted error message from the exceptions including nested ones.
     */
    public static String extractInnerMostMessage(Throwable cause, int maxNumberOfCharacters) {
        return extractErrorMessage(getInnerMostException(cause), maxNumberOfCharacters);
    }

    /**
     * Extracts inner most formatted error message from the exception. Gets to exception of exceptionType if any - otherwise goes to the inner most.
     *
     * @param cause                 Exception to process
     * @param exceptionType         Exception type to stop if any
     * @param maxNumberOfCharacters maximum number of characters to keep in the exception string
     * @return formatted error message from the exceptions including nested ones.
     */
    public static String extractInnerMostMessage(Throwable cause, Class exceptionType, int maxNumberOfCharacters) {
        return extractErrorMessage(getInnerMostException(cause, exceptionType), maxNumberOfCharacters);
    }

    /**
     * Extracts formatted error message from the exceptions including nested ones.
     *
     * @param cause exception
     * @return formatted error message from the exceptions including nested ones.
     */
    private static String extractErrorMessage(Throwable cause) {
        return extractErrorMessage(cause, -1);
    }

    /**
     * Extracts formatted error message from the exceptions including nested ones.
     *
     * @param cause                 Exception to process
     * @param maxNumberOfCharacters maximum number of characters to keep in the exception string
     * @return formatted error message from the exceptions including nested ones.
     */
    private static String extractErrorMessage(Throwable cause, int maxNumberOfCharacters) {
        if (cause == null) {
            return "";
        }
        StringBuffer errorMessageBuffer = new StringBuffer(extractErrorFragment(cause));
        Throwable rootCause = cause;
        for (int i = 0; rootCause.getCause() != null && rootCause != rootCause.getCause() && i < 100; i++) {
            rootCause = rootCause.getCause();
            errorMessageBuffer.append(NESTED_SEP).append(extractErrorFragment(rootCause));
        }
        return format(errorMessageBuffer, maxNumberOfCharacters);
    }

    /**
     * Retrieves the inner most exception of type exceptionType. If none found returns  cause
     *
     * @param cause         cause
     * @param exceptionType exceptionType Class
     * @return the inner most exception of type exceptionType. If none found returns  cause
     */
    private static <M extends Throwable> Throwable getInnerMostException(Throwable cause, Class<M> exceptionType) {
        if (cause == null) {
            return null;
        }
        Throwable lastNonNullExceptionOfType = null;
        Throwable lastExceptionOfType = resolveOfType(cause, exceptionType);
        if (lastExceptionOfType != null) {
            lastNonNullExceptionOfType = lastExceptionOfType;
        }
        Throwable rootCause = cause;
        for (int i = 0; rootCause.getCause() != null && rootCause != rootCause.getCause() && i < 100; i++) {
            rootCause = rootCause.getCause();
            lastExceptionOfType = resolveOfType(rootCause, exceptionType);
            if (lastExceptionOfType != null) {
                lastNonNullExceptionOfType = lastExceptionOfType;
            }
        }

        if (lastNonNullExceptionOfType != null) {
            return lastNonNullExceptionOfType;
        } else {
            return resolveOfType(rootCause, exceptionType);
        }
    }

    private static <M extends Throwable> Throwable resolveOfType(Throwable cause, Class<M> exceptionType) {
        if (exceptionType != null && exceptionType.isAssignableFrom(cause.getClass())) {
            return cause;
        }
        return exceptionType == null ? cause : null;
    }

    private static Throwable getInnerMostException(Throwable cause) {
        return getInnerMostException(cause, null);
    }

    private static String format(String errorMessage, int maxNumberOfCharacters) {
        return errorMessage == null ? null : format(new StringBuffer(errorMessage), maxNumberOfCharacters);
    }

    private static String format(StringBuffer errorMessageBuffer, int maxNumberOfCharacters) {
        if (maxNumberOfCharacters > 0) {
            int length = Math.min(errorMessageBuffer.length(), maxNumberOfCharacters);
            return errorMessageBuffer.substring(0, length);
        } else {
            return errorMessageBuffer.toString();
        }
    }

    public static String format(StringBuilder errorMessageBuffer, int maxNumberOfCharacters) {
        if (maxNumberOfCharacters > 0) {
            int length = Math.min(errorMessageBuffer.length(), maxNumberOfCharacters);
            return errorMessageBuffer.substring(0, length);
        } else {
            return errorMessageBuffer.toString();
        }
    }

    private static String extractErrorFragment(Throwable cause) {
        String name = getShortClassName(cause.getClass());
        return name + EXCEPT_MSG_SEP + toCanonicalMessage(cause);
    }

    private static String toCanonicalMessage(Throwable cause) {
        String message = cause.getMessage() == null ? "" : cause.getMessage();
        return "'" + message.replaceAll("\r", "") + "'";
    }

    static String getShortClassName(Class<?> clazz) {
        String[] tokens = clazz.getName().split("\\.");
        return tokens[tokens.length - 1];
    }

    public String toString() {
        return toString(-1);
    }

    public String toString(int maxNumberOfCharacters) {
        StringBuilder buffer = new StringBuilder(toStringHeader());
        return toString0(maxNumberOfCharacters, buffer);
    }

    private String toString0(int maxNumberOfCharacters, StringBuilder buffer) {
        for (int i = 0; i < size(); i++) {
            String sep = i == 0 ? "" : "; ";
            Throwable throwable = getException(i);
            buffer.append(sep).append("[").append(i).append("]: ").
                    append(extractErrorMessage(throwable));
        }

        return maxNumberOfCharacters > 0 ? format(buffer.toString(), maxNumberOfCharacters) : buffer.toString();
    }

    private String toStringHeader() {
        return "Number of encountered exceptions: " + size() + "; ";
    }

    public String getExceptionSummary() {
        return getExceptionSummary(DEFAULT_EXCEPTION_SEPARATOR);
    }

    public String getExceptionSummary(String exceptionSeparator) {
        if (hasExceptions()) {
            String sep = resolveExceptionSeparator(exceptionSeparator);
            String errorMessage = "Number of exceptions: " + size();
            StringBuilder errors =
                    new StringBuilder(errorMessage);
            errors.append(sep);
            for (Throwable exception : exceptions()) {
                errors.append(exception.getMessage()).append(sep);
            }
            return errors.toString();
        }
        return "";
    }

    public String getUserExceptionSummary() {
        return getUserExceptionSummary(DEFAULT_USER_EXCEPTION_SEPARATOR);
    }

    public String getUserExceptionSummary(String exceptionSeparator) {
        if (hasExceptions()) {
            String sep = resolveUserExceptionSeparator(exceptionSeparator);
            String errorMessage = "Number of exceptions: " + size();
            StringBuilder errors =
                    new StringBuilder(errorMessage);
            errors.append(sep);
            for (Throwable exception : exceptions()) {
                String message = exception.getMessage();
                errors.append(message).append(sep);
            }
            return errors.toString();
        }
        return "";
    }

    private String resolveExceptionSeparator(String exceptionSeparator) {
        return exceptionSeparator != null ? exceptionSeparator : this.exceptionSeparator;
    }

    private String resolveUserExceptionSeparator(String exceptionSeparator) {
        return exceptionSeparator != null ? exceptionSeparator : this.userExceptionSeparator;
    }


    /**
     * Throws or throws exception of given type by input parameters.
     *
     * @param failedCodeName Name of failed portion of code
     * @param errorCode      error code. If errorCode == 0 - no exception is thrown
     * @param errorMessage   Error message
     * @throws Throwable default Exception with passed error message
     */
    public static void handleException(String failedCodeName, long errorCode, String errorMessage) throws Throwable {
        handleException(failedCodeName, errorCode, errorMessage, null);
    }

    /**
     * Throws or throws exception of given type by input parameters.
     *
     * @param failedCodeName        Name of failed portion of code
     * @param errorCode             error code. If errorCode == 0 - no exception is thrown
     * @param errorMessage          Error message
     * @param wrapperExceptionClass Throwable class that has to have at least 2 public constructors:
     *                              with (String, Throwable) and (String) implemented.
     * @throws Throwable Wrapper exception, or default Exception with passed error message
     */
    public static void handleException(String failedCodeName,
                                       long errorCode,
                                       String errorMessage,
                                       Class<? extends Exception> wrapperExceptionClass) throws Throwable {
        handleException(failedCodeName, errorCode, errorMessage, null, wrapperExceptionClass);
    }

    /**
     * Re-throws or throws exception of given type by input parameters.
     *
     * @param failedCodeName        Name of failed portion of code
     * @param errorCode             error code. If errorCode == 0 - no exception is thrown
     * @param errorMessage          Error message
     * @param innerException        If passed it will be wrapped into wrapper exception
     * @param wrapperExceptionClass Throwable class that has to have at least 2 public constructors:
     *                              with (String, Throwable) and (String) implemented.
     * @throws Exception Wrapper exception, or default Exception with passed error message
     */
    private static void handleException(String failedCodeName,
                                        long errorCode,
                                        String errorMessage,
                                        Exception innerException,
                                        Class<? extends Exception> wrapperExceptionClass) throws Exception {
        if (errorCode == 0) {
            return;
        }

        failedCodeName = ridOfNull(failedCodeName);
        errorMessage = ridOfNull(errorMessage);
        errorMessage = failedCodeName + " failed: code: " + errorCode + "; message = '" + errorMessage + "'";
        if (wrapperExceptionClass != null && Throwable.class.isAssignableFrom(wrapperExceptionClass)) {
            Exception t;
            try {
                if (innerException != null) {
                    Constructor<? extends Exception> constructor = wrapperExceptionClass.getConstructor(String.class, Throwable.class);
                    if (innerException.getClass() == wrapperExceptionClass) {
                        Throwable cause = innerException.getCause();
                        String message = innerException.getMessage();
                        errorMessage = "; " + message;
                        t = constructor.newInstance(errorMessage, cause);
                    } else {
                        t = constructor.newInstance(errorMessage, innerException);
                    }
                } else {
                    Constructor<? extends Exception> constructor = wrapperExceptionClass.getConstructor(String.class);
                    t = constructor.newInstance(errorMessage);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                t = getDefaultException(innerException, errorMessage);
            }
            throw t;
        } else {
            Exception t;
            t = getDefaultException(innerException, errorMessage);
            throw t;
        }
    }

    private List<T> exceptions() {
        return exceptions;
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return exceptions.iterator();
    }

    private static String ridOfNull(String failedCodeName) {
        return failedCodeName == null ? "" : failedCodeName;
    }

    private static Exception getDefaultException(Throwable innerException, String errorMessage) {
        Exception t;
        if (innerException != null) {
            t = new Exception(errorMessage, innerException);
        } else {
            t = new Exception(errorMessage);
        }
        return t;
    }

    public static void assertNotNull(String objectName, Object object) {
        if (object == null) {
            throw new RuntimeException("Null object: " + objectName);
        }
    }

    public static void assertNotEmpty(String objectName, String object) {
        if (StringUtil.isEmpty(object)) {
            throw new RuntimeException("Empty object: " + objectName);
        }
    }

    public static void assertTrue(String objectName, boolean condition) {
        if (!condition) {
            throw new RuntimeException("Condition expected to be TRUE but is FALSE: " + objectName);
        }
    }

    public static void assertFalse(String objectName, boolean condition) {
        if (condition) {
            throw new RuntimeException("Condition expected  to be FALSE but is TRUE: " + objectName);
        }
    }

    public synchronized final void clearExceptions() {
        exceptions().clear();
    }

}
