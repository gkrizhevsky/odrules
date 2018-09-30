package com.objective.dynamics.odrules.traverser;

import static com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl.assertNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.odrules.Rule;
import com.objective.dynamics.odrules.RuleTraverseListener;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:56 PM
 */
public class TraversingRuleRunnable implements TraversingRunnable {

    private Logger logger = Logger.getLogger(getClass().getName());


    private ExceptionHandler exceptionHandler;
    private Rule rule;
    private RuleTraverseListener listener;
    private int level;
    private AtomicBoolean stopRequested = new AtomicBoolean(false);
    private volatile boolean stopped;

    public TraversingRuleRunnable(ExceptionHandler exceptionHandler,
                                  Rule rule,
                                  RuleTraverseListener listener,
                                  int level) {
        assertNotNull("exceptionHandler", exceptionHandler);
        assertNotNull("rule", rule);
        assertNotNull("listener", listener);
        this.exceptionHandler = exceptionHandler;
        this.rule = rule;
        this.listener = listener;
        this.level = level;
    }

    @Override
    public void run() {
        getLogger().info("Enter " + getClass().getSimpleName() + ":run for rule: " + rule);
        try {
            if (stopRequested.get()) {
                getLogger().info("Stop stop was requested: no execution will be performed.");
                return;
            }

            if (exceptionHandler != null && exceptionHandler.hasExceptions()) {
                getLogger().info("Exceptions detected: runnable will stop.");
                return;
            }
            try {
                listener.traverseRule(rule, level);
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE, "Cannot run rule", e);
                exceptionHandler.addException(e);
            }
        } finally {
            stopped = true;

            getLogger().info("Exit " + getClass().getSimpleName() + ":run for rule: " + rule.getName());
        }
    }


    public boolean isStopped() {
        return stopped;
    }

    @Override
    public boolean isStopRequested() {
        return stopRequested.get();
    }

    @Override
    public void requestStop() {
        if (!stopped && !stopRequested.get()) {
            getLogger().info("Enter " + getClass().getSimpleName() + ":requestStop");
            listener.stopRequested(rule, level);
            this.stopRequested.set(true);
            rule.requestStop();
            getLogger().info("Exit " + getClass().getSimpleName() + ":requestStop");
        }
    }


    public Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "{" +
                "exceptionHandler=" + exceptionHandler +
                ", rule=" + rule +
                ", listener=" + listener +
                ", level=" + level +
                ", stopRequested=" + stopRequested +
                ", stopped=" + stopped +
                '}';
    }
}
