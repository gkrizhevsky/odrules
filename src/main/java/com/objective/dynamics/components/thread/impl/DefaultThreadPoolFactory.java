package com.objective.dynamics.components.thread.impl;

import com.objective.dynamics.components.thread.SyncThreadPool;
import com.objective.dynamics.components.thread.ThreadPool;
import com.objective.dynamics.components.thread.ThreadPoolFactory;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:36 AM
 */
@SuppressWarnings("unused")
public class DefaultThreadPoolFactory implements ThreadPoolFactory {
    private long sleepTimeUntilStop = SyncThreadPoolImpl.DEFAULT_SLEEP_UNTIL_STOP;


    public DefaultThreadPoolFactory() {
    }

    public DefaultThreadPoolFactory(long sleepTimeUntilStop) {
        this.sleepTimeUntilStop = sleepTimeUntilStop;
    }

    @Override
    public ThreadPool newThreadPool(int maxNumberOfThreads, String baseThreadName) {
        return newSyncThreadPool(maxNumberOfThreads, baseThreadName);
    }

    @Override
    public SyncThreadPool newSyncThreadPool(int maxNumberOfThreads, String baseThreadName) {
        return newSyncThreadPool(maxNumberOfThreads, SyncThreadPoolImpl.DEFAULT_QUEUE_SIZE, baseThreadName);
    }

    @Override
    public SyncThreadPool newSyncThreadPool(int maxNumberOfThreads, int maximumQueueSize, String baseThreadName) {
        SyncThreadPool syncThreadPool;
        if (maxNumberOfThreads < 1) {
            syncThreadPool = new SingleThreadPoolImpl();
        } else {
            syncThreadPool = multiSyncThreadPool(maxNumberOfThreads, maximumQueueSize, baseThreadName);
        }
        return syncThreadPool;
    }

    protected SyncThreadPool multiSyncThreadPool(int maxNumberOfThreads, int maximumQueueSize, String baseThreadName) {
        return new SyncThreadPoolImpl()
                .setSleepTimeUntilStop(sleepTimeUntilStop)
                .setName(baseThreadName)
                .setMaximumQueueSize(maximumQueueSize)
                .setMaximumPoolSize(maxNumberOfThreads)
                .initialize();
    }


    public long getSleepTimeUntilStop() {
        return sleepTimeUntilStop;
    }

    public DefaultThreadPoolFactory setSleepTimeUntilStop(long sleepTimeUntilStop) {
        this.sleepTimeUntilStop = sleepTimeUntilStop;
        return this;
    }

}
