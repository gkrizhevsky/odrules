package com.objective.dynamics.odrules;


/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:17 PM
 */
public interface RuleTraverseListener {

    void startTraversingContainer(RuleContainer container, int level);

    void endTraversingContainer(RuleContainer container, Throwable exception, int level);

    void traverseRule(Rule rule, int level);

    void stopRequested(RuleItem ruleItem, int level);

}
