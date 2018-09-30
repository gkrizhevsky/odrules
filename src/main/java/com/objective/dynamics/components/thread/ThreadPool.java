package com.objective.dynamics.components.thread;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:15 AM
 */
@SuppressWarnings("unused")
public interface ThreadPool {
    /**
     * Returns pool name
     *
     * @return pool name
     */
    String getName();

    /**
     * Returns queue size
     *
     * @return queue size
     */
    int getQueueSize();

    /**
     * Returns active threads count
     *
     * @return active threads count
     */
    int getActiveCount();

    /**
     * Returns tasks queue count
     *
     * @return tasks queue count
     */
    long getTaskCount();


    /**
     * Executes task
     *
     * @param task Runnable task
     */
    void execute(Runnable task);

    /**
     * Returns maximum task queue size
     *
     * @return maximum task queue size
     */
    int getMaximumQueueSize();

    /**
     * Stops the thread pool observing timeout
     */
    void stop();

    /**
     * Returns the maximum number of threads to allow in the
     * pool.
     *
     * @return the maximum number of threads to allow in the
     * pool.
     */
    int getMaximumPoolSize();

    /**
     * Stops immediately
     */
    void stopNow();

    /**
     * Returns true if there are any Active Threads
     *
     * @return true if there are any Active Threads
     */
    boolean hasActiveThreads();
}
