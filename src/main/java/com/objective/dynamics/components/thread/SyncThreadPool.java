package com.objective.dynamics.components.thread;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:15 AM
 */
public interface SyncThreadPool extends ThreadPool {
    /**
     * Starts synchronized block. Main thread will wait until all the tasks
     * within the block are executed. In general it is not a good idea
     * mixing SyncBlock executions with execute() methods calls for
     * the same instance of a SyncThreadPool.
     * <p>
     * beginSyncBlock() method is more appropriate for a long lasting
     * batch type operations when it is desirable to
     * control the execution by pages and to return to the main thread
     * after all the tasks are done.
     *
     * @return synchronized block
     * @see SyncBlock
     */
    SyncBlock beginSyncBlock();
}
