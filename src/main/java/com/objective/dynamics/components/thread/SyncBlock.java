package com.objective.dynamics.components.thread;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:24 AM
 */
public interface SyncBlock {
    /**
     * Adds runnable to execution queue but does not not execute
     *
     * @param runnable runnable
     */
    void addToExecuteQueue(Runnable runnable);

    /**
     * Executes runnables from the execution queue and blocks the main thread until it is done
     */
    void executeAndWait();
}
