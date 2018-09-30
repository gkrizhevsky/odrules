package com.objective.dynamics.odrules.rules.impl;

import java.util.LinkedHashMap;
import java.util.Objects;

import com.objective.dynamics.odrules.RuleContainer;
import com.objective.dynamics.odrules.RuleContext;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:07 PM
 */
public class SimpleRuleContext implements RuleContext {

    private LinkedHashMap<String, Object> items = new LinkedHashMap<>();

    private RuleContainer ruleContainer;

    private SimpleRuleContext() {
    }

    public RuleContainer withRootContainer() {
        return ruleContainer;
    }

    public RuleContainer setRuleContainer(String name, int numberOfThreads) {
        this.ruleContainer = RuleContainerFactory.newContainer(name, numberOfThreads);
        this.ruleContainer.setRuleContext(this);

        return ruleContainer;
    }

    public RuleContext addItem(String name, Object item) {
        items.put(name, item);
        return this;
    }

    public <T> T getItem(String name) {
        return (T) items.get(name);
    }


    public static class Builder {
        private SimpleRuleContext simpleRuleContext = new SimpleRuleContext();

        Builder setRootContainer(String name, int numberOfThreads) {
            simpleRuleContext.setRuleContainer(name, numberOfThreads);
            return this;
        }


        public SimpleRuleContext build() {

            Objects.requireNonNull(simpleRuleContext.withRootContainer(), "RootContainer may not be null");

            return simpleRuleContext;
        }
    }
}
