package com.objective.dynamics.odrules.traverser;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl;
import com.objective.dynamics.odrules.*;


/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:09 PM
 */
public abstract class AbstractRuleTraverser implements RuleTraverser {

    private Logger logger = Logger.getLogger(getClass().getName());

    private AtomicBoolean interrupted = new AtomicBoolean(false);

    @Override
    public final void traverse(RuleContainer container, RuleTraverseListener listener) {
        interrupted.set(false);
        RuntimeException exception = null;
        int level = 0;
        onStartTraversing(container);
        try {
            traverseContainer(container, null, listener, level);
            onEndTraversing(container);
        } catch (RuntimeException e) {
            exception = e;
            throw e;
        } finally {
            if (listener instanceof RuleExecutionListener) {
                ((RuleExecutionListener) listener).onEndExecute(container, exception);
            }
        }
    }

    protected void onEndTraversing(RuleContainer container) {
    }

    protected final void traverseContainer(RuleContainer container, ConcurrencyHandler parentConcurrencyHandler, RuleTraverseListener listener, int level) {
        ExceptionHandlerImpl.assertNotNull("listener", listener);
        Throwable exception = null;
        level++;

        ConcurrencyHandler concurrencyHandler = getConcurrencyHandler(container);
        ExceptionHandler containerInternalExceptionHandler = concurrencyHandler.getExceptionHandler();
        if (parentConcurrencyHandler != null) {
            parentConcurrencyHandler.addContainerRunnable(new TraversingContainerRunnable(containerInternalExceptionHandler, concurrencyHandler, level));
        }
        //
        // Begin sync block as needed:
        //
        beforeStartTraversingContainer(concurrencyHandler);

        onStartTraversingContainer(container, listener, level);
        try {
            List<RuleItem> ruleItems = container.getRuleItems();
            for (RuleItem ruleItem : ruleItems) {
                if (ruleItem.isRule()) {
                    Rule rule = (Rule) ruleItem;
                    onTraversingRule(concurrencyHandler, containerInternalExceptionHandler, rule, listener, level);
                } else if (ruleItem.isContainer()) {
                    RuleContainer childContainer = (RuleContainer) ruleItem;
                    traverseContainer(childContainer, concurrencyHandler, listener, level);
                } else {
                    throw new RuntimeException("Unsupported ruleItem type: " + ruleItem);
                }
            }
            //
            // End sync block as needed:
            //
            onSuccessfulEndTraversingContainer(concurrencyHandler, level);

            handleInternalContainerExceptions(containerInternalExceptionHandler, container);
        } catch (Throwable e) {
            exception = e;
            getLogger().log(Level.SEVERE, "Cannot traverseContainer: " + container, e);
            throw new RuleRuntimeException("Cannot traverseContainer", e).setRuleContainer(container);
        } finally {
            onEndTraversingContainer(container, listener, concurrencyHandler, level, exception);
        }
    }

    protected void handleInternalContainerExceptions(ExceptionHandler containerInternalExceptionHandler, RuleContainer container) {
        if (containerInternalExceptionHandler.hasExceptions()) {
            Throwable firstException = containerInternalExceptionHandler.getFirstException();
            throw new RuleRuntimeException("Container Internal Exception", firstException).setRuleContainer(container);
        }
    }

    protected void onStartTraversing(RuleContainer container) {
    }

    protected abstract void beforeStartTraversingContainer(ConcurrencyHandler concurrencyHandler);

    protected void onStartTraversingContainer(RuleContainer container, RuleTraverseListener listener, int level) {
        listener.startTraversingContainer(container, level);
    }

    protected abstract ConcurrencyHandler getConcurrencyHandler(RuleContainer container);

    protected abstract void onTraversingRule(ConcurrencyHandler concurrencyHandler,
                                             ExceptionHandler containerExceptionHandler,
                                             Rule rule,
                                             RuleTraverseListener listener,
                                             int level);

    protected abstract void onSuccessfulEndTraversingContainer(ConcurrencyHandler concurrencyHandler, int level);

    protected void onEndTraversingContainer(RuleContainer container, RuleTraverseListener listener, ConcurrencyHandler concurrencyHandler, int level, Throwable exception) {
        listener.endTraversingContainer(container, exception, level);
    }

    protected void stopRequested() {
        interrupted.set(true);
    }

    @Override
    public boolean isInterrupted() {
        return interrupted.get();
    }

    public final Logger getLogger() {
        return logger;
    }
}
