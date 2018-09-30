package com.objective.dynamics.components.thread;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:35 AM
 */
@SuppressWarnings("unused")
public interface ThreadPoolFactory {
    /**
     * Creates new regular thread pool
     *
     * @param maxNumberOfThreads maximum number of threads
     * @param baseThreadName     base thread name
     * @return new regular thread pool
     */
    ThreadPool newThreadPool(int maxNumberOfThreads, String baseThreadName);

    /**
     * Creates new sync thread pool
     *
     * @param maxNumberOfThreads maximum number of threads
     * @param baseThreadName     base thread name
     * @return new regular thread pool
     */
    SyncThreadPool newSyncThreadPool(int maxNumberOfThreads, String baseThreadName);

    /**
     * Creates new sync thread pool
     *
     * @param maxNumberOfThreads maximum number of threads
     * @param maxNumberOfThreads maximum maximum task queue size
     * @param baseThreadName     base thread name
     * @return new regular thread pool
     */
    SyncThreadPool newSyncThreadPool(int maxNumberOfThreads, int maximumQueueSize, String baseThreadName);
}
