package com.objective.dynamics.odrules.rules.impl;

import static com.objective.dynamics.util.string.StringUtil.nvl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl;
import com.objective.dynamics.odrules.*;
import com.objective.dynamics.odrules.traverser.ConcurrentRuleTraverser;
import com.objective.dynamics.util.string.StringUtil;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:55 PM
 */
public class RuleManagerImpl implements RuleManager, RuleTraverseListener {

    private static final Logger logger = Logger.getLogger(RuleManagerImpl.class.getName());


    private RuleTraverser traverser = new ConcurrentRuleTraverser();
    private ExecutionPath executionPath = new ExecutionPath();

    @Override
    public void execute(RuleContext context) {

        final RuleContainer container = context.withRootContainer();
        runContainer(container, new ExceptionHandlerImpl());

    }


    protected boolean runContainer(RuleContainer container, ExceptionHandler exceptionHandler) {
        getLogger().info("===========================================================================================");
        getLogger().info(" container: " + container.getName());
        getLogger().info("-------------------------------------------------------------------------------------------");

        try {
            return traverse(container);
        } catch (Throwable e) {
            getLogger().log(Level.SEVERE, "runContainer failed for container: " + container.getName(), e);
            exceptionHandler.addException(e);
        }
        return false;
    }

    protected boolean traverse(RuleContainer container) {
        try {

            traverser.traverse(container, this);
            return traverser.isInterrupted();
        } catch (Exception e) {
            handleException(container, e);
        }
        return false;
    }


    @Override
    public void startTraversingContainer(RuleContainer container, int level) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":startTraversingContainer: " + container.getName() + "; level: " + level);

        if (container.isStopRequested()) {
            getLogger().info("Execution of container " + container.getName() + " skipped stopped because stop is requested ");
            return;
        }

        getLogger().info("Exit " + getClass().getSimpleName() + ":startTraversingContainer " + container.getName() + "; level: " + level);
    }

    @Override
    public void endTraversingContainer(RuleContainer container, Throwable exception, int level) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":endTraversingContainer: level: " + level
                + "; container name: " + container.getName());
        if (exception != null) {
            handleException(container, exception);
        }
        getLogger().info("Exit " + getClass().getSimpleName() + ":endTraversingContainer: level: " + level);
    }

    @Override
    public void traverseRule(Rule rule, int level) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":traverseRule");

        if (rule.isStopRequested()) {
            getLogger().info("Execution of rule " + rule.getName() + " skipped stopped because stop is requested ");
            return;
        }

        Throwable exception = null;
        try {
            runRule(rule, level);
        } catch (Throwable e) {
            exception = e;
            String message = "Cannot start traversing rule";
            getLogger().log(Level.SEVERE, message, e);
            handleException(message, rule, e);
        } finally {
            endTraversingRule(rule, exception);
        }
        getLogger().info("Exit " + getClass().getSimpleName() + ":traverseRule");
    }

    protected void endTraversingRule(Rule rule, Throwable exception) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":endTraversingRule");

        handleException("", rule, exception);
        getLogger().info("Exit " + getClass().getSimpleName() + ":endTraversingRule");
    }


    protected void runRule(Rule rule, int level) {
        getLogger().info("Enter " + getClass().getSimpleName() + ":runRule: " + rule.getName());
        if (!rule.isStopRequested()) {

            String ruleName = rule.getName();


            String parentContainerName = rule.getParentContainer().getName();
            String message = "[" + Thread.currentThread().getName() + "]:" +
                    parentContainerName + ":" +
                    rule.getParentContainer().getExecutionType().getType() + ": " + ruleName;
            String path = StringUtil.lpad(message, ' ', message.length() + level * 4);

            executionPath.addPath(path);

            rule.execute();
        }
        getLogger().info("Exit " + getClass().getSimpleName() + ":runRule: " + rule.getName());
    }

    private void handleException(RuleContainer container, Throwable e) {
        if (e != null) {
            String msg = "Traversing container " + container + " failed";
            getLogger().log(Level.SEVERE, msg, e);
            if (e instanceof RuleRuntimeException) {
                throw ((RuleRuntimeException) e);
            } else {
                throw new RuleRuntimeException(msg, e);
            }
        }
    }

    private void handleException(String prependMessage, Rule rule, Throwable e) {
        if (e != null) {
            prependMessage = nvl(prependMessage, "");
            if (e instanceof RuleRuntimeException) {
                throw ((RuleRuntimeException) e).setRule(rule).setPrependMessage(prependMessage);
            } else {
                throw new RuleRuntimeException(e).setRule(rule).setPrependMessage(prependMessage);
            }
        }
    }

    @Override
    public void stopRequested(RuleItem ruleItem, int level) {

    }

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public ExecutionPath getExecutionPath() {
        return executionPath;
    }
}
