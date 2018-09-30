package com.objective.dynamics.odrules.rules.impl;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:20 PM
 */
public class ExecutionPath {
    public static final String NL = System.getProperty("line.separator");

    private StringBuilder stringBuilder = new StringBuilder();

    public synchronized void addPath(String path) {
        stringBuilder.append(path).append(NL);
    }

    public synchronized String getExecutionPath() {
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return getExecutionPath();
    }
}
