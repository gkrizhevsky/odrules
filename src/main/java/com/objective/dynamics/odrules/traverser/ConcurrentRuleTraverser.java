package com.objective.dynamics.odrules.traverser;

import java.util.ArrayList;
import java.util.List;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.odrules.Rule;
import com.objective.dynamics.odrules.RuleContainer;
import com.objective.dynamics.odrules.RuleTraverseListener;
import com.objective.dynamics.odrules.RuleTraverser;


/**
 * Creates appropriate concurrent processes based on container type.
 *
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:09 PM
 */
public class ConcurrentRuleTraverser extends AbstractRuleTraverser implements RuleTraverser {


    private RuleConcurrencyHandlerFactory concurrencyHandlerFactory = new RuleConcurrencyHandlerFactoryImpl();

    private final List<ConcurrencyHandler> handlers = new ArrayList<>();

    @Override
    protected void onStartTraversing(RuleContainer container) {
        getLogger().info(" &&& Started Traversing container: " + container.getName()
                + "; name: " + container.getName());
        super.onStartTraversing(container);
        synchronized (handlers) {
            for (ConcurrencyHandler handler : handlers) {
                handler.stopNow(); // To prevent possible thread leak
            }
            handlers.clear();
        }
        getLogger().info("Ended Traversing container: " + container.getName()
                + "; name: " + container.getName());
    }

    @Override
    protected void onEndTraversing(RuleContainer container) {
        synchronized (handlers) {
            for (ConcurrencyHandler handler : handlers) {
                if (handler.isRoot()) {
                    handler.execute();
                }
            }
        }
    }

    protected ConcurrencyHandler getConcurrencyHandler(RuleContainer container) {
        ConcurrencyHandler concurrencyHandler = concurrencyHandlerFactory.concurrencyHandler(container, container.getExecutionType());
        handlers.add(concurrencyHandler);
        return concurrencyHandler;
    }

    protected void onSuccessfulEndTraversingContainer(ConcurrencyHandler concurrencyHandler, int level) {
    }

    protected void onTraversingRule(ConcurrencyHandler concurrencyHandler,
                                    ExceptionHandler containerExceptionHandler,
                                    Rule rule,
                                    RuleTraverseListener listener,
                                    int level) {
        TraversingRuleRunnable ruleRunnable =
                new TraversingRuleRunnable(concurrencyHandler.getExceptionHandler(), rule, listener, level);
        concurrencyHandler.addRuleRunnable(ruleRunnable);
    }

    protected void beforeStartTraversingContainer(ConcurrencyHandler concurrencyHandler) {
    }


    @Override
    public void stopNow(RuleTraverseListener listener) {
        List<ConcurrencyHandler> handlers = new ArrayList<>(this.handlers);
        for (ConcurrencyHandler concurrencyHandler : handlers) {
            stopRequested(concurrencyHandler, listener);
            concurrencyHandler.stopNow();
        }
    }


    public void stopByRuleName(String ruleName, RuleTraverseListener listener) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":stopByRuleName");

        List<ConcurrencyHandler> handlers = new ArrayList<>(this.handlers);

        boolean found = false;
        getLogger().info(" handlers size: " + handlers.size());
        for (ConcurrencyHandler concurrencyHandler : handlers) {
            getLogger().info(" Stopping concurrencyHandler: " + concurrencyHandler.getContainerName() + "; 1st ruleName: " + ruleName);
            if (found) { // Stop all the consequent ones
                stopRequested(concurrencyHandler, listener);
                concurrencyHandler.stop();
            } else if (ruleName != null && ruleName.equals(concurrencyHandler.getContainerName())) {
                found = true;
                concurrencyHandler.stop();
                stopRequested(concurrencyHandler, listener);
            }
        }
        if (!found) {
            getLogger().info(" No concurrencyHandlers found by ruleName " + ruleName);
        }
    }


    private void stopRequested(ConcurrencyHandler concurrencyHandler, RuleTraverseListener listener) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":stopRequested");
        listener.stopRequested(concurrencyHandler.getRuleContainer(), 0);
        super.stopRequested();
        getLogger().info("Exit " + getClass().getSimpleName() + ":stopRequested");
    }

}
