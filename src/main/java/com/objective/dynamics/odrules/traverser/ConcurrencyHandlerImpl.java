package com.objective.dynamics.odrules.traverser;

import static com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl.assertNotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.components.thread.SyncBlock;
import com.objective.dynamics.components.thread.SyncThreadPool;
import com.objective.dynamics.components.thread.ThreadPoolFactory;
import com.objective.dynamics.components.thread.impl.DefaultThreadPoolFactory;
import com.objective.dynamics.odrules.RuleContainer;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:50 PM
 */
public class ConcurrencyHandlerImpl implements ConcurrencyHandler {

    private Logger logger = Logger.getLogger(getClass().getName());

    private int maxNumberOfThreadsPerContainer;
    private RuleContainer container;
    private ExceptionHandler exceptionHandler;

    private SyncThreadPool threadPool;
    private SyncBlock syncBlock;
    private List<TraversingRunnable> runnables = new LinkedList<>();

    private ThreadPoolFactory threadPoolFactory = new DefaultThreadPoolFactory(0);

    ConcurrencyHandlerImpl(int maxNumberOfThreadsPerContainer, RuleContainer container) {
        assertNotNull("ruleContainer", container);
        this.maxNumberOfThreadsPerContainer = maxNumberOfThreadsPerContainer;
        this.container = container;
        this.exceptionHandler = container.getRootExceptionHandler();
        this.threadPool = threadPoolFactory.newSyncThreadPool(maxNumberOfThreadsPerContainer, container.getName());
    }


    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    private void begin() {
        if (syncBlock != null) {
            throw new RuntimeException("Attempt to re-run beginSyncBlock for container:" + container.getName());
        }
        syncBlock = threadPool.beginSyncBlock();
        String containerName = container.getName();
        getLogger().info("Started sync block for container: " + containerName + "; " +
                " is root ?: " + container.isRootContainer() +
                "; threadPool: " + threadPool);
    }

    @Override
    public void addRuleRunnable(TraversingRuleRunnable ruleRunnable) {
        runnables.add(ruleRunnable);
    }

    @Override
    public void addContainerRunnable(TraversingContainerRunnable containerRunnable) {
        if (getRuleContainerSafe().isStopRequested()) {
            getLogger().info("Stop requested - addChild() exit: " + getRuleContainerSafe().getName());
            return;
        }
        runnables.add(containerRunnable);
    }

    @Override
    public boolean isRoot() {
        return getRuleContainer().isRootContainer();
    }


    @Override
    public void execute() {
        if (isRoot()) { // Only root is supposed to start execution
            executeSyncBlock();
        }
    }

    /**
     * Note that the execution is recursive: in executeSyncBlock() child TraversingContainerRunnable-s
     * are added to the corresponding
     * syncBlock which, in turn, will call executeSyncBlock() in child's TraversingContainerRunnable.run() method
     */
    @Override
    public void executeSyncBlock() {
        begin();

        if (hasRunnables()) {
            for (TraversingRunnable runnable : runnables) {
                syncBlock.addToExecuteQueue(runnable);
            }
        }
        executeAndWait();
        if (exceptionHandler.hasExceptions()) {
            Throwable t = exceptionHandler.getFirstException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException("Cannot execute handler: " + getContainerName(), t);
            }
        }
    }

    private void executeAndWait() {
        String containerName = container.getName();
        getLogger().info("Started to execute and wait sync block for container: " + containerName);
        getSyncBlockSafe().executeAndWait();
        getLogger().info("Ended to execute and wait sync block for container: " + containerName);
    }

    @SuppressWarnings("unused")
    public int getMaxNumberOfThreadsPerContainer() {
        return maxNumberOfThreadsPerContainer;
    }

    @SuppressWarnings("unused")
    public void setMaxNumberOfThreadsPerContainer(int maxNumberOfThreadsPerContainer) {
        if (maxNumberOfThreadsPerContainer >= 0) {
            this.maxNumberOfThreadsPerContainer = maxNumberOfThreadsPerContainer;
        }
    }


    @Override
    public boolean isRootContainerHandler() {
        return container != null && container.isRootContainer();
    }

    @Override
    public String getContainerName() {
        return container == null ? null : container.getName();
    }

    @Override
    public RuleContainer getRuleContainer() {
        return container;
    }

    private RuleContainer getRuleContainerSafe() {
        assertNotNull("container", container);
        return container;
    }

    @SuppressWarnings("unused")
    public String getSummary() {
        if (maxNumberOfThreadsPerContainer > 0) {
            return "Multi-threaded (" +
                    maxNumberOfThreadsPerContainer +
                    ")";
        } else {
            return "Single-threaded(0)";
        }
    }

    @Override
    public void stopNow() {
        if (threadPool != null) {
            threadPool.stopNow();
        }
    }

    @Override
    public void stop() {
        getLogger().info("Enter " + getClass().getSimpleName() + ":stopByContainerName: " + getContainerName()
                + "; getContainerName: " + getContainerName());
        if (container != null) {
            container.requestStop();
        }
        for (TraversingRunnable runnable : runnables) {
            runnable.requestStop();
        }
        getLogger().info("Exit " + getClass().getSimpleName() + ":stopByContainerName");
    }

    @Override
    public boolean isStopRequested() {
        return container != null && container.isStopRequested();
    }


    private boolean hasRunnables() {
        return !runnables.isEmpty();
    }

    private SyncBlock getSyncBlockSafe() {
        assertNotNull("syncBlock for container: " + getContainerName(), syncBlock);
        return syncBlock;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "{" +
                "threadPoolFactory=" + threadPoolFactory +
                ", maxNumberOfThreadsPerContainer=" + maxNumberOfThreadsPerContainer +
                ", container=" + container +
                ", threadPool=" + threadPool +
                ", syncBlock=" + syncBlock +
                '}';
    }

}
