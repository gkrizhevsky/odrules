package com.objective.dynamics.odrules.traverser;


import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl;
import com.objective.dynamics.odrules.RuleContainer;

/**
 * Concurrency handler should be created one per rule container
 *
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:49 PM
 */
public interface ConcurrencyHandler {
    NullConcurrencyHandler CONCURRENCY_HANDLER = new NullConcurrencyHandler();


    ExceptionHandler getExceptionHandler();

    void addRuleRunnable(TraversingRuleRunnable ruleRunnable);

    void addContainerRunnable(TraversingContainerRunnable containerRunnable);

    boolean isRoot();

    void execute();

    /**
     * To only be run from the child TraversingContainerRunnable
     * <p/>
     * Note that the execution is recursive: in executeSyncBlock() child TraversingContainerRunnable-s are added to the
     * syncBlock which, in turn, will call executeSyncBlock() in child's TraversingContainerRunnable.run() method
     */
    void executeSyncBlock();

    void stopNow();

    void stop();

    String getContainerName();

    boolean isRootContainerHandler();

    RuleContainer getRuleContainer();

    boolean isStopRequested();

    class NullConcurrencyHandler implements ConcurrencyHandler {

        private ExceptionHandler exceptionHandler = new ExceptionHandlerImpl();

        @Override
        public ExceptionHandler getExceptionHandler() {
            return exceptionHandler;
        }

        @Override
        public void stopNow() {
        }

        @Override
        public void stop() {
        }

        @Override
        public String getContainerName() {
            return "Null";
        }

        @Override
        public boolean isRootContainerHandler() {
            return false;
        }

        @Override
        public RuleContainer getRuleContainer() {
            return null;
        }

        @Override
        public void addContainerRunnable(TraversingContainerRunnable containerRunnable) {
        }

        @Override
        public boolean isRoot() {
            return false;
        }

        @Override
        public void execute() {
        }

        @Override
        public void executeSyncBlock() {
        }

        @Override
        public void addRuleRunnable(TraversingRuleRunnable ruleRunnable) {
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }
    }
}
