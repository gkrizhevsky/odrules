package com.objective.dynamics.odrules;


/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:59 PM
 */
public interface RuleTraverser {
    void traverse(RuleContainer container, RuleTraverseListener listener);

    void stopNow(RuleTraverseListener listener);

    boolean isInterrupted();
}
