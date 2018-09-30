package com.objective.dynamics.odrules.rules.impl;


import java.util.*;
import java.util.logging.Level;

import com.objective.dynamics.components.exception.ExceptionHandler;
import com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl;
import com.objective.dynamics.odrules.*;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:50 PM
 */
public abstract class AbstractRuleContainer extends AbstractRuleItem implements RuleContainer {

    private List<RuleItem> ruleItems = new ArrayList<>();

    private LinkedHashMap<String, RuleContainer> containerMap;
    private LinkedHashMap<String, Rule> ruleMap;
    private ExceptionHandler<Throwable> exceptionHandler = new ExceptionHandlerImpl();
    private ExecutionType executionType;

    protected AbstractRuleContainer(String name, ExecutionType executionType) {
        super(name);
        this.executionType = executionType;
    }

    protected AbstractRuleContainer(ExecutionType executionType) {
        this.executionType = executionType;
    }

    @Override
    public RuleContainer addRule(Rule rule) {
        setupItem(rule);
        addToContainerMap(rule);
        return this;
    }

    @Override
    public RuleContainer addContainer(RuleContainer container) {
        setupItem(container);
        addToContainerMap(container);
        return this;
    }

    private void setupItem(RuleItem container) {
        container.setParentContainer(this);
        container.setRuleContext(getRuleContext());
        ruleItems.add(container);
    }

    protected synchronized void addToContainerMap(RuleContainer container) {
        ExceptionHandlerImpl.assertNotNull("container", container);
        String containerName = container.getName();
        AbstractRuleContainer rootContainer = (AbstractRuleContainer) getRootContainerSafe();
        Map<String, RuleContainer> rootContainerMap = lazyRootContainerMap();
        if (rootContainerMap.containsKey(containerName)) {
            RuleValidationRuntimeException exception = new RuleValidationRuntimeException("Root container '" +
                    getName() +
                    "' already contains child container with name '" +
                    containerName +
                    "'");
            rootContainer.exceptionHandler.addException(exception);
        }
        rootContainerMap.put(containerName, container);
    }

    private Map<String, RuleContainer> lazyRootContainerMap() {
        AbstractRuleContainer rootContainer = (AbstractRuleContainer) getRootContainerSafe();
        if (rootContainer != null) {
            if (rootContainer.containerMap == null) {
                rootContainer.containerMap = new LinkedHashMap<>();
            }
            return rootContainer.containerMap;
        }
        return Collections.emptyMap();
    }

    protected synchronized void addToContainerMap(Rule rule) {
        ExceptionHandlerImpl.assertNotNull("rule", rule);
        Map<String, Rule> rootRuleMap = lazyRootRuleMap();
        AbstractRuleContainer rootContainer = (AbstractRuleContainer) getRootContainerSafe();
        String ruleName = rule.getName();
        if (rootRuleMap.containsKey(ruleName)) {
            RuleValidationRuntimeException exception = new RuleValidationRuntimeException("Root container '" +
                    getName() +
                    "' already contains child rule with name '" +
                    ruleName +
                    "'");

            rootContainer.exceptionHandler.addException(exception);
        }
        rootRuleMap.put(ruleName, rule);
    }

    private Map<String, Rule> lazyRootRuleMap() {
        AbstractRuleContainer rootContainer = (AbstractRuleContainer) getRootContainerSafe();
        if (rootContainer != null) {
            if (rootContainer.ruleMap == null) {
                rootContainer.ruleMap = new LinkedHashMap<>();
            }
            return rootContainer.ruleMap;
        }
        return Collections.emptyMap();
    }


    @Override
    public void validate() {
        if (isRootContainer() && exceptionHandler.hasExceptions()) {
            String errorMessage = "Validation failed: number of exceptions: " + exceptionHandler.size();
            StringBuilder errors =
                    new StringBuilder(errorMessage);
            errors.append("\n");
            getLogger().log(Level.SEVERE, errorMessage);
            for (Throwable throwable : exceptionHandler) {
                getLogger().log(Level.SEVERE, " Validation error" + throwable.getMessage());
                errors.append(throwable.getMessage()).append("\n");
            }
            throw new RuleValidationRuntimeException(errors.toString());
        }
    }

    public Iterator<Throwable> validationExceptions() {
        if (exceptionHandler != null) {
            return exceptionHandler.iterator();
        }
        return null;
    }

    @Override
    public List<RuleItem> getRuleItems() {
        return ruleItems;
    }


    @Override
    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public ExceptionHandler<Throwable> getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public ExceptionHandler<Throwable> getRootExceptionHandler() {
        return ((AbstractRuleContainer) getRootContainerSafe()).getExceptionHandler();
    }


}
