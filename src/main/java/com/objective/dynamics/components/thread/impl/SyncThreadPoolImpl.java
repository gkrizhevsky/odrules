package com.objective.dynamics.components.thread.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import com.objective.dynamics.components.thread.SyncBlock;
import com.objective.dynamics.components.thread.SyncThreadPool;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:17 AM
 */
@SuppressWarnings("unused")
public class SyncThreadPoolImpl implements SyncThreadPool {
    static final int DEFAULT_SLEEP_UNTIL_STOP = 60000;
    static final int DEFAULT_QUEUE_SIZE = 101;

    private ThreadPoolExecutor threadPoolExecutor;
    private long sleepTimeUntilStop = DEFAULT_SLEEP_UNTIL_STOP;
    private static final RejectedExecutionHandler DEFAULT_BLOCK_INCOMING_POLICY =
            new ThreadPoolExecutor.CallerRunsPolicy();

    private int corePoolSize;
    private int maximumPoolSize = 1;
    private int maximumQueueSize = DEFAULT_QUEUE_SIZE;
    private String name;
    private long keepAliveTimeMillis = Long.MAX_VALUE;
    private RejectedExecutionHandler rejectedPolicy = DEFAULT_BLOCK_INCOMING_POLICY;

    SyncThreadPoolImpl() {
    }

    synchronized SyncThreadPoolImpl initialize() {
        if (threadPoolExecutor == null) {
            assert true : maximumPoolSize > 0;

            if (maximumQueueSize <= 0) {
                maximumQueueSize = DEFAULT_QUEUE_SIZE;
            }

            if (corePoolSize <= 0) {
                corePoolSize = maximumPoolSize;
            }

            WorkerThreadFactory workerThreadFactory = new WorkerThreadFactory(name);
            BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(maximumQueueSize);
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeMillis,
                    TimeUnit.MILLISECONDS, blockingQueue, workerThreadFactory, rejectedPolicy);
        }
        return this;
    }

    public int getQueueSize() {
        ensureInitialized();
        return threadPoolExecutor.getQueue().size();
    }

    public int getActiveCount() {
        ensureInitialized();
        return threadPoolExecutor.getActiveCount();
    }

    @Override
    public boolean hasActiveThreads() {
        return getActiveCount() > 0;
    }


    public void execute(Runnable task) {
        ensureInitialized();
        threadPoolExecutor.execute(task);
    }

    public long getTaskCount() {
        ensureInitialized();
        return threadPoolExecutor.getTaskCount();
    }

    public SyncBlock beginSyncBlock() {
        ensureInitialized();
        return new SyncBlockImpl(threadPoolExecutor);
    }

    public RejectedExecutionHandler getRejectedPolicy() {
        return rejectedPolicy;
    }


    public void setRejectedPolicy(RejectedExecutionHandler rejectedPolicy) {
        this.rejectedPolicy = rejectedPolicy;
    }

    public String getName() {
        return name;
    }


    SyncThreadPoolImpl setName(String name) {
        this.name = name;
        return this;
    }


    public SyncThreadPoolImpl setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }


    public int getCorePoolSize() {
        return corePoolSize;
    }


    public SyncThreadPoolImpl setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    @Override
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }


    SyncThreadPoolImpl setMaximumPoolSize(int maximumPoolSize) {
        assert true : maximumPoolSize > 0;
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    @Override
    public int getMaximumQueueSize() {
        return maximumQueueSize;
    }


    SyncThreadPoolImpl setMaximumQueueSize(int maximumQueueSize) {
        this.maximumQueueSize = maximumQueueSize;
        return this;
    }


    public long getSleepTimeUntilStop() {
        return sleepTimeUntilStop;
    }


    SyncThreadPoolImpl setSleepTimeUntilStop(long sleepTimeUntilStop) {
        this.sleepTimeUntilStop = sleepTimeUntilStop;
        return this;
    }

    public void stop() {
        try {
            ensureInitialized();
            threadPoolExecutor.shutdown();
            if (hasActiveThreads()) {
                if (sleepTimeUntilStop > 0) {
                    try {
                        Thread.sleep(sleepTimeUntilStop);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted while trying to shutdown the " + name + " thread pool");
                    }
                }
            }

            stopNow();
        } catch (Exception e) {
            System.err.println("Cannot stop the pool");
            e.printStackTrace();
        }
    }

    @Override
    public void stopNow() {
        ensureInitialized();
        threadPoolExecutor.shutdownNow();
    }


    //
    //
    // Util classes:
    //
    //
    protected static class RunnableAdapter implements Runnable {
        private Runnable runnable;
        private CountDownLatch syncCounter;

        RunnableAdapter(Runnable runnable, CountDownLatch syncCounter) {
            this.runnable = runnable;
            this.syncCounter = syncCounter;
        }

        public void run() {
            try {
                runnable.run();
            } finally {
                syncCounter.countDown();
            }
        }
    }


    public long getKeepAliveTimeMillis() {
        return keepAliveTimeMillis;
    }


    public void setKeepAliveTimeMillis(long keepAliveTimeMillis) {
        this.keepAliveTimeMillis = keepAliveTimeMillis;
    }


    public boolean isStarted() {
        return threadPoolExecutor != null;
    }

    private void ensureInitialized() {
        if (threadPoolExecutor == null) {
            throw new RuntimeException("Thread pool is not initialized - call initialize method 1st");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + System.identityHashCode(this) +
                "{" +
                "threadPoolExecutor=" + threadPoolExecutor +
                ", sleepTime=" + sleepTimeUntilStop +
                ", corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", maximumQueueSize=" + maximumQueueSize +
                ", name='" + name + '\'' +
                ", keepAliveTimeMillis=" + keepAliveTimeMillis +
                '}';
    }

    //
    // Sync Block:
    //

    /**
     * Specialized barrier.
     */
    protected static class SyncBlockImpl implements SyncBlock {
        private Thread startingThread;
        private ThreadPoolExecutor threadPoolExecutor;
        private List<Runnable> runnableList = new LinkedList<>();

        SyncBlockImpl(ThreadPoolExecutor threadPoolExecutor) {
            this.startingThread = Thread.currentThread();
            this.threadPoolExecutor = threadPoolExecutor;
        }

        public void addToExecuteQueue(Runnable runnable) {
            ensureOpen();
            validate();
            runnableList.add(runnable);
        }

        public void executeAndWait() {
            ensureOpen();
            if (runnableList.size() == 0) {
                return;
            }
            try {
                validate();
                CountDownLatch syncCounter = new CountDownLatch(runnableList.size());
                for (Runnable runnable : runnableList) {
                    RunnableAdapter runnableAdapter = new RunnableAdapter(runnable, syncCounter);
                    threadPoolExecutor.execute(runnableAdapter);
                }
                syncCounter.await();
            } catch (InterruptedException e) {
                // Just get out
            } finally {
                runnableList = null;
            }
        }

        private void ensureOpen() {
            if (runnableList == null) {
                throw new RuntimeException("Attempt to execute executed block");
            }
        }


        public int size() {
            return runnableList.size();
        }

        private void validate() {
            Thread currentThread = Thread.currentThread();
            if (startingThread != currentThread) {
                throw new RuntimeException(
                        "Block was opened by thread [" +
                                startingThread +
                                "] but accessed now in thread [" +
                                currentThread +
                                "]" +
                                ". Block must be accessed in the same thread it was opened");
            }
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                    "{" +
                    "startingThread=" + startingThread +
                    ", threadPoolExecutor=" + threadPoolExecutor +
                    ", runnableList=" + runnableList +
                    '}';
        }
    }
}
