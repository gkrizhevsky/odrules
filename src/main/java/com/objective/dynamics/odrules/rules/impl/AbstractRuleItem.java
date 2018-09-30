package com.objective.dynamics.odrules.rules.impl;


import static com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl.assertNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.objective.dynamics.odrules.Rule;
import com.objective.dynamics.odrules.RuleContainer;
import com.objective.dynamics.odrules.RuleContext;
import com.objective.dynamics.odrules.RuleItem;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:09 PM
 */
public abstract class AbstractRuleItem implements RuleItem {

    private static final Logger logger = Logger.getLogger(AbstractRuleItem.class.getName());

    private RuleContainer parentContainer;
    private String name;
    private RuleContext ruleContext;

    private AtomicBoolean stopRequested = new AtomicBoolean(false);

    protected AbstractRuleItem() {
    }

    protected AbstractRuleItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public RuleContainer getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(RuleContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    @Override
    public RuleContext getRuleContext() {
        return ruleContext;
    }

    @Override
    public void setRuleContext(RuleContext ruleContext) {
        this.ruleContext = ruleContext;
    }

    @Override
    public boolean isRule() {
        return this instanceof Rule;
    }

    @Override
    public boolean isContainer() {
        return this instanceof RuleContainer;
    }

    @Override
    public boolean isRootContainer() {
        return isContainer() && parentContainer == null;
    }


    @Override
    public RuleContainer getRootContainerSafe() {
        RuleContainer rootContainer = getRootContainer();
        assertNotNull("rootContainer", rootContainer);
        return rootContainer;
    }

    @Override
    public RuleContainer getRootContainer() {
        RuleContainer rootContainer;
        RuleContainer currentContainer = isContainer() ? ((RuleContainer) this) : this.getParentContainer();
        while (true) {
            if (currentContainer.isRootContainer()) {
                rootContainer = currentContainer;
                break;
            } else {
                currentContainer = currentContainer.getParentContainer();
            }
        }
        return rootContainer;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "{" +
                toString0() +
                '}';
    }


    @Override
    public boolean isStopRequested() {
        if (isRootContainer()) {
            return stopRequested.get();
        } else {
            RuleContainer rootContainer = this.getRootContainerSafe();
            return rootContainer.isStopRequested();
        }
    }

    @Override
    public void requestStop() {
        this.stopRequested.set(true);
        if (!this.isRootContainer()) {
            RuleContainer rootContainer = this.getRootContainerSafe();
            rootContainer.requestStop();
        }
    }

    public static Logger getLogger() {
        return logger;
    }


    protected String toString0() {
        RuleContainer rootContainer = getRootContainer();
        return "rootContainer name=" + (rootContainer == null ? "" : rootContainer.getName()) +
                ", parentContainer name=" + (parentContainer == null ? "" : parentContainer.getName()) +
                ", name='" + name + '\'' +
                ", stopRequested='" + stopRequested + '\'' +
                ", ruleContext=" + ruleContext;
    }
}
