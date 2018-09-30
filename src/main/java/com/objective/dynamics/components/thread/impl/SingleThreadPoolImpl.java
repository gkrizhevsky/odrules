package com.objective.dynamics.components.thread.impl;

import java.util.LinkedList;
import java.util.List;

import com.objective.dynamics.components.thread.SyncBlock;
import com.objective.dynamics.components.thread.SyncThreadPool;

/**
 * Single threaded thread pool. Run in the main thread.
 *
 * @author Gennady Krizhevsky
 * Date: 2018-06-28.
 * Time: 9:32 AM
 */
public class SingleThreadPoolImpl implements SyncThreadPool {


    public void execute(Runnable runnable) {
        runnable.run();
    }

    public String getName() {
        return "SingleThreadPoolImpl";
    }

    public int getActiveCount() {
        return 0;
    }

    @Override
    public boolean hasActiveThreads() {
        return false;
    }

    @Override
    public int getMaximumPoolSize() {
        return 1;
    }

    public int getQueueSize() {
        return 0;
    }

    public void stop() {
    }

    @Override
    public void stopNow() {
    }


    public long getTaskCount() {
        return 0;
    }


    @Override
    public SyncBlock beginSyncBlock() {
        return new SyncBlockImpl();
    }

    @Override
    public int getMaximumQueueSize() {
        return 0;
    }

    /**
     * Specialized barrier.
     */
    protected static class SyncBlockImpl implements SyncBlock {
        private List<Runnable> runnableList = new LinkedList<>();

        public void addToExecuteQueue(Runnable runnable) {
            runnableList.add(runnable);
        }

        public void executeAndWait() {
            for (Runnable runnable : runnableList) {
                runnable.run();
            }
        }
    }
}
