package com.objective.dynamics.util.enumeration;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 10:24 AM
 */
public enum Tolerance {
    /**
     * Requires code string to be found in one of enumns
     */
    STRICT,
    /**
     * Requires code string to be found in one of enumns except for null code vales
     */
    TOLERATE_EMPTY,
    /**
     * Does not impose any restrictions on the code string: if not found converts to null
     */
    TOLERATE_ALL
}
