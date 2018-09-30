package com.objective.dynamics.components.exception;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 10:01 AM
 */
@SuppressWarnings("unused")
public interface ExceptionHandler<T extends Throwable> extends Iterable<T> {
    /**
     * Return true is number of accumulated exceptions > 0
     *
     * @return true is number of accumulated exceptions > 0
     */
    boolean hasExceptions();

    /**
     * Addes exception to internal collection
     *
     * @param e exception
     */
    void addException(T e);

    /**
     * Returns number of accumulated exceptions
     *
     * @return number of accumulated exceptions
     */
    int size();

    String toString(int maxNumberOfCharacters);

    /**
     * Returns exception at index. If index >= exceptions.size() - returns null.
     *
     * @param index index of exception
     * @return exception at index. If index >= exceptions.size() - returns null.
     */
    T getException(int index);

    /**
     * @return Returns exception at index 0.
     * @see ExceptionHandler#getException(int)
     */
    T getFirstException();

    String getExceptionSummary();

    String getExceptionSummary(String exceptionSeparator);

    String getUserExceptionSummary();

    String getUserExceptionSummary(String exceptionSeparator);

    void clearExceptions();
}
