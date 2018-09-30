package com.objective.dynamics.odrules;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:01 PM
 */
public interface RuleItem {
    RuleContext getRuleContext();

    void setRuleContext(RuleContext ruleContext);

    boolean isRule();

    boolean isContainer();

    String getName();

    void setName(String name);

    RuleContainer getParentContainer();

    void setParentContainer(RuleContainer parentContainer);

    boolean isRootContainer();

    RuleContainer getRootContainerSafe();


    RuleContainer getRootContainer();

    boolean isStopRequested();

    void requestStop();
}
