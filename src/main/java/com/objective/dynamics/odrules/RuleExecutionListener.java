package com.objective.dynamics.odrules;


/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:27 PM
 */
public interface RuleExecutionListener extends RuleTraverseListener {
    void onEndExecute(RuleContainer container, Throwable exception);
}
